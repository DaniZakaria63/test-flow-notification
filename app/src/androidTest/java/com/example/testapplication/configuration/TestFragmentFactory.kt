package com.example.testapplication.configuration

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.testapplication.ui.list.ListFragment
import com.example.testapplication.ui.main.MainFragment
import com.example.testapplication.ui.main.MainViewModel

class TestFragmentFactory constructor(
    private val mainViewModel: MainViewModel
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MainFragment::class.java.name -> MainFragment(mainViewModel)
            ListFragment::class.java.name -> ListFragment(mainViewModel)
            else -> super.instantiate(classLoader, className)
        }
    }
}