package com.maxrave.simpmusic.ui.ipod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.maxrave.domain.data.model.SongModel

@Composable
fun IpodLcdDisplay(
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    currentSong: SongModel?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(lcdTheme.screenBackground)
            .border(2.dp, lcdTheme.dividerColor, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // LCD STATUS HEADER BAR
            LcdHeaderBar(
                lcdTheme = lcdTheme,
                title = screen.title,
                canGoBack = canGoBack,
                isPlaying = isPlaying
            )

            // SCREEN CONTENT
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (screen) {
                    is IpodScreen.NowPlaying -> {
                        LcdNowPlayingView(
                            lcdTheme = lcdTheme,
                            song = currentSong,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            lyricsText = lyricsText
                        )
                    }
                    is IpodScreen.Search -> {
                        LcdSearchView(
                            lcdTheme = lcdTheme,
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            items = items,
                            selectedIndex = selectedIndex
                        )
                    }
                    else -> {
                        LcdMenuView(
                            lcdTheme = lcdTheme,
                            items = items,
                            selectedIndex = selectedIndex,
                            isMainMenu = screen is IpodScreen.Main,
                            currentSong = currentSong
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LcdHeaderBar(
    lcdTheme: LcdTheme,
    title: String,
    canGoBack: Boolean,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        lcdTheme.headerBackground,
                        lcdTheme.headerBackground.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back Indicator
            if (canGoBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = lcdTheme.headerTextColor,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Spacer(modifier = Modifier.size(14.dp))
            }

            // Center: Screen Title
            Text(
                text = title,
                color = lcdTheme.headerTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Right: Play / Battery Status Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                    contentDescription = "Status",
                    tint = lcdTheme.headerTextColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.BatteryFull,
                    contentDescription = "Battery",
                    tint = lcdTheme.headerTextColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        HorizontalDivider(
            color = lcdTheme.dividerColor,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun LcdMenuView(
    lcdTheme: LcdTheme,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    isMainMenu: Boolean,
    currentSong: SongModel?
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(selectedIndex.coerceIn(0, items.lastIndex))
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Menu List Column
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(if (isMainMenu) 0.62f else 1f)
                .fillMaxHeight()
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == selectedIndex

                val rowModifier = if (isSelected) {
                    Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    lcdTheme.highlightBackgroundTop,
                                    lcdTheme.highlightBackgroundBottom
                                )
                            )
                        )
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .background(lcdTheme.screenBackground)
                }

                Row(
                    modifier = rowModifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = if (isSelected) lcdTheme.highlightTextColor else lcdTheme.textColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.subtitle != null) {
                            Text(
                                text = item.subtitle,
                                color = if (isSelected) lcdTheme.highlightTextColor.copy(alpha = 0.8f) else lcdTheme.secondaryTextColor,
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (item.hasSubmenu) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Submenu",
                            tint = if (isSelected) lcdTheme.highlightTextColor else lcdTheme.secondaryTextColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                HorizontalDivider(color = lcdTheme.dividerColor.copy(alpha = 0.4f), thickness = 0.5.dp)
            }
        }

        // Right Preview Section for iPod Main Menu
        if (isMainMenu) {
            Box(
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
                    .background(lcdTheme.screenBackground)
                    .border(width = 0.5.dp, color = lcdTheme.dividerColor),
                contentAlignment = Alignment.Center
            ) {
                val thumbnailUrl = currentSong?.thumbnails?.lastOrNull()?.url
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = "Album Art Preview",
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .shadow(4.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Music",
                        tint = lcdTheme.secondaryTextColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LcdNowPlayingView(
    lcdTheme: LcdTheme,
    song: SongModel?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?
) {
    if (song == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No Song Playing",
                color = lcdTheme.secondaryTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val thumbnailUrl = song.thumbnails?.lastOrNull()?.url
    val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Artwork
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, lcdTheme.dividerColor, RoundedCornerShape(6.dp))
                    .background(lcdTheme.dividerColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = song.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = lcdTheme.secondaryTextColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Metadata Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    color = lcdTheme.textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = song.artists?.joinToString(", ") { it.name } ?: "Unknown Artist",
                    color = lcdTheme.secondaryTextColor,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (song.album != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = song.album?.name ?: "",
                        color = lcdTheme.secondaryTextColor.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Synced Lyrics snippet if available
        if (!lyricsText.isNull_orEmpty()) {
            Text(
                text = lyricsText,
                color = lcdTheme.textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // Timeline Progress Section
        Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = lcdTheme.highlightBackgroundBottom,
                trackColor = lcdTheme.dividerColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPositionMs),
                    color = lcdTheme.secondaryTextColor,
                    fontSize = 9.sp
                )
                Text(
                    text = "-${formatTime((durationMs - currentPositionMs).coerceAtLeast(0L))}",
                    color = lcdTheme.secondaryTextColor,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun LcdSearchView(
    lcdTheme: LcdTheme,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    items: List<IpodMenuItem>,
    selectedIndex: Int
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search songs, artists...", fontSize = 11.sp, color = lcdTheme.secondaryTextColor) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = lcdTheme.secondaryTextColor, modifier = Modifier.size(16.dp)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lcdTheme.screenBackground,
                    unfocusedContainerColor = lcdTheme.screenBackground,
                    focusedTextColor = lcdTheme.textColor,
                    unfocusedTextColor = lcdTheme.textColor
                ),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            )
        }

        LcdMenuView(
            lcdTheme = lcdTheme,
            items = items,
            selectedIndex = selectedIndex,
            isMainMenu = false,
            currentSong = null
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun String?.isNull_orEmpty(): Boolean = this == null || this.trim().isEmpty()
