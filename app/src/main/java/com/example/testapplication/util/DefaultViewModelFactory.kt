package com.example.testapplication.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.source.DataRepository
import com.example.testapplication.ui.detail.DefaultDetailViewModel
import com.example.testapplication.ui.main.DefaultMainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Named

//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class MainViewModelFactory_
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class DetailViewModelFactory_

@Module
@InstallIn(ActivityRetainedComponent::class)
object DefaultViewModelFactory {

    @Provides
    @Named("MainViewModelFactory")
    fun provideMainViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return MainViewModelFactory(repository, dispatcher)
    }

    @Provides
    @Named("DetailViewModelFactory")
    fun provideDetailViewModelFactory(
        repository: DataRepository,
        dispatcher: DispatcherProvider
    ): ViewModelProvider.Factory {
        return DetailViewModelFactory(repository, dispatcher)
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