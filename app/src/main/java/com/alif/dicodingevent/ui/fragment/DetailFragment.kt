package com.alif.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.alif.dicodingevent.R
import com.alif.dicodingevent.data.response.ListEventsItem
import com.alif.dicodingevent.databinding.FragmentDetailBinding
import com.alif.dicodingevent.view_model.EventViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idEvent = DetailFragmentArgs.fromBundle(arguments as Bundle).idEvent

        eventViewModel.apply {
            if (event.value == null) {
                getDetailEventById(idEvent)
            }

            event.observe(viewLifecycleOwner) { event ->
                setEventData(event!!)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    private fun setEventData(event: ListEventsItem) {
        binding.apply {
            Glide.with(requireActivity())
                .load(event.mediaCover)
                .into(imgCoverEvent)
            tvEventTitle.text = event.name
            tvEventDate.text = event.beginTime
            tvEventQuota.text = getString(R.string.event_quota, (event.quota - event.registrants).toString())
            tvOwnerName.text = getString(R.string.event_owner, event.ownerName)
            tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnRegistration.setOnClickListener {
                val launcherIntentBrowser = Intent(Intent.ACTION_VIEW, event.link.toUri())
                startActivity(launcherIntentBrowser)
            }
        }
    }
}