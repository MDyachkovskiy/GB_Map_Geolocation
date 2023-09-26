package gb.com.map_geolocation.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var mapView: MapView

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey("54730c83-7ec0-4ecf-93da-c64ad0b860d6")
        MapKitFactory.initialize(requireContext())

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        showLocationPermissionDialog()

        return binding.root
    }

    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Select Location Accuracy")
            .setMessage("Do you want precise or coarse location?")
            .setPositiveButton("Precise") {_,_ ->
                requestLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Coarse") {_,_ ->
                requestLocationPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            .show()
    }

    private fun requestLocationPermission(permission: String) {
        if(ContextCompat.checkSelfPermission(requireContext(),permission) !=
            PackageManager.PERMISSION_GRANTED) {
           requestPermissionLauncher
        } else {
            initializeMap()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if(isGranted) {
            initializeMap()
        } else {
            Snackbar.make(binding.root,
                "Location permission is required for this features",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun initializeMap() {
        mapView
            .map
            .move(
                CameraPosition(
                    Point(55.755864, 37.617698),
                11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 10f), null)
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}