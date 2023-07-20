package com.example.testapplication.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.TestApp
import com.example.testapplication.databinding.ActivityMainBinding

/**
 * The layout gonna be something interesting
 * 1. Selection of button will be animation of dropping ball
 * 2. ball can be dragged and choose between three component
 * 3. each component will trigger their own functionality
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}