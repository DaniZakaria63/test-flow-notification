package com.example.testapplication.ui.detail

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.testapplication.BuildConfig.TAG
import com.example.testapplication.R
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.Meals
import com.example.testapplication.data.source.DummyNotificationHelper.Companion.NOTIFICATION_DEFAULT_IMG_URL
import com.example.testapplication.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private lateinit var adapter: IngredientAdapter
    private val binding: ActivityDetailBinding get() = _binding!!
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = IngredientAdapter()
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = this@DetailActivity
        }
        setContentView(binding.root)

        val mealId = intent.getIntExtra("ID", 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.imgHeader.setRenderEffect(
                RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.REPEAT)
            )
        }

        binding.rvIngredients.adapter = adapter
        binding.rvIngredients.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch { detailViewModel.callDetail(mealId) }

                launch {
                    detailViewModel.ingredientData.collect { data ->
                        adapter.updateData(data ?: listOf(Pair("-", "-")))
                    }
                }

                launch {
                    detailViewModel.mealData.collect {
                        processImage(it.strMealThumb)
                    }
                }

                launch {
                    detailViewModel.statusState.collect{ state ->
                        Log.d(TAG, "onCreate: State-> $state")
                        when(state){
                            is Result.Success -> processDone()
                            is Result.Error -> processError()
                            Result.Loading -> processLoading()
                        }
                    }
                }

                launch {
                    detailViewModel.updateClickedStatus(mealId)
                }
            }
        }
    }


    private fun processLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }


    private fun processError() {
        binding.progressBar.visibility = View.GONE
    }

    private fun processDone() {
        binding.progressBar.visibility = View.GONE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun processImage(url: String?) {
        Glide.with(this@DetailActivity)
            .load(url ?: NOTIFICATION_DEFAULT_IMG_URL)
            .into(binding.imgHeader)
            .onLoadFailed(this@DetailActivity.getDrawable(R.drawable.dummy))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}