package com.alif.dicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.alif.dicodingevent.R
import com.alif.dicodingevent.data.Result
import com.alif.dicodingevent.data.local.entity.FavoriteEventEntity
import com.alif.dicodingevent.data.remote.response.ListEventsItem
import com.alif.dicodingevent.databinding.FragmentDetailBinding
import com.alif.dicodingevent.ui.view_model.EventViewModel
import com.alif.dicodingevent.ui.view_model.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

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
        var isFavorite: Boolean = false

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

            eventViewModel.isFavoriteEvent(event.id).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        isFavorite = result.data
                        imgFavoriteIcon.isEnabled = true

                        if (isFavorite) {
                            imgFavoriteIcon.setImageResource(R.drawable.ic_favorite_full)
                        } else {
                            imgFavoriteIcon.setImageResource(R.drawable.ic_favorite_border)
                        }
                    }
                    is Result.Loading -> {
                        imgFavoriteIcon.isEnabled = false
                    }
                    is Result.Error -> {
                        imgFavoriteIcon.isEnabled = true
                        Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            imgFavoriteIcon.setOnClickListener {
                if (isFavorite) {
                    eventViewModel.removeEventFromFavorite(event.id)
                } else {
                    val event = FavoriteEventEntity(
                        event.id,
                        event.name,
                        event.summary,
                        event.description,
                        event.imageLogo,
                        event.mediaCover,
                        event.category,
                        event.ownerName,
                        event.cityName,
                        event.quota,
                        event.registrants,
                        event.beginTime,
                        event.endTime,
                        event.link
                    )
                    eventViewModel.addToFavoriteEvent(event)
                }
            }
        }
    }
}