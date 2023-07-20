package com.example.testapplication

import android.app.Application
import com.example.testapplication.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class TestApp : Application() {
    val repository : Repository get() = ServiceLocator.provideRepository(context = this)
    val appCoroutine: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onLowMemory() {
        super.onLowMemory()
        appCoroutine.cancel()
    }
}