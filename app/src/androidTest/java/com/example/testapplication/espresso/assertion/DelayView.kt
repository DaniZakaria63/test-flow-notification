package com.example.testapplication.espresso.assertion

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class DelayView(val time: Long) : ViewAction {
    override fun getDescription(): String = "Wait for delay"

    override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.loopMainThreadForAtLeast(time)
    }
}