package com.example.testapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.detail.FakeDetailViewModel
import com.example.testapplication.ui.main.FakeMainViewModel
import com.example.testapplication.util.DefaultViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named


@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [DefaultViewModelFactory::class]
)
object TestViewModelProvider{

    @Provides
    @Named("MainViewModelFactory")
    fun provideMainViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return FakeMainViewModelFactory(repository, dispatcher)
    }

    @Provides
    @Named("DetailViewModelFactory")
    fun provideDetailViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return FakeDetailViewModelFactory(repository, dispatcher)
    }

    class FakeMainViewModelFactory(
        val repository: DataRepository,
        val dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FakeMainViewModel(repository, dispatcher) as T
        }
    }

    class FakeDetailViewModelFactory(
        val repository: DataRepository,
        val dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FakeDetailViewModel(repository, dispatcher) as T
        }
    }
}