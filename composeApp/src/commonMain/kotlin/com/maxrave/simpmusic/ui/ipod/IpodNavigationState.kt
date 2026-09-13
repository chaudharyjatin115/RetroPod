package com.maxrave.simpmusic.ui.ipod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class IpodScreen(val title: String) {
    data object Main : IpodScreen("iPod")
    data object Music : IpodScreen("Music")
    data object Playlists : IpodScreen("Playlists")
    data object Artists : IpodScreen("Artists")
    data object Albums : IpodScreen("Albums")
    data object Songs : IpodScreen("Songs")
    data object LocalSongs : IpodScreen("Local Storage")
    data class ArtistDetail(val artistName: String, val artistId: String) : IpodScreen(artistName)
    data class AlbumDetail(val albumName: String, val albumId: String) : IpodScreen(albumName)
    data class PlaylistDetail(val playlistName: String, val playlistId: String) : IpodScreen(playlistName)
    data object NowPlaying : IpodScreen("Now Playing")
    data object Search : IpodScreen("Search")
    data object Settings : IpodScreen("Settings")
    data object SettingsFinish : IpodScreen("iPod Finish")
    data object SettingsLcdTheme : IpodScreen("LCD Theme")
}

data class IpodMenuItem(
    val title: String,
    val subtitle: String? = null,
    val hasSubmenu: Boolean = true,
    val action: () -> Unit
)

class IpodNavigationEngine {
    val navStack = mutableStateListOf<IpodScreen>(IpodScreen.Main)
    var selectedIndex by mutableIntStateOf(0)
    var searchQuery by mutableStateOf("")

    val currentScreen: IpodScreen
        get() = navStack.lastOrNull() ?: IpodScreen.Main

    fun pushScreen(screen: IpodScreen) {
        navStack.add(screen)
        selectedIndex = 0
    }

    fun popScreen(): Boolean {
        if (navStack.size > 1) {
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
