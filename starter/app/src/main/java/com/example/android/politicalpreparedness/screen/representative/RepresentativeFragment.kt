package com.example.android.politicalpreparedness.screen.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.screen.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.PermissionUtils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class RepresentativeFragment : BaseFragment() {

    override val viewModel: RepresentativeViewModel by viewModel()
    private val uiHandler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_representative,
            container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.spinnerStates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.updateState(binding.spinnerStates.selectedItem as String)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updateState(binding.spinnerStates.selectedItem as String)
            }
        }

        val representativeListAdapter = RepresentativeListAdapter()
        binding.rvRepresentations.adapter = representativeListAdapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativeListAdapter.submitList(representatives)
        })

        binding.btnFindLocation.setOnClickListener {
            requestUserLocationAndFetchAddress()
        }
        binding.btnSearchRepresentatives.setOnClickListener {
            viewModel.findMyRepresentatives()
        }
    }

    private fun isFineLocationPermissionGranted(): Boolean {
        return PermissionUtils.isGranted(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun requestUserLocationAndFetchAddress() {
        when {
            isFineLocationPermissionGranted() -> {
                enableLocationServiceAndGetUserLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.location_permission_required_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_PERMISSION
                        )
                    }.show()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }


    /*
  *  Uses the Location Client to check the current state of location settings, and gives the user
  *  the opportunity to turn on location services within our app.
  */
    private fun enableLocationServiceAndGetUserLocation(needResolve: Boolean = true) {
        Timber.d("Come enableLocationServiceAndGetUserLocation")
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_LOW_POWER
            }
        )
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && needResolve) {
                startIntentSenderForResult(
                    exception.resolution.intentSender,
                    REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null
                )
            } else {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.location_services_denied_explanation, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    enableLocationServiceAndGetUserLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                retrieveLocationRetry = 0
                fetchLocationAndUpdateAddress()
            }
        }
    }

    var retrieveLocationRetry = 0
    val RETRIE_LOCATION_MAXIMUM = 5
    val RETRIE_LOCATION_DELAY = 1000L

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndUpdateAddress() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val address = geoCodeLocation(location)
                    viewModel.updateAddress(address)
                    selectSpinnerState(address)
                    retrieveLocationRetry = 0
                } else {
                    Timber.e("Location is null!")
                    // Sometime, it can't get location immediately, need some delay
                    if (retrieveLocationRetry < RETRIE_LOCATION_MAXIMUM) {
                        retrieveLocationRetry += 1
                        uiHandler.postDelayed({
                            fetchLocationAndUpdateAddress()
                        }, RETRIE_LOCATION_DELAY)
                    }
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun selectSpinnerState(address: Address) {
        val states = resources.getStringArray(R.array.states)
        binding.spinnerStates.setSelection(
            if (states.contains(address.state)) {
                states.indexOf(address.state)
            } else {
                0
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TURN_DEVICE_LOCATION_ON -> {
                enableLocationServiceAndGetUserLocation(false)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                // Check if location permissions are granted and if so enable the
                // location data layer.
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestUserLocationAndFetchAddress()
                } else {
                    // Show messages to telling the user why your app actually requires the location permission.
                    // In case they previously chose "Deny & don't ask again",
                    // tell your users where to manually enable the location permission.
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        R.string.location_permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 1002
    }
}