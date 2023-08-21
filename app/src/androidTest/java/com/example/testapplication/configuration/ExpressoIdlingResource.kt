package com.example.testapplication.configuration

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object ExpressoIdlingResource {
    private val RESOURCE = "GLOBAL"
    private val countingIdlingResource = CountingIdlingResource(RESOURCE)

    val idlingResource: IdlingResource get() = countingIdlingResource

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        countingIdlingResource.decrement()
    }
}