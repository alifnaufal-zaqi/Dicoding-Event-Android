package com.alif.dicodingevent.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alif.dicodingevent.adapter.EventAdapter
import com.alif.dicodingevent.databinding.FragmentActiveEventBinding
import com.alif.dicodingevent.utils.EventType
import com.alif.dicodingevent.view_model.EventViewModel
import com.google.android.material.snackbar.Snackbar

class ActiveEventFragment : Fragment() {

    private var _binding: FragmentActiveEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val eventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEventUpcoming.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = eventAdapter
        }

        eventAdapter.setOnClickDetailCallback(object : EventAdapter.OnClickDetailCallback {
            override fun onClickDetail(idEvent: Int) {
                val toDetailFragment = ActiveEventFragmentDirections.actionNavigationActiveEventToDetailFragment(idEvent)
                findNavController().navigate(toDetailFragment)
            }
        })

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        eventViewModel.apply {
            if (activeComingEvents.value == null) {
                getEvents(EventType.ACTIVE)
            }

            activeComingEvents.observe(viewLifecycleOwner) { events ->
                eventAdapter.submitList(events)
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
            binding.loadingView.apply {
                progressBar.visibility = View.VISIBLE
                tvLoading.visibility = View.VISIBLE
            }
        } else {
            binding.loadingView.apply {
                progressBar.visibility = View.GONE
                tvLoading.visibility = View.GONE
            }
        }
    }
}