package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.core.util.collectLatestLifecycleFlow
import com.prizma_distribucija.prizma.databinding.FragmentTrackLocationBinding
import com.prizma_distribucija.prizma.databinding.LoadingDialogBinding
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import com.prizma_distribucija.prizma.feature_track_location.domain.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
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

    private lateinit var loadingDialog: AlertDialog
    private var _loadingDialogBinding: LoadingDialogBinding? = null
    private val loadingDialogBinding get() = _loadingDialogBinding!!

    lateinit var workManager: WorkManager

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
        _loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)

        bearingCalculator = BearingCalculator(requireContext())

        setUpGoogleMap()

        requestPermissions()

        createLoadingDialog()

        workManager = WorkManager.getInstance(requireActivity().application)

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

        requireActivity().collectLatestLifecycleFlow(viewModel.savingStatus) { event ->
            when (event) {
                is Resource.Loading -> {
                    onSaveLoading()
                }

                is Resource.Success -> {
                    onSaveSuccess()
                }

                is Resource.Error -> {
                    onSaveError(event.message ?: "Unknown error appeared")
                }
            }
        }

        requireActivity().collectLatestLifecycleFlow(viewModel.distanceMarkerPoints) { markerPoints ->
            map?.let { googleMap ->
                googleMapsManager.drawMarkerPoints(googleMap, markerPoints, requireContext())
            }
        }

    }

    private fun createLoadingDialog() {
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(loadingDialogBinding.root)
            .setCancelable(false)
            .setNegativeButton("Posalji kasnije") { _, _ ->
                viewModel.onSendLaterClick(workManager)
            }
            .create()
    }

    @SuppressLint("SetTextI18n")
    private fun onSaveLoading() {
        loadingDialogBinding.textView2.text = "Saving... Please wait"
        loadingDialog.show()
    }

    private fun onSaveSuccess() {
        loadingDialog.dismiss()
        binding.constraintLayoutSaved.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            requireActivity().onBackPressed()
        }
    }

    private fun onSaveError(error: String) {
        loadingDialog.dismiss()
        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
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