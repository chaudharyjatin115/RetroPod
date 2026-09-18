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
        com.maxrave.simpmusic.ui.ipod.IpodMenuBuilder.buildMenuItems(
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
        canGoBack = navEngine.canGoBack,
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
