package com.example.testapplication.ui.detail

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.testapplication.BuildConfig.TAG
import com.example.testapplication.data.Result
import com.example.testapplication.data.source.DummyNotificationHelper.Companion.NOTIFICATION_DEFAULT_IMG_URL
import com.example.testapplication.databinding.ActivityDetailBinding
import com.example.testapplication.ui.base.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding: ActivityDetailBinding get() = _binding!!

    @Inject
    @Named("DetailViewModelFactory")
    lateinit var factory: ViewModelProvider.Factory
    private val detailViewModel: DetailViewModel by viewModels(factoryProducer = {factory})

    @Inject
    lateinit var glideInstance: RequestManager
    private lateinit var adapter: IngredientAdapter

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
        if (mealId < 1) throw IllegalAccessException("Make sure parameter is correct")

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
                    detailViewModel.mealData.collect {
                        processImage(it.strMealThumb)
                    }
                }

            }
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailViewModel.ingredientData.collect { data ->
                        adapter.updateData(data ?: listOf(Pair("-", "-")))
                    }
                }

                launch {
                    detailViewModel.updateClickedStatus(mealId)
                }

                launch {
                    detailViewModel.statusState.collect { state ->
                        when (state) {
                            is Result.Success -> processDone()
                            is Result.Error -> processError()
                            Result.Loading -> processLoading()
                        }
                    }
                }
            }
        }
    }


    private fun processLoading() {
        Log.d(TAG, "processLoading: Still Loading")
    }


    private fun processError() {
        Log.d(TAG, "processLoading: Process Error")
    }

    private fun processDone() {
        Log.d(TAG, "processLoading: Process Already Done")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun processImage(url: String?) {
        glideInstance.load(url ?: NOTIFICATION_DEFAULT_IMG_URL)
            .into(binding.imgHeader)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}