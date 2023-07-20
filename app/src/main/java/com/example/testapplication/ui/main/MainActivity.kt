package com.example.testapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.testapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

/*
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                mainViewModel.mealState.collect{ meals ->
                    Log.d("ASD", "onCreate: ${meals.strMeal}")
                }
            }
        }

        binding.button2.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.triggerPushOnlineNotif()
            }
        }
        */
    }
}