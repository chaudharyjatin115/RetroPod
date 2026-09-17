package com.maxrave.simpmusic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maxrave.domain.data.entities.LocalPlaylistEntity
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.searchResult.playlists.PlaylistsResult
import com.maxrave.domain.data.model.searchResult.songs.SongsResult
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.mediaservice.handler.DownloadHandler
import com.maxrave.domain.mediaservice.handler.SimpleMediaState
import com.maxrave.domain.repository.LocalPlaylistRepository
import com.maxrave.domain.repository.PlaylistRepository
import com.maxrave.domain.repository.SongRepository
import com.maxrave.domain.utils.LocalResource
import com.maxrave.domain.utils.Resource
import com.maxrave.domain.utils.isLocal
import com.maxrave.domain.utils.toTrack
import com.maxrave.simpmusic.expect.ui.ScanLocalAudioEffect
import com.maxrave.simpmusic.ui.ipod.HardwareFinish
import com.maxrave.simpmusic.ui.ipod.IpodHardwareShell
import com.maxrave.simpmusic.ui.ipod.IpodHardwareTheme
import com.maxrave.simpmusic.ui.ipod.IpodMenuItem
import com.maxrave.simpmusic.ui.ipod.IpodNavigationEngine
import com.maxrave.simpmusic.ui.ipod.IpodPresets
import com.maxrave.simpmusic.ui.ipod.IpodScreen
import com.maxrave.simpmusic.ui.ipod.LcdTheme
import com.maxrave.simpmusic.viewModel.LibraryViewModel
import com.maxrave.simpmusic.viewModel.SearchViewModel
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import com.maxrave.simpmusic.viewModel.SharedViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val sharedViewModel: SharedViewModel = koinInject()
    val searchViewModel: SearchViewModel = koinViewModel()
    val libraryViewModel: LibraryViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val songRepository: SongRepository = koinInject()
    val playlistRepository: PlaylistRepository = koinInject()
    val localPlaylistRepository: LocalPlaylistRepository = koinInject()
    val downloadHandler: DownloadHandler = koinInject()
    val dataStoreManager: DataStoreManager = koinInject()

    val scope = rememberCoroutineScope()
    val navEngine = remember { IpodNavigationEngine() }

    val savedHardwareThemeId by dataStoreManager.getString("ipod_hardware_theme").collectAsStateWithLifecycle(null)
    val savedLcdThemeName by dataStoreManager.getString("ipod_lcd_theme").collectAsStateWithLifecycle(null)
    val savedStickersPref by dataStoreManager.getString("ipod_stickers_enabled").collectAsStateWithLifecycle(null)

    var hardwareTheme by remember(savedHardwareThemeId) {
        mutableStateOf(IpodPresets.ALL_PRESETS.find { it.id == savedHardwareThemeId } ?: IpodPresets.FASHION_BARBIE)
    }
    var lcdTheme by remember(savedLcdThemeName) {
        mutableStateOf(runCatching { LcdTheme.valueOf(savedLcdThemeName ?: "") }.getOrDefault(LcdTheme.GLAM_BARBIE))
    }
    var stickersEnabled by remember(savedStickersPref) {
        mutableStateOf(savedStickersPref != "false")
    }

    var isHoldEnabled by remember { mutableStateOf(false) }

    ScanLocalAudioEffect { scannedSongs ->
        scope.launch {
            scannedSongs.forEach { song ->
                runCatching { songRepository.insertSong(song).first() }
            }
        }
    }

    val nowPlayingTrackState by sharedViewModel.nowPlayingState.collectAsStateWithLifecycle()
    val simpleMediaState by sharedViewModel.simpleMediaState.collectAsStateWithLifecycle()
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()
    val youtubeLoggedIn by sharedViewModel.getYouTubeLoggedIn().collectAsStateWithLifecycle("false")
    val cookie by dataStoreManager.cookie.collectAsStateWithLifecycle("")

    val isYoutubeLoggedIn = youtubeLoggedIn == "true" || youtubeLoggedIn == DataStoreManager.TRUE || cookie.isNotEmpty()

    val currentSong: Track? = nowPlayingTrackState.track ?: nowPlayingTrackState.songEntity?.toTrack()
    val isPlaying = controllerState.isPlaying
    val currentPositionMs = (simpleMediaState as? SimpleMediaState.Progress)?.progress ?: 0L
    val durationMs = (simpleMediaState as? SimpleMediaState.Ready)?.duration
        ?: (simpleMediaState as? SimpleMediaState.Loading)?.duration ?: 0L

    val searchState by searchViewModel.searchScreenState.collectAsStateWithLifecycle()
    val localSongs by songRepository.getAllSongs(100).collectAsStateWithLifecycle(emptyList())
    val likedSongs by songRepository.getLikedSongs().collectAsStateWithLifecycle(emptyList())
    val localPlaylists by localPlaylistRepository.getAllLocalPlaylists().collectAsStateWithLifecycle(emptyList())
    val youTubePlaylistsResource by libraryViewModel.youTubePlaylist.collectAsStateWithLifecycle()

    LaunchedEffect(navEngine.currentScreen) {
        if (navEngine.currentScreen is IpodScreen.Playlists) {
            libraryViewModel.getYouTubePlaylist()
        }
    }

    var currentPlaylistTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isPlaylistTracksLoading by remember { mutableStateOf(false) }

    val currentScreen = navEngine.currentScreen
    LaunchedEffect(currentScreen) {
        if (currentScreen is IpodScreen.PlaylistDetail) {
            isPlaylistTracksLoading = true
            currentPlaylistTracks = emptyList()
            playlistRepository.getFullPlaylistData(currentScreen.playlistId, "").collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        currentPlaylistTracks = resource.data?.tracks ?: emptyList()
                        isPlaylistTracksLoading = false
                    }
                    is Resource.Error -> {
                        isPlaylistTracksLoading = false
                    }
                }
            }
        } else if (currentScreen is IpodScreen.LocalPlaylistDetail) {
            isPlaylistTracksLoading = true
            currentPlaylistTracks = emptyList()
            val songEntities = localPlaylistRepository.getFullPlaylistTracks(currentScreen.playlistId)
            currentPlaylistTracks = songEntities.map { it.toTrack() }
            isPlaylistTracksLoading = false
        }
    }

    val currentItems = remember(
        navEngine.currentScreen,
        searchState,
        localSongs,
        likedSongs,
        localPlaylists,
        youTubePlaylistsResource,
        currentPlaylistTracks,
        isPlaylistTracksLoading,
        hardwareTheme,
        lcdTheme,
        stickersEnabled,
        isYoutubeLoggedIn,
        currentSong
    ) {
        buildIpodMenuItems(
            screen = navEngine.currentScreen,
            navEngine = navEngine,
            sharedViewModel = sharedViewModel,
            downloadHandler = downloadHandler,
            searchSongs = searchState.searchSongsResult,
            localSongs = localSongs,
            likedSongs = likedSongs,
            localPlaylists = localPlaylists,
            youTubePlaylistsResource = youTubePlaylistsResource,
            playlistTracks = currentPlaylistTracks,
            isPlaylistTracksLoading = isPlaylistTracksLoading,
            hardwareTheme = hardwareTheme,
            setHardwareTheme = { theme ->
                hardwareTheme = theme
                scope.launch { dataStoreManager.putString("ipod_hardware_theme", theme.id) }
            },
            lcdTheme = lcdTheme,
            setLcdTheme = { theme ->
                lcdTheme = theme
                scope.launch { dataStoreManager.putString("ipod_lcd_theme", theme.name) }
            },
            stickersEnabled = stickersEnabled,
            setStickersEnabled = { enabled ->
                stickersEnabled = enabled
                scope.launch { dataStoreManager.putString("ipod_stickers_enabled", if (enabled) "true" else "false") }
            },
            isYoutubeLoggedIn = isYoutubeLoggedIn,
            onSignOut = {
                scope.launch {
                    settingsViewModel.setUsedAccount(null)
                    navEngine.popScreen()
                }
            },
            scope = scope
        )
    }

    IpodHardwareShell(
        hardwareTheme = hardwareTheme,
        lcdTheme = lcdTheme,
        screen = navEngine.currentScreen,
        canGoBack = navEngine.navStack.size > 1,
        items = currentItems,
        selectedIndex = navEngine.selectedIndex,
        currentSong = currentSong,
        isPlaying = isPlaying,
        currentPositionMs = currentPositionMs,
        durationMs = durationMs,
        lyricsText = null,
        searchQuery = navEngine.searchQuery,
        isHoldEnabled = isHoldEnabled,
        stickersEnabled = stickersEnabled,
        onHoldToggle = { isHoldEnabled = !isHoldEnabled },
        onSearchQueryChange = { query ->
            navEngine.searchQuery = query
            searchViewModel.searchAll(query)
        },
        onYoutubeLoginDone = { cookieStr ->
            scope.launch {
                settingsViewModel.addAccount(cookieStr)
                navEngine.popScreen()
            }
        },
        onWheelScroll = { steps ->
            if (!isHoldEnabled) {
                navEngine.scrollSelection(steps, currentItems.size)
            }
        },
        onMenuClick = {
            if (!isHoldEnabled) {
                navEngine.popScreen()
            }
        },
        onSelectClick = {
            if (!isHoldEnabled && currentItems.isNotEmpty() && navEngine.selectedIndex in currentItems.indices) {
                currentItems[navEngine.selectedIndex].action()
            }
        },
        onPlayPauseClick = {
            if (!isHoldEnabled) {
                sharedViewModel.onUIEvent(SharedViewModel.UIEvent.PlayPause)
            }
        },
        onNextClick = {
            if (!isHoldEnabled) {
                sharedViewModel.onUIEvent(SharedViewModel.UIEvent.Next)
            }
        },
        onPrevClick = {
            if (!isHoldEnabled) {
                sharedViewModel.onUIEvent(SharedViewModel.UIEvent.Previous)
            }
        },
        modifier = Modifier
    )
}

private fun buildIpodMenuItems(
    screen: IpodScreen,
    navEngine: IpodNavigationEngine,
    sharedViewModel: SharedViewModel,
    downloadHandler: DownloadHandler,
    searchSongs: List<SongsResult>,
    localSongs: List<SongEntity>,
    likedSongs: List<SongEntity>,
    localPlaylists: List<LocalPlaylistEntity>,
    youTubePlaylistsResource: LocalResource<List<PlaylistsResult>>,
    playlistTracks: List<Track>,
    isPlaylistTracksLoading: Boolean,
    hardwareTheme: IpodHardwareTheme,
    setHardwareTheme: (IpodHardwareTheme) -> Unit,
    lcdTheme: LcdTheme,
    setLcdTheme: (LcdTheme) -> Unit,
    stickersEnabled: Boolean,
    setStickersEnabled: (Boolean) -> Unit,
    isYoutubeLoggedIn: Boolean,
    onSignOut: () -> Unit,
    scope: kotlinx.coroutines.CoroutineScope
): List<IpodMenuItem> {
    return when (screen) {
        is IpodScreen.Main -> listOf(
            IpodMenuItem("Music", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Music) },
            IpodMenuItem("Playlists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Playlists) },
            IpodMenuItem("Now Playing", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.NowPlaying) },
            IpodMenuItem("Search", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Search) },
            IpodMenuItem("Settings", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Settings) }
        )

        is IpodScreen.Music -> listOf(
            IpodMenuItem("Search YouTube Music", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Search) },
            IpodMenuItem("YouTube Playlists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Playlists) },
            IpodMenuItem("Local Playlists (${localPlaylists.size})", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LocalPlaylists) },
            IpodMenuItem("Liked Songs (${likedSongs.size})", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LikedSongs) },
            IpodMenuItem("Downloaded & Local Songs (${localSongs.size})", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LocalSongs) },
            IpodMenuItem("Now Playing", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.NowPlaying) }
        )

        is IpodScreen.Playlists -> {
            when (youTubePlaylistsResource) {
                is LocalResource.Loading -> listOf(IpodMenuItem("Loading playlists...", hasSubmenu = false) {})
                is LocalResource.Error -> listOf(IpodMenuItem("Error loading playlists", subtitle = "Sign in to view your playlists", hasSubmenu = false) {})
                is LocalResource.Success -> {
                    val playlists = youTubePlaylistsResource.data ?: emptyList()
                    if (playlists.isEmpty()) {
                        listOf(IpodMenuItem("No Playlists Found", subtitle = if (!isYoutubeLoggedIn) "Sign in to sync playlists" else "No saved playlists", hasSubmenu = false) {})
                    } else {
                        playlists.map { playlist ->
                            IpodMenuItem(playlist.title, subtitle = "${playlist.itemCount} tracks", hasSubmenu = true) {
                                navEngine.pushScreen(IpodScreen.PlaylistDetail(playlist.title, playlist.browseId))
                            }
                        }
                    }
                }
            }
        }

        is IpodScreen.LocalPlaylists -> {
            if (localPlaylists.isEmpty()) {
                listOf(IpodMenuItem("No Local Playlists", subtitle = "Create local playlists in library", hasSubmenu = false) {})
            } else {
                localPlaylists.map { localPl ->
                    IpodMenuItem(localPl.title, subtitle = "Local Playlist", hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.LocalPlaylistDetail(localPl.title, localPl.id))
                    }
                }
            }
        }

        is IpodScreen.PlaylistDetail, is IpodScreen.LocalPlaylistDetail -> {
            if (isPlaylistTracksLoading) {
                listOf(IpodMenuItem("Loading tracks...", hasSubmenu = false) {})
            } else if (playlistTracks.isEmpty()) {
                listOf(IpodMenuItem("No tracks found in playlist", hasSubmenu = false) {})
            } else {
                playlistTracks.map { track ->
                    val artistStr = track.artists?.joinToString(", ") { it.name } ?: ""
                    IpodMenuItem(track.title, subtitle = artistStr, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.TrackOptions(track, playlistTracks))
                    }
                }
            }
        }

        is IpodScreen.LikedSongs -> {
            if (likedSongs.isEmpty()) {
                listOf(IpodMenuItem("No Liked Songs Found", subtitle = "Like songs to see them here", hasSubmenu = false) {})
            } else {
                val tracks = likedSongs.map { it.toTrack() }
                likedSongs.map { songEntity ->
                    val artistStr = songEntity.artistName?.joinToString(", ") ?: ""
                    IpodMenuItem(songEntity.title, subtitle = artistStr, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.TrackOptions(songEntity.toTrack(), tracks))
                    }
                }
            }
        }

        is IpodScreen.LocalSongs -> {
            if (localSongs.isEmpty()) {
                listOf(IpodMenuItem("No Downloaded / Local Songs", subtitle = "Download tracks or add local audio files", hasSubmenu = false) {})
            } else {
                val tracks = localSongs.map { it.toTrack() }
                localSongs.map { songEntity ->
                    val artistStr = songEntity.artistName?.joinToString(", ") ?: ""
                    IpodMenuItem(songEntity.title, subtitle = artistStr, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.TrackOptions(songEntity.toTrack(), tracks))
                    }
                }
            }
        }

        is IpodScreen.Search -> {
            if (searchSongs.isEmpty()) {
                listOf(IpodMenuItem("Type above to search YouTube Music...", hasSubmenu = false) {})
            } else {
                val queueTracks = searchSongs.map { it.toTrack() }
                searchSongs.map { song ->
                    val track = song.toTrack()
                    val artistNames = song.artists?.joinToString(", ") { it.name } ?: ""
                    IpodMenuItem(song.title ?: "", subtitle = artistNames, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.TrackOptions(track, queueTracks))
                    }
                }
            }
        }

        is IpodScreen.TrackOptions -> {
            val items = mutableListOf<IpodMenuItem>()
            items.add(
                IpodMenuItem("Play Now", subtitle = "Start playback", hasSubmenu = false) {
                    sharedViewModel.playTrack(screen.track, screen.queue)
                    navEngine.pushScreen(IpodScreen.NowPlaying)
                }
            )
            val isLocalOrDownloaded = screen.track.isLocal() || localSongs.any { it.videoId == screen.track.videoId }
            if (!isLocalOrDownloaded) {
                items.add(
                    IpodMenuItem("Download", subtitle = "Save to local storage", hasSubmenu = false) {
                        scope.launch {
                            downloadHandler.downloadTrack(
                                videoId = screen.track.videoId,
                                title = screen.track.title,
                                thumbnail = screen.track.thumbnails?.lastOrNull()?.url.orEmpty()
                            )
                        }
                        navEngine.popScreen()
                    }
                )
            }
            items
        }

        is IpodScreen.Settings -> listOf(
            IpodMenuItem("Account Settings", subtitle = if (isYoutubeLoggedIn) "Logged In" else "Not Logged In", hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsLogin)
            },
            IpodMenuItem("Whimsical Stickers", subtitle = if (stickersEnabled) "Enabled 🎀" else "Disabled", hasSubmenu = false) {
                setStickersEnabled(!stickersEnabled)
            },
            IpodMenuItem("Shell Theme", subtitle = hardwareTheme.name, hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsHardwarePreset)
            },
            IpodMenuItem("Hardware Finish", subtitle = hardwareTheme.finishStyle.title, hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsFinishStyle)
            },
            IpodMenuItem("LCD Display Theme", subtitle = lcdTheme.title, hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsLcdTheme)
            }
        )

        is IpodScreen.SettingsLogin -> listOf(
            IpodMenuItem(
                title = if (isYoutubeLoggedIn) "Status: Logged In" else "Status: Guest Mode",
                subtitle = if (isYoutubeLoggedIn) "Connected to YouTube Music" else "Tap below to manage account",
                hasSubmenu = false
            ) {},
            IpodMenuItem(
                title = if (isYoutubeLoggedIn) "Sign Out / Logout" else "Sign In to YouTube Music",
                hasSubmenu = !isYoutubeLoggedIn
            ) {
                if (isYoutubeLoggedIn) {
                    onSignOut()
                } else {
                    navEngine.pushScreen(IpodScreen.YouTubeLogin)
                }
            }
        )

        is IpodScreen.SettingsHardwarePreset -> IpodPresets.ALL_PRESETS.map { preset ->
            val isCurrent = preset.id == hardwareTheme.id
            IpodMenuItem("${if (isCurrent) "✓ " else ""}${preset.name}", subtitle = preset.category.title, hasSubmenu = false) {
                setHardwareTheme(preset)
                navEngine.popScreen()
            }
        }

        is IpodScreen.SettingsFinishStyle -> HardwareFinish.entries.map { finish ->
            val isCurrent = finish == hardwareTheme.finishStyle
            IpodMenuItem("${if (isCurrent) "✓ " else ""}${finish.title}", hasSubmenu = false) {
                setHardwareTheme(hardwareTheme.copy(finishStyle = finish))
                navEngine.popScreen()
            }
        }

        is IpodScreen.SettingsLcdTheme -> LcdTheme.entries.map { theme ->
            val isCurrent = theme == lcdTheme
            IpodMenuItem("${if (isCurrent) "✓ " else ""}${theme.title}", hasSubmenu = false) {
                setLcdTheme(theme)
                navEngine.popScreen()
            }
        }

        else -> listOf(IpodMenuItem("Back", hasSubmenu = false) { navEngine.popScreen() })
    }
}
