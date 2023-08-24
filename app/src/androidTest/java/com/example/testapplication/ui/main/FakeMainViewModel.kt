package com.example.testapplication.ui.main

import androidx.lifecycle.ViewModel
import com.example.testapplication.DispatcherProvider
import com.example.testapplication.data.source.DataRepository

class FakeMainViewModel constructor(
    private val repository: DataRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

}