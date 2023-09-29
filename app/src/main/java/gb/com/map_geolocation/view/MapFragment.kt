package gb.com.map_geolocation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.FragmentMapBinding
import gb.com.map_geolocation.model.LocationState
import gb.com.map_geolocation.model.datasource.MapManager
import gb.com.map_geolocation.utils.FabHandler
import gb.com.map_geolocation.utils.PermissionHandler
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mapManager: MapManager
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var fabHandler: FabHandler

    private val model: MapViewModel by viewModel()

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
        setMapKit()

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        permissionHandler = PermissionHandler(this, model)
        mapManager = MapManager(requireContext(),mapView, model, viewLifecycleOwner)
        fabHandler = FabHandler(binding, mapView, model, viewLifecycleOwner, requireContext())

        initObservers()
        initPlacemarksList()
        return binding.root
    }

    private fun initPlacemarksList() {
        val adapter = PlacemarkAdapter(listOf())
        binding.placemarksList.layoutManager = LinearLayoutManager(requireContext())
        binding.placemarksList.adapter = adapter

        model.placemarks.observe(viewLifecycleOwner) {placemarks ->
            (binding.placemarksList.adapter as PlacemarkAdapter).updatePlacemarks(placemarks)
        }

        adapter.onDeleteClickListener = { placemark ->
            model.deletePlacemark(placemark)
        }

        adapter.onUpdateClickListener = { placemark ->
            model.updatePlacemark(placemark)
        }
    }

    private fun initObservers() {
        model.locationState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is LocationState.Success -> mapManager.initializeMap(state.location)
                is LocationState.Error -> showError(state.message)
                else -> {}
            }
        }
        model.checkPermission(requireContext())
        model.isPermissionGranted.observe(viewLifecycleOwner) {isGranted ->
            if(isGranted) {
                model.getLocation()
            } else {
                permissionHandler.requestLocationPermission()
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setMapKit() {
        MapKitFactory.setApiKey("54730c83-7ec0-4ecf-93da-c64ad0b860d6")
        MapKitFactory.initialize(requireContext())
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}