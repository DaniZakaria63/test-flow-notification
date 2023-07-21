package com.example.testapplication.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testapplication.TestApp
import com.example.testapplication.data.Repository

class ListViewModel(private val repository: Repository) : ViewModel() {


    companion object{
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository : Repository = (this[APPLICATION_KEY] as TestApp).repository
                ListViewModel(repository)
            }
        }
    }
}