package com.maxrave.simpmusic.expect.ui

import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.maxrave.domain.data.entities.DownloadState
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.extension.now
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Composable
actual fun ScanLocalAudioEffect(onSongsScanned: (List<SongEntity>) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val localList = mutableListOf<SongEntity>()
            val resolver = context.contentResolver
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            try {
                resolver.query(uri, projection, selection, null, "${MediaStore.Audio.Media.TITLE} ASC")?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val title = cursor.getString(titleCol) ?: "Unknown Track"
                        val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                        val album = cursor.getString(albumCol)
                        val durationMs = cursor.getLong(durationCol)
                        val albumId = cursor.getLong(albumIdCol)

                        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                        val artUri = "content://media/external/audio/albumart/$albumId"

                        val totalSecs = (durationMs / 1000).toInt()
                        val mins = totalSecs / 60
                        val secs = totalSecs % 60
                        val durStr = "$mins:${if (secs < 10) "0$secs" else "$secs"}"

                        localList.add(
                            SongEntity(
                                videoId = contentUri,
                                title = title,
                                artistName = listOf(artist),
                                albumName = album,
                                duration = durStr,
                                durationSeconds = totalSecs,
                                isAvailable = true,
                                isExplicit = false,
                                likeStatus = "INDIFFERENT",
                                thumbnails = artUri,
                                videoType = "LOCAL",
                                category = "Local",
                                resultType = "song",
                                downloadState = DownloadState.STATE_DOWNLOADED,
                                inLibrary = now()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (localList.isNotEmpty()) {
                onSongsScanned(localList)
            }
        }
    }
}
