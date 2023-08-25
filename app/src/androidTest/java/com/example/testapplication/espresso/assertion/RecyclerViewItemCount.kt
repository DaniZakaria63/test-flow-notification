package com.example.testapplication.espresso.assertion

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`

class RecyclerViewItemCount private constructor(
    private val matcher: Matcher<Int>
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        val recyclerView = view as? RecyclerView
        val adapter = recyclerView?.adapter
        assertThat(adapter?.itemCount, matcher)
    }

    companion object {
        fun withItemCount(expextedCount: Int): RecyclerViewItemCount {
            return withItemCount(`is`(expextedCount))
        }

        fun withItemCount(matcher: Matcher<Int>): RecyclerViewItemCount {
            return RecyclerViewItemCount(matcher)
        }
    }
}