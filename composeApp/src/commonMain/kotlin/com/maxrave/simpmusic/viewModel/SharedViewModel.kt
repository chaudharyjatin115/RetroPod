package com.maxrave.simpmusic.viewModel

import androidx.lifecycle.viewModelScope
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.player.GenericCastState
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.mediaservice.handler.ControlState
import com.maxrave.domain.mediaservice.handler.NowPlayingTrackState
import com.maxrave.domain.mediaservice.handler.PlayerEvent
import com.maxrave.domain.mediaservice.handler.QueueData
import com.maxrave.domain.mediaservice.handler.RepeatState
import com.maxrave.domain.mediaservice.handler.SimpleMediaState
import com.maxrave.domain.mediaservice.handler.SleepTimerState
import com.maxrave.domain.repository.AlbumRepository
import com.maxrave.domain.repository.CacheRepository
import com.maxrave.domain.repository.LocalPlaylistRepository
import com.maxrave.domain.repository.PlaylistRepository
import com.maxrave.domain.repository.SongRepository
import com.maxrave.domain.repository.StreamRepository
import com.maxrave.simpmusic.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.added_to_queue
import simpmusic.composeapp.generated.resources.added_to_youtube_liked
import simpmusic.composeapp.generated.resources.error
import simpmusic.composeapp.generated.resources.play_next
import simpmusic.composeapp.generated.resources.removed_from_youtube_liked

class SharedViewModel(
    private val dataStoreManager: DataStoreManager,
    private val streamRepository: StreamRepository,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val localPlaylistRepository: LocalPlaylistRepository,
    private val playlistRepository: PlaylistRepository,
    private val cacheRepository: CacheRepository,
) : BaseViewModel() {

    val nowPlayingState: StateFlow<NowPlayingTrackState> = mediaPlayerHandler.nowPlayingState
    val nowPlayingTrackState: StateFlow<NowPlayingTrackState> = mediaPlayerHandler.nowPlayingState
    val simpleMediaState: StateFlow<SimpleMediaState> = mediaPlayerHandler.simpleMediaState
    val controllerState: StateFlow<ControlState> = mediaPlayerHandler.controlState
    val controlState: StateFlow<ControlState> = mediaPlayerHandler.controlState
    val queueData: StateFlow<QueueData?> = mediaPlayerHandler.queueData
    val sleepTimerState: StateFlow<SleepTimerState> = mediaPlayerHandler.sleepTimerState
    val castState: StateFlow<GenericCastState> = mediaPlayerHandler.castState

    private val _likeStatus = MutableStateFlow(false)
    val likeStatus: StateFlow<Boolean> get() = _likeStatus.asStateFlow()

    fun onUIEvent(uiEvent: UIEvent) =
        viewModelScope.launch {
            when (uiEvent) {
                UIEvent.Backward -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Backward)
                UIEvent.Forward -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Forward)
                UIEvent.PlayPause -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.PlayPause)
                UIEvent.Next -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Next)
                UIEvent.Previous -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Previous)
                UIEvent.SkipToPrevious -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.SkipToPrevious)
                UIEvent.Stop -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Stop)
                is UIEvent.UpdateProgress -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.UpdateProgress(uiEvent.newProgress))
                UIEvent.Repeat -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Repeat)
                UIEvent.Shuffle -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.Shuffle)
                UIEvent.ToggleLike -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.ToggleLike)
                is UIEvent.UpdateVolume -> mediaPlayerHandler.onPlayerEvent(PlayerEvent.UpdateVolume(uiEvent.newVolume))
            }
        }

    fun playTrack(track: Track, queue: List<Track> = listOf(track)) {
        viewModelScope.launch {
            val list = ArrayList(queue)
            val idx = list.indexOfFirst { it.videoId == track.videoId }.coerceAtLeast(0)
            mediaPlayerHandler.setQueueData(QueueData.Data(listTracks = list, firstPlayedTrack = track))
            mediaPlayerHandler.loadMediaItem(track, type = "song", index = idx)
        }
    }

    fun addListToQueue(listTrack: ArrayList<Track>) {
        viewModelScope.launch {
            if (listTrack.size == 1 && dataStoreManager.endlessQueue.first() == DataStoreManager.TRUE) {
                mediaPlayerHandler.playNext(listTrack.first())
                makeToast(getString(Res.string.play_next))
            } else {
                mediaPlayerHandler.loadMoreCatalog(listTrack)
                makeToast(getString(Res.string.added_to_queue))
            }
        }
    }

    fun addToYouTubeLiked() {
        viewModelScope.launch {
            val videoId = mediaPlayerHandler.nowPlaying.first()?.mediaId
            if (videoId != null) {
                val like = likeStatus.value
                if (!like) {
                    songRepository.addToYouTubeLiked(videoId).collect { response ->
                        if (response == 200) {
                            makeToast(getString(Res.string.added_to_youtube_liked))
                            _likeStatus.value = true
                        } else {
                            makeToast(getString(Res.string.error))
                        }
                    }
                } else {
                    songRepository.removeFromYouTubeLiked(videoId).collect { response ->
                        if (response == 200) {
                            makeToast(getString(Res.string.removed_from_youtube_liked))
                            _likeStatus.value = false
                        } else {
                            makeToast(getString(Res.string.error))
                        }
                    }
                }
            }
        }
    }

    fun getYouTubeLoggedIn() = dataStoreManager.loggedIn
    fun getThemeMode() = dataStoreManager.themeMode
    fun getThemeColorSource() = dataStoreManager.themeColorSource

    sealed class UIEvent {
        data object PlayPause : UIEvent()
        data object Backward : UIEvent()
        data object Forward : UIEvent()
        data object Stop : UIEvent()
        data object Next : UIEvent()
        data object Previous : UIEvent()
        data object SkipToPrevious : UIEvent()
        data object Shuffle : UIEvent()
        data object Repeat : UIEvent()
        data class UpdateProgress(val newProgress: Float) : UIEvent()
        data object ToggleLike : UIEvent()
        data class UpdateVolume(val newVolume: Float) : UIEvent()
    }
}
