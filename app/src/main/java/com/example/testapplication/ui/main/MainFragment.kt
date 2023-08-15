package com.example.testapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.testapplication.databinding.FragmentMainBinding
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
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.triggerOfflineNotification()
            }
        }

        binding.button2.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.triggerPushOnlineNotify()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}