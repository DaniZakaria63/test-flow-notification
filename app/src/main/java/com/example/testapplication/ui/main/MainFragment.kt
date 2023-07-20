package com.example.testapplication.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testapplication.R
import com.example.testapplication.TestApp
import com.example.testapplication.databinding.FragmentMainBinding
import com.example.testapplication.ui.list.ListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The layout gonna be something interesting
 * 1. Selection of button will be animation of dropping ball
 * 2. ball can be dragged and choose between three component
 * 3. each component will trigger their own functionality
 */
class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var coroutineScope : CoroutineScope
    private val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coroutineScope = (requireActivity().application as TestApp).appCoroutine
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

        binding.button2.setOnClickListener {
            coroutineScope.launch {
                mainViewModel.triggerPushOnlineNotif()
            }
        }
        /*
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED){
                        mainViewModel.mealState.collect{ meals ->
                            Log.d("ASD", "onCreate: ${meals.strMeal}")
                        }
                    }
                }

                */

        binding.button3.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToListFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}