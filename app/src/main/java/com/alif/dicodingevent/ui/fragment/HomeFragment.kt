package com.alif.dicodingevent.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alif.dicodingevent.adapter.EventAdapter
import com.alif.dicodingevent.databinding.FragmentHomeBinding
import com.alif.dicodingevent.utils.EventType
import com.alif.dicodingevent.view_model.EventViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val activeEventAdapter = EventAdapter()
    private val finishedEventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEventUpcoming.adapter = activeEventAdapter
        binding.rvEventUpcoming.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvEventFinished.adapter = finishedEventAdapter
        binding.rvEventFinished.layoutManager = GridLayoutManager(requireActivity(), 2)

        activeEventAdapter.setOnClickDetailCallback(object : EventAdapter.OnClickDetailCallback {
            override fun onClickDetail(idEvent: Int) {
                navigateToDetailFragment(idEvent)
            }
        })

        finishedEventAdapter.setOnClickDetailCallback(object : EventAdapter.OnClickDetailCallback {
            override fun onClickDetail(idEvent: Int) {
                navigateToDetailFragment(idEvent)
            }
        })

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToDetailFragment(idEvent: Int) {
        val toDetailFragment = HomeFragmentDirections.actionNavigationHomeToDetailFragment(idEvent)
        findNavController().navigate(toDetailFragment)
    }

    private fun observeViewModel() {
        eventViewModel.apply {
            if (activeComingEvents.value == null) {
                getEvents(EventType.ACTIVE)
            }

            if (finishedEvents.value == null) {
                getEvents(EventType.FINISHED)
            }

            activeComingEvents.observe(viewLifecycleOwner) { events ->
                activeEventAdapter.submitList(events.take(5))
            }

            finishedEvents.observe(viewLifecycleOwner) { events ->
                finishedEventAdapter.submitList(events.take(5))
            }

            isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }

            responseMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayoutFinishedEvent.apply {
                progressBar.visibility = View.VISIBLE
                tvLoading.visibility = View.VISIBLE
            }

            binding.loadingLayoutUpcomingEvent.apply {
                progressBar.visibility = View.VISIBLE
                tvLoading.visibility = View.VISIBLE
            }
        } else {
            binding.loadingLayoutFinishedEvent.apply {
                progressBar.visibility = View.GONE
                tvLoading.visibility = View.GONE
            }

            binding.loadingLayoutUpcomingEvent.apply {
                progressBar.visibility = View.GONE
                tvLoading.visibility = View.GONE
            }
        }
    }
}