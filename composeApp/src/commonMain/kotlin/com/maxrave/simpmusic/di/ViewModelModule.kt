package com.maxrave.simpmusic.di

import com.maxrave.simpmusic.viewModel.AlbumViewModel
import com.maxrave.simpmusic.viewModel.ArtistViewModel
import com.maxrave.simpmusic.viewModel.HomeViewModel
import com.maxrave.simpmusic.viewModel.LibraryDynamicPlaylistViewModel
import com.maxrave.simpmusic.viewModel.LibraryViewModel
import com.maxrave.simpmusic.viewModel.LocalPlaylistViewModel
import com.maxrave.simpmusic.viewModel.LogInViewModel
import com.maxrave.simpmusic.viewModel.PlaylistViewModel
import com.maxrave.simpmusic.viewModel.SearchViewModel
import com.maxrave.simpmusic.viewModel.SettingsViewModel
import com.maxrave.simpmusic.viewModel.SharedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LogInViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
            )
        }
    }
