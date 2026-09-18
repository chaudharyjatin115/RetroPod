package com.maxrave.simpmusic.ui.ipod

import com.maxrave.domain.data.entities.LocalPlaylistEntity
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.searchResult.playlists.PlaylistsResult
import com.maxrave.domain.data.model.searchResult.songs.SongsResult
import com.maxrave.domain.mediaservice.handler.DownloadHandler
import com.maxrave.domain.utils.LocalResource
import com.maxrave.domain.utils.isLocal
import com.maxrave.domain.utils.toTrack
import com.maxrave.simpmusic.viewModel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Utility builder for generating structured iPod menu item lists based on active screen and state.
 */
object IpodMenuBuilder {

    fun buildMenuItems(
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
        scope: CoroutineScope
    ): List<IpodMenuItem> {
        return when (screen) {
            is IpodScreen.Main -> buildMainMenu(navEngine)
            is IpodScreen.Music -> buildMusicMenu(navEngine, localPlaylists.size, likedSongs.size, localSongs.size)
            is IpodScreen.Playlists -> buildPlaylistsMenu(navEngine, youTubePlaylistsResource, isYoutubeLoggedIn)
            is IpodScreen.LocalPlaylists -> buildLocalPlaylistsMenu(navEngine, localPlaylists)
            is IpodScreen.PlaylistDetail, is IpodScreen.LocalPlaylistDetail -> buildPlaylistDetailMenu(navEngine, playlistTracks, isPlaylistTracksLoading)
            is IpodScreen.LikedSongs -> buildLikedSongsMenu(navEngine, likedSongs)
            is IpodScreen.LocalSongs -> buildLocalSongsMenu(navEngine, localSongs)
            is IpodScreen.Search -> buildSearchMenu(navEngine, searchSongs)
            is IpodScreen.TrackOptions -> buildTrackOptionsMenu(screen, sharedViewModel, downloadHandler, localSongs, navEngine, scope)
            is IpodScreen.Settings -> buildSettingsMenu(navEngine, hardwareTheme, lcdTheme, stickersEnabled, setStickersEnabled, isYoutubeLoggedIn)
            is IpodScreen.SettingsLogin -> buildLoginSettingsMenu(navEngine, isYoutubeLoggedIn, onSignOut)
            is IpodScreen.SettingsHardwarePreset -> buildHardwarePresetMenu(navEngine, hardwareTheme, setHardwareTheme)
            is IpodScreen.SettingsFinishStyle -> buildFinishStyleMenu(navEngine, hardwareTheme, setHardwareTheme)
            is IpodScreen.SettingsLcdTheme -> buildLcdThemeMenu(navEngine, lcdTheme, setLcdTheme)
            else -> listOf(IpodMenuItem("Back", hasSubmenu = false) { navEngine.popScreen() })
        }
    }

    private fun buildMainMenu(navEngine: IpodNavigationEngine): List<IpodMenuItem> = listOf(
        IpodMenuItem("Music", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Music) },
        IpodMenuItem("Playlists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Playlists) },
        IpodMenuItem("Now Playing", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.NowPlaying) },
        IpodMenuItem("Search", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Search) },
        IpodMenuItem("Settings", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Settings) }
    )

    private fun buildMusicMenu(
        navEngine: IpodNavigationEngine,
        localPlaylistCount: Int,
        likedSongsCount: Int,
        localSongsCount: Int
    ): List<IpodMenuItem> = listOf(
        IpodMenuItem("Search YouTube Music", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Search) },
        IpodMenuItem("YouTube Playlists", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.Playlists) },
        IpodMenuItem("Local Playlists ($localPlaylistCount)", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LocalPlaylists) },
        IpodMenuItem("Liked Songs ($likedSongsCount)", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LikedSongs) },
        IpodMenuItem("Downloaded & Local Songs ($localSongsCount)", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.LocalSongs) },
        IpodMenuItem("Now Playing", hasSubmenu = true) { navEngine.pushScreen(IpodScreen.NowPlaying) }
    )

    private fun buildPlaylistsMenu(
        navEngine: IpodNavigationEngine,
        resource: LocalResource<List<PlaylistsResult>>,
        isYoutubeLoggedIn: Boolean
    ): List<IpodMenuItem> = when (resource) {
        is LocalResource.Loading -> listOf(IpodMenuItem("Loading playlists...", hasSubmenu = false) {})
        is LocalResource.Error -> listOf(IpodMenuItem("Error loading playlists", subtitle = "Sign in to view your playlists", hasSubmenu = false) {})
        is LocalResource.Success -> {
            val playlists = resource.data ?: emptyList()
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

    private fun buildLocalPlaylistsMenu(
        navEngine: IpodNavigationEngine,
        localPlaylists: List<LocalPlaylistEntity>
    ): List<IpodMenuItem> = if (localPlaylists.isEmpty()) {
        listOf(IpodMenuItem("No Local Playlists", subtitle = "Create local playlists in library", hasSubmenu = false) {})
    } else {
        localPlaylists.map { localPl ->
            IpodMenuItem(localPl.title, subtitle = "Local Playlist", hasSubmenu = true) {
                navEngine.pushScreen(IpodScreen.LocalPlaylistDetail(localPl.title, localPl.id))
            }
        }
    }

    private fun buildPlaylistDetailMenu(
        navEngine: IpodNavigationEngine,
        playlistTracks: List<Track>,
        isLoading: Boolean
    ): List<IpodMenuItem> = if (isLoading) {
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

    private fun buildLikedSongsMenu(
        navEngine: IpodNavigationEngine,
        likedSongs: List<SongEntity>
    ): List<IpodMenuItem> = if (likedSongs.isEmpty()) {
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

    private fun buildLocalSongsMenu(
        navEngine: IpodNavigationEngine,
        localSongs: List<SongEntity>
    ): List<IpodMenuItem> = if (localSongs.isEmpty()) {
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

    private fun buildSearchMenu(
        navEngine: IpodNavigationEngine,
        searchSongs: List<SongsResult>
    ): List<IpodMenuItem> = if (searchSongs.isEmpty()) {
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

    private fun buildTrackOptionsMenu(
        screen: IpodScreen.TrackOptions,
        sharedViewModel: SharedViewModel,
        downloadHandler: DownloadHandler,
        localSongs: List<SongEntity>,
        navEngine: IpodNavigationEngine,
        scope: CoroutineScope
    ): List<IpodMenuItem> {
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
        return items
    }

    private fun buildSettingsMenu(
        navEngine: IpodNavigationEngine,
        hardwareTheme: IpodHardwareTheme,
        lcdTheme: LcdTheme,
        stickersEnabled: Boolean,
        setStickersEnabled: (Boolean) -> Unit,
        isYoutubeLoggedIn: Boolean
    ): List<IpodMenuItem> = listOf(
        IpodMenuItem("Account Settings", subtitle = if (isYoutubeLoggedIn) "Logged In" else "Not Logged In", hasSubmenu = true) {
            navEngine.pushScreen(IpodScreen.SettingsLogin)
        },
        IpodMenuItem("Laser Engraved Decals", subtitle = if (stickersEnabled) "Enabled" else "Disabled", hasSubmenu = false) {
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

    private fun buildLoginSettingsMenu(
        navEngine: IpodNavigationEngine,
        isYoutubeLoggedIn: Boolean,
        onSignOut: () -> Unit
    ): List<IpodMenuItem> = listOf(
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

    private fun buildHardwarePresetMenu(
        navEngine: IpodNavigationEngine,
        hardwareTheme: IpodHardwareTheme,
        setHardwareTheme: (IpodHardwareTheme) -> Unit
    ): List<IpodMenuItem> = IpodPresets.ALL_PRESETS.map { preset ->
        val isCurrent = preset.id == hardwareTheme.id
        IpodMenuItem("${if (isCurrent) "✓ " else ""}${preset.name}", subtitle = preset.category.title, hasSubmenu = false) {
            setHardwareTheme(preset)
            navEngine.popScreen()
        }
    }

    private fun buildFinishStyleMenu(
        navEngine: IpodNavigationEngine,
        hardwareTheme: IpodHardwareTheme,
        setHardwareTheme: (IpodHardwareTheme) -> Unit
    ): List<IpodMenuItem> = HardwareFinish.entries.map { finish ->
        val isCurrent = finish == hardwareTheme.finishStyle
        IpodMenuItem("${if (isCurrent) "✓ " else ""}${finish.title}", hasSubmenu = false) {
            setHardwareTheme(hardwareTheme.copy(finishStyle = finish))
            navEngine.popScreen()
        }
    }

    private fun buildLcdThemeMenu(
        navEngine: IpodNavigationEngine,
        lcdTheme: LcdTheme,
        setLcdTheme: (LcdTheme) -> Unit
    ): List<IpodMenuItem> = LcdTheme.entries.map { theme ->
        val isCurrent = theme == lcdTheme
        IpodMenuItem("${if (isCurrent) "✓ " else ""}${theme.title}", hasSubmenu = false) {
            setLcdTheme(theme)
            navEngine.popScreen()
        }
    }
}
