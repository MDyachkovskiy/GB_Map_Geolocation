package gb.com.map_geolocation.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import gb.com.map_geolocation.model.LocationState
import gb.com.map_geolocation.utils.MapManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private val model: MapViewModel by viewModel()
    private val mapManager by lazy { MapManager(requireContext()) }
    private var locationLayerAdded = false
    private var isMapInitialized = false

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
        mapManager.initializeMapKit()
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        initFABs()

        model.isPermissionGranted.observe(viewLifecycleOwner) { isGranted ->
            Log.d("@@@", "Permissions Granted: $isGranted")
            if (isGranted) {
                Log.d("@@@", "Requesting location")
                model.getLocation()
            } else {
                showLocationPermissionDialog()
            }
        }
        model.locationState.observe(viewLifecycleOwner) { state ->
            Log.d("@@@", "Location State: $state")
            when(state) {
                is LocationState.Success -> {
                    val location = state.location
                    initializeMap(location)
                }
                is LocationState.Error -> {
                    Snackbar.make(binding.root,
                        state.message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
        model.checkPermission(requireContext())

        return binding.root
    }

    private fun initFABs() {
        binding.fabLocate.setOnClickListener {
            model.getLocation()
        }

        binding.fabZoomIn.setOnClickListener {
            val cameraPosition = mapView.map.cameraPosition
            val newZoomLevel = cameraPosition.zoom + 1
            val newCameraPosition = CameraPosition(
                cameraPosition.target, newZoomLevel,
                cameraPosition.azimuth, cameraPosition.tilt
            )
            mapView.map.move(
                newCameraPosition,
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }

        binding.fabZoomOut.setOnClickListener {
            val cameraPosition = mapView.map.cameraPosition
            val newZoomLevel = cameraPosition.zoom - 1
            val newCameraPosition = CameraPosition(
                cameraPosition.target, newZoomLevel,
                cameraPosition.azimuth, cameraPosition.tilt
            )
            mapView.map.move(
                newCameraPosition,
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }
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
           requestPermissionLauncher.launch(permission)
        } else {
            model.getLocation()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if(isGranted) {
            model.getLocation()
        } else {
            Snackbar.make(binding.root,
                "Location permission is required for this features",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun initializeMap(location: Location?) {
        Log.d("@@@", "Initialize Map with Location: ${location?.latitude}, ${location?.longitude}")
        val currentZoom: Float = if (isMapInitialized) {
            mapView.map.cameraPosition.zoom
        } else {
            isMapInitialized = true
            15.0f
        }
        location?.let {
            mapView.map
                .move(CameraPosition(Point(it.latitude, it.longitude),
                    currentZoom, 0.0f, 0.0f))
        }

        if(!locationLayerAdded) {
            val locationOnMap = MapKitFactory
                .getInstance()
                .createUserLocationLayer(binding.mapView.mapWindow)
            locationOnMap.isVisible = true
            locationLayerAdded = true
        }
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