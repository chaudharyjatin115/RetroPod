package com.maxrave.simpmusic.ui.ipod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.maxrave.domain.data.model.browse.album.Track

/**
 * Screen destinations supported in the iPod interface navigation engine.
 */
sealed class IpodScreen(val title: String) {
    data object Main : IpodScreen("RetroPod")
    data object Music : IpodScreen("Music")
    data object Playlists : IpodScreen("Playlists")
    data object Artists : IpodScreen("Artists")
    data object Albums : IpodScreen("Albums")
    data object Songs : IpodScreen("Songs")
    data object LocalSongs : IpodScreen("Local Storage")
    data object LikedSongs : IpodScreen("Liked Songs")
    data object LocalPlaylists : IpodScreen("Local Playlists")
    data class LocalPlaylistDetail(val playlistName: String, val playlistId: Long) : IpodScreen(playlistName)
    data class ArtistDetail(val artistName: String, val artistId: String) : IpodScreen(artistName)
    data class AlbumDetail(val albumName: String, val albumId: String) : IpodScreen(albumName)
    data class PlaylistDetail(val playlistName: String, val playlistId: String) : IpodScreen(playlistName)
    data object NowPlaying : IpodScreen("Now Playing")
    data object Search : IpodScreen("Search")
    data object Settings : IpodScreen("Settings")
    data object SettingsHardwarePreset : IpodScreen("Shell Theme")
    data object SettingsFinishStyle : IpodScreen("Hardware Finish")
    data object SettingsLcdTheme : IpodScreen("LCD Display Theme")
    data object SettingsLogin : IpodScreen("YouTube Account")
    data object YouTubeLogin : IpodScreen("Sign In")
    data class TrackOptions(val track: Track, val queue: List<Track>) : IpodScreen(track.title)
}

/**
 * Representation of a selectable item within the iPod LCD menu list.
 */
data class IpodMenuItem(
    val title: String,
    val subtitle: String? = null,
    val hasSubmenu: Boolean = true,
    val action: () -> Unit
)

/**
 * Navigation state engine managing backstack, selection index, and search state.
 */
class IpodNavigationEngine {
    val navStack = mutableStateListOf<IpodScreen>(IpodScreen.Main)
    var selectedIndex by mutableIntStateOf(0)
    var searchQuery by mutableStateOf("")

    val currentScreen: IpodScreen
        get() = navStack.lastOrNull() ?: IpodScreen.Main

    val canGoBack: Boolean
        get() = navStack.size > 1

    fun pushScreen(screen: IpodScreen) {
        navStack.add(screen)
        selectedIndex = 0
    }

    fun popScreen(): Boolean {
        if (canGoBack) {
            navStack.removeAt(navStack.lastIndex)
            selectedIndex = 0
            return true
        }
        return false
    }

    fun scrollSelection(steps: Int, itemCount: Int) {
        if (itemCount <= 0) return
        val newIndex = selectedIndex + steps
        selectedIndex = newIndex.coerceIn(0, itemCount - 1)
    }
}
