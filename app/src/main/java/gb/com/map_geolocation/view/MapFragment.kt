package gb.com.map_geolocation.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import gb.com.map_geolocation.R
import gb.com.map_geolocation.databinding.FragmentMapBinding
import gb.com.map_geolocation.model.LocationState
import gb.com.map_geolocation.utils.MapManager
import gb.com.map_geolocation.utils.PermissionHandler
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private val model: MapViewModel by viewModel()
    private val mapManager by lazy { MapManager(requireContext()) }
    private lateinit var permissionHandler: PermissionHandler

    private var locationLayerAdded = false
    private var isMapInitialized = false

    private val tapListener = GeoObjectTapListener { geoObjectTapEvent ->
        val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        binding.mapView.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        false
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val street = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET)}
                ?.name ?: "Информация об улице не найдена"

            Toast.makeText(requireContext(), street, Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: Error) {
        }
    }

    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            Log.d("@@@", "Tap detected at $point")
            mapView.map.deselectGeoObject()
            searchSession = searchManager.submit(point,20, SearchOptions(), searchListener)
            setMarker(point)
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
        }
    }

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject

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
        permissionHandler = PermissionHandler(this, model)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        mapView.map.addInputListener(inputListener)
        mapView.map.addTapListener(tapListener)

        initFABs()

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
        checkLocationPermission()
        return binding.root
    }

    private fun checkLocationPermission() {
        model.checkPermission(requireContext())
        model.isPermissionGranted.observe(viewLifecycleOwner) {isGranted ->
            if(isGranted) {
                model.getLocation()
            } else {
                permissionHandler.requestLocationPermission()
            }
        }
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
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createBitmapFromVector(art: Int) : Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0,0,canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun setMarker(point: Point) {
        val marker = createBitmapFromVector(R.drawable.ic_location)
        val imageProvider = ImageProvider.fromBitmap(marker)
        mapObjectCollection = binding.mapView.map.mapObjects
        placemarkMapObject = mapObjectCollection.addPlacemark(point, imageProvider)
    }
}