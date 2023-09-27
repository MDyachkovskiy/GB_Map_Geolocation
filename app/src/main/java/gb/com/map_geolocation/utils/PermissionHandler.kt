package gb.com.map_geolocation.utils

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import gb.com.map_geolocation.view.MapViewModel

class PermissionHandler(
    private val fragment: Fragment,
    private val viewModel: MapViewModel

) {

    init {
        observePermissionState()
    }

    private fun observePermissionState() {
        viewModel.isPermissionGranted.observe(fragment.viewLifecycleOwner){isGranted ->
            if(isGranted) {
                onPermissionGranted()
            } else {
                showPermissionExplanationDialog()
            }
        }
    }

    fun requestLocationPermission() {
        showPermissionExplanationDialog()
    }

    private val requestPermissionLauncher = fragment
        .registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if(isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()

        }
    }

    private fun onPermissionDenied() {
        Snackbar.make(fragment.requireView(),
            "Location permission is required for this features",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onPermissionGranted() {
        viewModel.getLocation()
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Select Location Accuracy")
            .setMessage("Do you want precise or coarse location?")
            .setPositiveButton("Precise") {_,_ ->
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Coarse") {_,_ ->
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            .show()
    }

    private fun requestPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }
}