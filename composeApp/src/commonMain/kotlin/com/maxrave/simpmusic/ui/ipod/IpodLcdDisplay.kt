package com.maxrave.simpmusic.ui.ipod

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.simpmusic.expect.ui.PlatformWebView
import com.maxrave.simpmusic.expect.ui.createWebViewCookieManager
import com.maxrave.simpmusic.expect.ui.rememberWebViewState
import com.maxrave.simpmusic.ui.icon.ArrowBackIosNew
import com.maxrave.simpmusic.ui.icon.ArrowForwardIos
import com.maxrave.simpmusic.ui.icon.LibraryMusic
import com.maxrave.simpmusic.ui.icon.Pause
import com.maxrave.simpmusic.ui.icon.PlayArrow
import com.maxrave.simpmusic.ui.icon.Search
import com.maxrave.simpmusic.ui.icon.SimpIcons
import kotlinx.coroutines.delay

/**
 * Root Composable for the iPod LCD screen rendering status bar, menu views, now playing, and search.
 */
@Composable
fun IpodLcdDisplay(
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    playbackState: IpodPlaybackState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onYoutubeLoginDone: (cookie: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    IpodLcdDisplay(
        lcdTheme = lcdTheme,
        screen = screen,
        canGoBack = canGoBack,
        items = items,
        selectedIndex = selectedIndex,
        currentSong = playbackState.currentSong,
        isPlaying = playbackState.isPlaying,
        currentPositionMs = playbackState.currentPositionMs,
        durationMs = playbackState.durationMs,
        lyricsText = playbackState.lyricsText,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onYoutubeLoginDone = onYoutubeLoginDone,
        modifier = modifier
    )
}

/**
 * Direct primitive parameter overload for backwards compatibility.
 */
@Composable
fun IpodLcdDisplay(
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    currentSong: Track?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onYoutubeLoginDone: (cookie: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(lcdTheme.screenBackground)
            .border(1.5.dp, lcdTheme.dividerColor, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. LCD STATUS HEADER BAR
            LcdHeaderBar(
                lcdTheme = lcdTheme,
                title = screen.title,
                canGoBack = canGoBack,
                isPlaying = isPlaying
            )

            // 2. SCREEN CONTENT VIEW
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (screen) {
                    is IpodScreen.NowPlaying -> {
                        LcdNowPlayingView(
                            lcdTheme = lcdTheme,
                            song = currentSong,
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
                    is IpodScreen.YouTubeLogin -> {
                        LcdLoginView(
                            onLoginDone = onYoutubeLoginDone
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
                        lcdTheme.headerBackground.copy(alpha = 0.88f)
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
            // Left: Back Chevron Indicator
            if (canGoBack) {
                Icon(
                    imageVector = SimpIcons.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = lcdTheme.headerTextColor,
                    modifier = Modifier.size(13.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(13.dp))
            }

            // Center: Screen Title
            Text(
                text = title,
                color = lcdTheme.headerTextColor,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Right: Play State + Battery Meter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) SimpIcons.PlayArrow else SimpIcons.Pause,
                    contentDescription = "Status",
                    tint = lcdTheme.headerTextColor,
                    modifier = Modifier.size(13.dp)
                )

                // Authentic iPod Battery Gauge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(10.dp)
                            .border(1.dp, lcdTheme.headerTextColor, RoundedCornerShape(2.dp))
                            .padding(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(lcdTheme.headerTextColor))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(lcdTheme.headerTextColor))
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(lcdTheme.headerTextColor))
                        }
                    }
                    // Battery terminal nub
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(4.dp)
                            .background(
                                lcdTheme.headerTextColor,
                                RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp)
                            )
                    )
                }
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
    currentSong: Track?
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(selectedIndex.coerceIn(0, items.lastIndex))
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Menu Items List
        Box(
            modifier = Modifier
                .weight(if (isMainMenu) 0.58f else 1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items, key = { index, item -> "${item.title}_$index" }) { index, item ->
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
                        modifier = rowModifier
                            .clickable { item.action() }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
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
                                    color = if (isSelected) lcdTheme.highlightTextColor.copy(alpha = 0.85f) else lcdTheme.secondaryTextColor,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (item.hasSubmenu) {
                            Icon(
                                imageVector = SimpIcons.ArrowForwardIos,
                                contentDescription = "Submenu",
                                tint = if (isSelected) lcdTheme.highlightTextColor else lcdTheme.secondaryTextColor,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = lcdTheme.dividerColor.copy(alpha = 0.35f),
                        thickness = 0.5.dp
                    )
                }
            }

            // iPod Classic LCD Right-Side Scrollbar Indicator
            if (items.size > 5) {
                val totalItems = items.size
                val scrollRatio = selectedIndex.toFloat() / (totalItems - 1).coerceAtLeast(1)
                val verticalBias = (scrollRatio * 2f - 1f).coerceIn(-1f, 1f)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(lcdTheme.dividerColor.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight((1f / totalItems.coerceAtLeast(1)).coerceAtLeast(0.15f))
                            .align(BiasAlignment(1f, verticalBias))
                            .clip(RoundedCornerShape(2.dp))
                            .background(lcdTheme.secondaryTextColor.copy(alpha = 0.6f))
                    )
                }
            }
        }

        // Right Preview Section for iPod Main Menu
        if (isMainMenu) {
            Box(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .background(lcdTheme.screenBackground)
                    .border(width = 0.5.dp, color = lcdTheme.dividerColor),
                contentAlignment = Alignment.Center
            ) {
                val thumbnailUrl = currentSong?.thumbnails?.lastOrNull()?.url
                if (thumbnailUrl != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = "Album Art Preview",
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .aspectRatio(1f)
                                .shadow(6.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .border(0.5.dp, lcdTheme.dividerColor, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentSong.title,
                            color = lcdTheme.textColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = currentSong.artists?.firstOrNull()?.name ?: "",
                            color = lcdTheme.secondaryTextColor,
                            fontSize = 8.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(lcdTheme.dividerColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = SimpIcons.LibraryMusic,
                                contentDescription = "Music",
                                tint = lcdTheme.secondaryTextColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RetroPod",
                            color = lcdTheme.textColor.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LcdNowPlayingView(
    lcdTheme: LcdTheme,
    song: Track?,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?
) {
    if (song == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = SimpIcons.LibraryMusic,
                contentDescription = null,
                tint = lcdTheme.secondaryTextColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Song Playing",
                color = lcdTheme.textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select a track from Music or Search to start playback",
                color = lcdTheme.secondaryTextColor,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val rawThumb = song.thumbnails?.lastOrNull()?.url
    val thumbnailUrl = if (!rawThumb.isNullOrEmpty()) rawThumb else if (song.videoId.isNotEmpty()) "https://i.ytimg.com/vi/${song.videoId}/hqdefault.jpg" else null
    val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Section: Album Cover & Track Metadata
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Album Artwork Preview
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, lcdTheme.dividerColor, RoundedCornerShape(8.dp))
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
                        imageVector = SimpIcons.LibraryMusic,
                        contentDescription = null,
                        tint = lcdTheme.secondaryTextColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Track Title, Artist, Album, Format Badge
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
                        color = lcdTheme.secondaryTextColor.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quality Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(lcdTheme.dividerColor.copy(alpha = 0.25f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AAC 256kbps • Lossless",
                        color = lcdTheme.secondaryTextColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Synced Lyrics Box snippet
        if (!lyricsText.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(lcdTheme.dividerColor.copy(alpha = 0.25f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lyricsText,
                    color = lcdTheme.textColor,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Timeline Progress Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = lcdTheme.highlightBackgroundBottom,
                trackColor = lcdTheme.dividerColor.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPositionMs),
                    color = lcdTheme.secondaryTextColor,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "-${formatTime((durationMs - currentPositionMs).coerceAtLeast(0L))}",
                    color = lcdTheme.secondaryTextColor,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace
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
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Retro LCD Search Bar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(lcdTheme.screenBackground)
                .border(1.dp, lcdTheme.dividerColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = SimpIcons.Search,
                    contentDescription = null,
                    tint = lcdTheme.secondaryTextColor,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search YouTube Music...",
                            color = lcdTheme.secondaryTextColor.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = lcdTheme.textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(lcdTheme.textColor),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "✕",
                        color = lcdTheme.secondaryTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onSearchQueryChange("") }
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = lcdTheme.dividerColor, thickness = 0.5.dp)

        LcdMenuView(
            lcdTheme = lcdTheme,
            items = items,
            selectedIndex = selectedIndex,
            isMainMenu = false,
            currentSong = null
        )
    }
}

@Composable
private fun LcdLoginView(
    onLoginDone: (String) -> Unit
) {
    var isHandled by remember { mutableStateOf(false) }
    val state = rememberWebViewState()
    val loginUrl = "https://accounts.google.com/ServiceLogin?service=youtube&uilel=3&passive=true&continue=https%3A%2F%2Fmusic.youtube.com%2F"

    fun checkAndSubmitCookies() {
        if (isHandled) return
        try {
            val cookieManager = createWebViewCookieManager()
            val cookie = cookieManager.getCookie("https://music.youtube.com")
            if (cookie.isNotEmpty() && (cookie.contains("SAPISID") || cookie.contains("__Secure-3PAPISID") || cookie.contains("SID") || cookie.contains("HSID"))) {
                isHandled = true
                onLoginDone(cookie)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        while (!isHandled) {
            delay(1500)
            checkAndSubmitCookies()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PlatformWebView(
            state = state,
            initUrl = loginUrl,
            aboveContent = {},
            onPageFinished = { url ->
                if (url.contains("music.youtube.com")) {
                    checkAndSubmitCookies()
                }
            }
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
