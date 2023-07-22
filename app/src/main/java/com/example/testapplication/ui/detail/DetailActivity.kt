package com.example.testapplication.ui.detail

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.testapplication.R
import com.example.testapplication.TestApp
import com.example.testapplication.databinding.ActivityDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels { DetailViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealId = intent.getIntExtra("ID",0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.imgHeader.setRenderEffect(
                RenderEffect.createBlurEffect(20f, 20f,Shader.TileMode.REPEAT)
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                detailViewModel.callDetail(mealId)

                detailViewModel.exception.collect{ error ->
                    Log.e("ASD", "onCreate: $error")
                }
            }

            repeatOnLifecycle(Lifecycle.State.STARTED){
                detailViewModel.loading.collect{ isLoading ->
                    Log.d("ASD", "onCreate: Still Loading")
                }
            }
        }

    }
}