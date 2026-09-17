package com.maxrave.simpmusic.viewModel

import androidx.lifecycle.viewModelScope
import com.maxrave.domain.data.entities.ArtistEntity
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.repository.ArtistRepository
import com.maxrave.domain.repository.SongRepository
import com.maxrave.simpmusic.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryDynamicPlaylistViewModel(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
) : BaseViewModel() {
    private val _listFavoriteSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listFavoriteSong: StateFlow<List<SongEntity>> get() = _listFavoriteSong

    private val _listFollowedArtist: MutableStateFlow<List<ArtistEntity>> = MutableStateFlow(emptyList())
    val listFollowedArtist: StateFlow<List<ArtistEntity>> get() = _listFollowedArtist

    private val _listMostPlayedSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listMostPlayedSong: StateFlow<List<SongEntity>> get() = _listMostPlayedSong

    private val _listDownloadedSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listDownloadedSong: StateFlow<List<SongEntity>> get() = _listDownloadedSong

    private val _listMonthlyRecapSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listMonthlyRecapSong: StateFlow<List<SongEntity>> get() = _listMonthlyRecapSong

    fun getFavoriteSong() {
        viewModelScope.launch {
            songRepository.getLikedSongs().collect { songs ->
                _listFavoriteSong.value = songs
            }
        }
    }

    fun getFollowedArtist() {
        viewModelScope.launch {
            artistRepository.getFollowedArtists().collect { artists ->
                _listFollowedArtist.value = artists
            }
        }
    }

    fun getDownloadedSong() {
        viewModelScope.launch {
            songRepository.getDownloadedSongs().collect { songs ->
                _listDownloadedSong.value = songs ?: emptyList()
            }
        }
    }
}
