package com.example.testapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.testapplication.BuildConfig.TAG
import com.example.testapplication.TestApp
import com.example.testapplication.api.Meals
import com.example.testapplication.data.Result
import com.example.testapplication.data.model.NotificationModel
import com.example.testapplication.databinding.FragmentMainBinding
import com.example.testapplication.data.source.DummyNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * The layout gonna be something interesting
 * 1. Selection of button will be animation of dropping ball
 * 2. ball can be dragged and choose between three component
 * 3. each component will trigger their own functionality
 */
class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                Log.d("ASD", "Lifecycle State Created")
                mainViewModel.mealState.collect { meals ->
                    when (meals) {
                        is Result.Success -> createNotification(meals.data)
                        is Result.Error -> handleError(Exception(meals.exception))
                        Result.Loading -> handleLoading()
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            createNotification(Meals(0))
        }

        binding.button2.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.triggerPushOnlineNotif()
            }
        }

        binding.button3.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToListFragment()
            findNavController().navigate(action)
        }
    }


    /*to produce create notification inside MainActivity
    * args: meals: Meals [MainViewModel->mealState] [DummyButton]
    * give: formatted notification object that will pass to [MainViewModel->triggerNotification]
    * ps, notification id in this section is not necessary because not provided to database*/
    private fun createNotification(meals: Meals){
        val notification : NotificationModel = if(meals.idMeal == 0)
            DummyNotificationHelper().getOne()
        else meals.asNotificationModel()

        lifecycleScope.launch {
            Log.d(TAG, "create notification trigger")
            mainViewModel.triggerNotification(notification)
        }
    }


    /* internal fragment error handling
    * args: error: Exception [MainViewModel->mealsState]
    * */
    private fun handleError(error: Exception){

    }

    /* internal fragment loading animation
    * */
    private fun handleLoading() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}