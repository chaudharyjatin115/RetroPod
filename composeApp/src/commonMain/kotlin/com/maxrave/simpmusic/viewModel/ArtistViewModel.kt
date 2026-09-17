package com.maxrave.simpmusic.viewModel

import androidx.lifecycle.viewModelScope
import com.maxrave.domain.data.entities.ArtistEntity
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.browse.artist.Albums
import com.maxrave.domain.data.model.browse.artist.ArtistBrowse
import com.maxrave.domain.data.model.browse.artist.ArtistLogo
import com.maxrave.domain.data.model.browse.artist.Singles
import com.maxrave.domain.extension.now
import com.maxrave.domain.repository.ArtistRepository
import com.maxrave.domain.repository.SongRepository
import com.maxrave.domain.utils.Resource
import com.maxrave.simpmusic.extension.toArtistScreenData
import com.maxrave.simpmusic.viewModel.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ArtistScreenData(
    val title: String? = null,
    val imageUrl: String? = null,
    val subscribers: String? = null,
    val playCount: String? = null,
    val isChannel: Boolean = false,
    val channelId: String? = null,
    val radioParam: String? = null,
    val shuffleParam: String? = null,
    val description: String? = null,
    val listSongParam: String? = null,
    val popularSongs: List<Track> = emptyList(),
    val singles: List<Singles> = emptyList(),
    val albums: List<Albums> = emptyList(),
    val video: ArtistBrowse.Videos? = null,
)

sealed class ArtistScreenState {
    data object Loading : ArtistScreenState()
    data class Success(val data: ArtistScreenData) : ArtistScreenState()
    data class Error(val message: String) : ArtistScreenState()
}

class ArtistViewModel(
    private val artistRepository: ArtistRepository,
    private val songRepository: SongRepository,
) : BaseViewModel() {

    private val _artistLogo: MutableStateFlow<ArtistLogo?> = MutableStateFlow(null)
    val artistLogo: StateFlow<ArtistLogo?> = _artistLogo

    private var _followed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var followed: StateFlow<Boolean> = _followed

    private val _artistScreenState: MutableStateFlow<ArtistScreenState> = MutableStateFlow(ArtistScreenState.Loading)
    val artistScreenState: StateFlow<ArtistScreenState> = _artistScreenState

    fun browseArtist(channelId: String) {
        _artistScreenState.value = ArtistScreenState.Loading
        _artistLogo.value = null
        _followed.value = false
        viewModelScope.launch {
            artistRepository.getArtistData(channelId).collect { browse ->
                val data = browse.data
                when (browse) {
                    is Resource.Success if (data != null) -> {
                        data.channelId?.let { chId ->
                            insertArtist(
                                ArtistEntity(
                                    chId,
                                    data.name,
                                    data.thumbnails?.lastOrNull()?.url,
                                ),
                            )
                        }
                        _artistScreenState.value = ArtistScreenState.Success(data.toArtistScreenData())
                    }

                    is Resource.Error ->
                        _artistScreenState.value = ArtistScreenState.Error(browse.message ?: "Error")

                    else -> {
                        _artistScreenState.value = ArtistScreenState.Error("Error")
                    }
                }
            }
        }
    }

    fun insertArtist(artist: ArtistEntity) {
        viewModelScope.launch {
            artistRepository.insertArtist(artist)
            artistRepository.updateArtistInLibrary(now(), artist.channelId)
            delay(100)
            artistRepository.getArtistById(artist.channelId).collect { artistEntity ->
                if (artistEntity != null) {
                    artist.thumbnails?.let {
                        artistRepository.updateArtistImage(artistEntity.channelId, it)
                    }
                    _followed.value = artistEntity.followed
                }
            }
        }
    }

    fun followArtist(channelId: String) {
        viewModelScope.launch {
            artistRepository.getArtistById(channelId).collect { artistEntity ->
                artistRepository.updateArtistInLibrary(now(), channelId)
                _followed.value = !(artistEntity?.followed ?: false)
            }
        }
    }
}
