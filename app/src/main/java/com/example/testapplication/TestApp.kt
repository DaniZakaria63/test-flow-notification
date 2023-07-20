package com.example.testapplication

import android.app.Application
import com.example.testapplication.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class TestApp : Application() {
    val repository : Repository by lazy { ServiceLocator.provideRepository(context = this) }
    val appCoroutine: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        appCoroutine.cancel()
    }
}