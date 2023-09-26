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
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.FragmentMapBinding
import gb.com.map_geolocation.utils.MapManager

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var lastKnownLocation: Location
    private val model: MapViewModel by lazy { MapViewModel() }
    private val mapManager by lazy { MapManager(requireContext()) }

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

        model.isPermissionGranted.observe(viewLifecycleOwner) { isGranted ->
            if (isGranted) {
                val locationManager = MapKitFactory.getInstance().createLocationManager()

                locationManager.requestSingleUpdate(object : LocationListener{
                    override fun onLocationUpdated(location: Location) {
                        initializeMap(location)
                    }

                    override fun onLocationStatusUpdated(p0: LocationStatus) {
                    }
                })
            } else {
                showLocationPermissionDialog()
            }
        }
        model.checkPermission(requireContext())

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
            initializeMap(lastKnownLocation)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if(isGranted) {
            initializeMap(lastKnownLocation)
        } else {
            Snackbar.make(binding.root,
                "Location permission is required for this features",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun initializeMap(location: Location?) {
        if (location != null) {
            mapView
                .map
                .move(CameraPosition(Point(location.position.latitude, location.position.longitude),
                    150.0f, 0.0f, 0.0f))
        }
        var locationOnMap = MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)
        locationOnMap.isVisible = true
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