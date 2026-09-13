package com.maxrave.simpmusic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maxrave.domain.data.model.SongModel
import com.maxrave.domain.extension.toSongEntity
import com.maxrave.simpmusic.ui.ipod.IpodFinish
import com.maxrave.simpmusic.ui.ipod.IpodHardwareShell
import com.maxrave.simpmusic.ui.ipod.IpodMenuItem
import com.maxrave.simpmusic.ui.ipod.IpodNavigationEngine
import com.maxrave.simpmusic.ui.ipod.IpodScreen
import com.maxrave.simpmusic.ui.ipod.LcdTheme
import com.maxrave.simpmusic.viewModel.AlbumViewModel
import com.maxrave.simpmusic.viewModel.ArtistViewModel
import com.maxrave.simpmusic.viewModel.HomeViewModel
import com.maxrave.simpmusic.viewModel.LibraryViewModel
import com.maxrave.simpmusic.viewModel.LocalPlaylistViewModel
import com.maxrave.simpmusic.viewModel.PlaylistViewModel
import com.maxrave.simpmusic.viewModel.SearchViewModel
import com.maxrave.simpmusic.viewModel.SharedViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val sharedViewModel: SharedViewModel = koinInject()
    val homeViewModel: HomeViewModel = koinViewModel()
    val libraryViewModel: LibraryViewModel = koinViewModel()
    val searchViewModel: SearchViewModel = koinViewModel()
    val localPlaylistViewModel: LocalPlaylistViewModel = koinViewModel()
    val artistViewModel: ArtistViewModel = koinViewModel()
    val albumViewModel: AlbumViewModel = koinViewModel()
    val playlistViewModel: PlaylistViewModel = koinViewModel()

    val navEngine = remember { IpodNavigationEngine() }

    var ipodFinish by remember { mutableStateOf(IpodFinish.SILVER) }
    var lcdTheme by remember { mutableStateOf(LcdTheme.CLASSIC_BLUE) }

    val nowPlayingTrackState by sharedViewModel.nowPlayingTrackState.collectAsStateWithLifecycle()
    val simpleMediaState by sharedViewModel.simpleMediaState.collectAsStateWithLifecycle()
    val lyricsResource by sharedViewModel.lyrics.collectAsStateWithLifecycle()

    val currentSong = nowPlayingTrackState.songModel
    val isPlaying = simpleMediaState.isPlaying
    val currentPositionMs = simpleMediaState.progress
    val durationMs = simpleMediaState.duration

    val lyricsText = lyricsResource.data?.plainLyrics

    val homeState by homeViewModel.homeState.collectAsStateWithLifecycle()
    val libraryState by libraryViewModel.libraryState.collectAsStateWithLifecycle()
    val searchState by searchViewModel.searchScreenState.collectAsStateWithLifecycle()
    val localSongs by localPlaylistViewModel.allLocalSongs.collectAsStateWithLifecycle(emptyList())

    // Load home and library content on startup
    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
        libraryViewModel.getLibraryData()
    }

    // Build items for current screen
    val currentItems = remember(
        navEngine.currentScreen,
        homeState,
        libraryState,
        searchState,
        localSongs,
        ipodFinish,
        lcdTheme
    ) {
        buildIpodMenuItems(
            screen = navEngine.currentScreen,
            navEngine = navEngine,
            sharedViewModel = sharedViewModel,
            homeViewModel = homeViewModel,
            searchViewModel = searchViewModel,
            libraryState = libraryState,
            homeState = homeState,
            searchState = searchState,
            localSongs = localSongs,
            ipodFinish = ipodFinish,
            setFinish = { ipodFinish = it },
            lcdTheme = lcdTheme,
            setLcdTheme = { lcdTheme = it }
        )
    }

    IpodHardwareShell(
        finish = ipodFinish,
        lcdTheme = lcdTheme,
        screen = navEngine.currentScreen,
        canGoBack = navEngine.navStack.size > 1,
        items = currentItems,
        selectedIndex = navEngine.selectedIndex,
        currentSong = currentSong,
        isPlaying = isPlaying,
        currentPositionMs = currentPositionMs,
        durationMs = durationMs,
        lyricsText = lyricsText,
        searchQuery = navEngine.searchQuery,
        onSearchQueryChange = { query ->
            navEngine.searchQuery = query
            searchViewModel.search(query)
        },
        onWheelScroll = { steps ->
            navEngine.scrollSelection(steps, currentItems.size)
        },
        onMenuClick = {
            navEngine.popScreen()
        },
        onSelectClick = {
            if (currentItems.isNotEmpty() && navEngine.selectedIndex in currentItems.indices) {
                currentItems[navEngine.selectedIndex].action()
            }
        },
        onPlayPauseClick = {
            if (isPlaying) sharedViewModel.pause() else sharedViewModel.play()
        },
        onNextClick = {
            sharedViewModel.next()
        },
        onPrevClick = {
            sharedViewModel.prev()
        },
        modifier = Modifier
    )
}

private fun buildIpodMenuItems(
    screen: IpodScreen,
    navEngine: IpodNavigationEngine,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    libraryState: com.maxrave.simpmusic.viewModel.LibraryState,
    homeState: com.maxrave.simpmusic.viewModel.HomeState,
    searchState: com.maxrave.simpmusic.viewModel.SearchScreenState,
    localSongs: List<com.maxrave.domain.data.entities.SongEntity>,
    ipodFinish: IpodFinish,
    setFinish: (IpodFinish) -> Unit,
    lcdTheme: LcdTheme,
    setLcdTheme: (LcdTheme) -> Unit
): List<IpodMenuItem> {
    return when (screen) {
        is IpodScreen.Main -> listOf(
            IpodMenuItem("Music", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Music) },
            IpodMenuItem("Now Playing", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.NowPlaying) },
            IpodMenuItem("Search", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Search) },
            IpodMenuItem("Settings", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Settings) }
        )

        is IpodScreen.Music -> listOf(
            IpodMenuItem("Playlists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Playlists) },
            IpodMenuItem("Artists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Artists) },
            IpodMenuItem("Albums", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Albums) },
            IpodMenuItem("Songs", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Songs) },
            IpodMenuItem("Local Storage (${localSongs.size})", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LocalSongs) }
        )

        is IpodScreen.Playlists -> {
            val playlists = libraryState.playlists
            if (playlists.isEmpty()) {
                listOf(IpodMenuItem("No Playlists Found", hasSubmenu = false) {})
            } else {
                playlists.map { playlist ->
                    IpodMenuItem(playlist.name, subtitle = "${playlist.songCount} songs", hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.PlaylistDetail(playlist.name, playlist.id))
                    }
                }
            }
        }

        is IpodScreen.Artists -> {
            val artists = libraryState.artists
            if (artists.isEmpty()) {
                listOf(IpodMenuItem("No Followed Artists", hasSubmenu = false) {})
            } else {
                artists.map { artist ->
                    IpodMenuItem(artist.name, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.ArtistDetail(artist.name, artist.id))
                    }
                }
            }
        }

        is IpodScreen.Albums -> {
            val albums = libraryState.albums
            if (albums.isEmpty()) {
                listOf(IpodMenuItem("No Saved Albums", hasSubmenu = false) {})
            } else {
                albums.map { album ->
                    IpodMenuItem(album.title, subtitle = album.artistsName, hasSubmenu = true) {
                        navEngine.pushScreen(IpodScreen.AlbumDetail(album.title, album.id))
                    }
                }
            }
        }

        is IpodScreen.Songs -> {
            val songs = libraryState.likedSongs
            if (songs.isEmpty()) {
                listOf(IpodMenuItem("No Liked Songs", hasSubmenu = false) {})
            } else {
                songs.mapIndexed { index, song ->
                    IpodMenuItem(song.title, subtitle = song.artists?.joinToString(", ") { it.name }, hasSubmenu = false) {
                        sharedViewModel.loadAndPlayList(
                            com.maxrave.domain.mediaservice.handler.QueueData(
                                songs = songs,
                                startIndex = index
                            )
                        )
                        navEngine.pushScreen(IpodScreen.NowPlaying)
                    }
                }
            }
        }

        is IpodScreen.LocalSongs -> {
            if (localSongs.isEmpty()) {
                listOf(IpodMenuItem("No Local Tracks Found", hasSubmenu = false) {})
            } else {
                localSongs.mapIndexed { index, songEntity ->
                    IpodMenuItem(songEntity.title, subtitle = songEntity.artistName, hasSubmenu = false) {
                        val songModels = localSongs.map { it.toSongModel() }
                        sharedViewModel.loadAndPlayList(
                            com.maxrave.domain.mediaservice.handler.QueueData(
                                songs = songModels,
                                startIndex = index
                            )
                        )
                        navEngine.pushScreen(IpodScreen.NowPlaying)
                    }
                }
            }
        }

        is IpodScreen.Search -> {
            val searchSongs = searchState.searchSongsResult
            if (searchSongs.isEmpty()) {
                listOf(IpodMenuItem("Type to search...", hasSubmenu = false) {})
            } else {
                searchSongs.map { song ->
                    val songModel = SongModel(
                        videoId = song.videoId,
                        title = song.title,
                        artists = song.artists?.map { com.maxrave.domain.data.model.browse.artist.Artist(it.name, it.id ?: "") },
                        thumbnails = song.thumbnails
                    )
                    IpodMenuItem(song.title, subtitle = song.artists?.joinToString(", ") { it.name }, hasSubmenu = false) {
                        sharedViewModel.loadAndPlayList(
                            com.maxrave.domain.mediaservice.handler.QueueData(
                                songs = listOf(songModel),
                                startIndex = 0
                            )
                        )
                        navEngine.pushScreen(IpodScreen.NowPlaying)
                    }
                }
            }
        }

        is IpodScreen.Settings -> listOf(
            IpodMenuItem("iPod Finish", subtitle = ipodFinish.title, hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsFinish)
            },
            IpodMenuItem("LCD Display Theme", subtitle = lcdTheme.title, hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.SettingsLcdTheme)
            }
        )

        is IpodScreen.SettingsFinish -> IpodFinish.entries.map { finish ->
            val isCurrent = finish == ipodFinish
            IpodMenuItem("${if (isCurrent) "✓ " else ""}${finish.title}", hasSubmenu = false) {
                setFinish(finish)
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

private fun com.maxrave.domain.data.entities.SongEntity.toSongModel(): SongModel {
    return SongModel(
        videoId = this.id,
        title = this.title,
        artists = listOf(com.maxrave.domain.data.model.browse.artist.Artist(name = this.artistName ?: "Unknown", id = "")),
        duration = this.duration,
        thumbnails = listOf(com.maxrave.domain.data.model.metadata.Thumbnail(url = this.thumbnailUrl ?: "", width = 300, height = 300))
    )
}
