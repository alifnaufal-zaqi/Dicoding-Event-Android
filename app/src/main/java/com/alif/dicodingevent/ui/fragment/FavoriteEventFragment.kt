package com.alif.dicodingevent.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alif.dicodingevent.R
import com.alif.dicodingevent.adapter.EventAdapter
import com.alif.dicodingevent.data.remote.response.ListEventsItem
import com.alif.dicodingevent.databinding.FragmentFavoriteEventBinding
import com.alif.dicodingevent.ui.view_model.EventViewModel
import com.alif.dicodingevent.ui.view_model.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteEventFragment : Fragment() {

    private var _binding: FragmentFavoriteEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val eventAdapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEventFavorite.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = eventAdapter
        }

        eventAdapter.setOnClickDetailCallback(object : EventAdapter.OnClickDetailCallback {
            override fun onClickDetail(idEvent: Int) {
                val toDetailFragment = FavoriteEventFragmentDirections.actionFavoriteEventFragmentToDetailFragment(idEvent)
                findNavController().navigate(toDetailFragment)
            }
        })

        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        eventViewModel.apply {
            if (favoriteEvents.value == null) {
                getFavoriteEvents()
            }

            favoriteEvents.observe(viewLifecycleOwner) { events ->
                val mappedEvents = events.map {
                    ListEventsItem(it.summary, it.mediaCover, it.registrants, it.imageLogo, it.link, it.description, it.ownerName, it.cityName, it.quota, it.name, it.id, it.beginTime, it.endTime, it.category)
                }
                eventAdapter.submitList(mappedEvents)
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