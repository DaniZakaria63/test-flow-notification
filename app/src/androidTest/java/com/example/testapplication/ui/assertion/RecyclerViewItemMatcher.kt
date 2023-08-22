package com.example.testapplication.ui.assertion

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher

class RecyclerViewItemMatcher(val ID:Int) : ViewAction {
    override fun getDescription(): String = "click on this view id $ID"

    override fun getConstraints(): Matcher<View> = any(View::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        val v: ViewGroup = view?.findViewById(ID) ?: throw NullPointerException("No such an element from this ID")
        v.performClick()
    }
}