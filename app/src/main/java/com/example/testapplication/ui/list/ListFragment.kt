package com.example.testapplication.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.data.Status
import com.example.testapplication.databinding.FragmentListBinding
import com.example.testapplication.ui.main.MainViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListFragment @Inject constructor(var _mainViewModel: MainViewModel?) : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel get()= _mainViewModel!!
//    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var listAdapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainViewModel = _mainViewModel?:ViewModelProvider(requireActivity())[MainViewModel::class.java]
        listAdapter = ListAdapter { mealId ->
            mainViewModel.setIntentExtra("ID", mealId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = listAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.updateNotifySeenStatus()

                mainViewModel.allListData.collect { result ->
                    binding.swipeRefresh.isRefreshing = false
                    when (result.status) {
                        Status.ERROR -> showErrorDialog(Exception(""))
                        Status.LOADING -> showLoadingBar()
                        else -> listAdapter.updateData(
                            result.dataList?.toMutableList() ?: mutableListOf()
                        )
                    }
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.refreshNotificationList(refreshed = true)
        }
    }

    /*Error animation should belongs to activity*/
    private fun showErrorDialog(exception: Throwable) {
        //TODO: Put some error status
        exception.printStackTrace()
    }

    /*Loading animation should belongs to activity*/
    private fun showLoadingBar() {
        binding.swipeRefresh.isRefreshing = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        mainViewModel.clearNotificationList()
    }
}