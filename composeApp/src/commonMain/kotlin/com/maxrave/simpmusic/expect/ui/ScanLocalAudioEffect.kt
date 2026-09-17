package com.maxrave.simpmusic.expect.ui

import androidx.compose.runtime.Composable
import com.maxrave.domain.data.entities.SongEntity

@Composable
expect fun ScanLocalAudioEffect(onSongsScanned: (List<SongEntity>) -> Unit)
