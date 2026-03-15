package com.alif.dicodingevent.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alif.dicodingevent.adapter.EventAdapter
import com.alif.dicodingevent.databinding.FragmentSearchBinding
import com.alif.dicodingevent.ui.activity.MainActivity
import com.alif.dicodingevent.ui.view_model.EventViewModel
import com.alif.dicodingevent.ui.view_model.ViewModelFactory
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val eventAdapter = EventAdapter()
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSearchingEvent.apply {
            adapter = eventAdapter
            layoutManager = GridLayoutManager(requireActivity(), 2)
        }

        showLoading(false)
        observeEvents()

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    val keyword = searchView.text
                    searchBar.setText(keyword)
                    searchView.hide()

                    eventViewModel.searchEventByName(keyword.toString())
                    false
                }

            searchView.addTransitionListener { _, _, newState ->
                val activity = requireActivity() as MainActivity

                if (newState == SearchView.TransitionState.SHOWING) {
                    activity.setBottomNavigationVisibility(false)
                } else if (newState == SearchView.TransitionState.HIDDEN) {
                    activity.setBottomNavigationVisibility(true)
                }
            }
        }

        eventAdapter.setOnClickDetailCallback(object : EventAdapter.OnClickDetailCallback {
            override fun onClickDetail(idEvent: Int) {
                val toDetailFragment = SearchFragmentDirections.actionNavigationSearchToDetailFragment(idEvent)
                findNavController().navigate(toDetailFragment)
            }
        })

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeEvents() {
        eventViewModel.allEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)

            binding.tvEventNotFound.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeViewModel() {
        eventViewModel.apply {
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)

                if (isLoading) {
                    binding.tvEventNotFound.visibility = View.GONE
                } else {
                    binding.tvEventNotFound.visibility = View.VISIBLE
                }
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
            binding.apply {
                loadingLayout.progressBar.visibility = View.VISIBLE
                loadingLayout.tvLoading.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                loadingLayout.progressBar.visibility = View.GONE
                loadingLayout.tvLoading.visibility = View.GONE
            }
        }
    }
}