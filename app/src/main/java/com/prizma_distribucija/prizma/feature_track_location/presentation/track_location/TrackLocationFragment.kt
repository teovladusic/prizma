package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.collectLatestLifecycleFlow
import com.prizma_distribucija.prizma.databinding.FragmentTrackLocationBinding
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import com.prizma_distribucija.prizma.feature_track_location.domain.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackLocationFragment : Fragment(R.layout.fragment_track_location) {

    private val viewModel: TrackLocationViewModel by viewModels()

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var googleMapsManager: GoogleMapManager

    private var _binding: FragmentTrackLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var bearingCalculator: BearingCalculator

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var map: GoogleMap? = null

    override fun onResume() {
        super.onResume()
        map?.let { it ->
            googleMapsManager.drawPolyLineBetweenAllPathPoints(it, viewModel.locations.value)
        }
        bearingCalculator.registerListener()
    }

    override fun onPause() {
        bearingCalculator.unregisterListener()
        super.onPause()
    }


    @SuppressLint("SetTextI18n")
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTrackLocationBinding.bind(view)

        bearingCalculator = BearingCalculator(requireContext())

        setUpGoogleMap()

        requestPermissions()

        binding.btnStopStart.setOnClickListener {
            viewModel.onStartStopClicked(permissionManager.hasPermissions(requireContext()))
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.isTracking) { isTracking ->
            if (isTracking) {
                binding.btnStopStart.text = Constants.STOP_TRACKING_BUTTON_TEXT
            } else {
                binding.btnStopStart.text = Constants.START_TRACKING_BUTTON_TEXT
            }
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.trackLocationEvents) { event ->
            when (event) {
                is TrackLocationViewModel.TrackLocationEvents.RequestPermissions -> {
                    requestPermissions()
                }
                is TrackLocationViewModel.TrackLocationEvents.SendCommandToForegroundService -> {
                    sendCommandToTrackingForegroundService(event.action)
                }
            }
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.locations) { locations ->
            map?.let {
                googleMapsManager.onNewPathPoints(it, locations)
            }
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.distanceTravelled) { distance ->
            binding.tvDistance.text = "$distance km"
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.timePassed) { timePassed ->
            binding.tvTime.text = timePassed
        }
    }

    private fun setUpGoogleMap() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        supportMapFragment?.getMapAsync { googleMap ->
            map = googleMap
            googleMapsManager.setStyle(googleMap, requireContext())
        }
    }

    private fun sendCommandToTrackingForegroundService(action: String) =
        Intent(
            requireContext(),
            TrackingForegroundService::class.java
        ).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun requestPermissions() {
        permissionManager.requestPermissionsIfNeeded(requireActivity() as Context)
    }
}