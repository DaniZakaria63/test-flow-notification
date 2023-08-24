package com.example.testapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.detail.DefaultDetailViewModel
import com.example.testapplication.ui.main.DefaultMainViewModel
import com.example.testapplication.util.DefaultViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [DefaultViewModelFactory::class]
)
class TestViewModelFactory {

    @Provides
    fun provideMainViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return DefaultViewModelFactory.MainViewModelFactory(repository, dispatcher)
    }

    @Provides
    fun provideDetailViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return DefaultViewModelFactory.DetailViewModelFactory(repository, dispatcher)
    }

    class MainViewModelFactory(
        val repository: DataRepository,
        val dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DefaultMainViewModel(repository, dispatcher) as T
        }
    }

    class DetailViewModelFactory(
        val repository: DataRepository,
        val dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DefaultDetailViewModel(repository, dispatcher) as T
        }
    }
}