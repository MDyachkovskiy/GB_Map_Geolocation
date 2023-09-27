package gb.com.map_geolocation.utils

import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.FragmentMapBinding
import gb.com.map_geolocation.view.MapViewModel

class FabHandler(
    private val binding: FragmentMapBinding,
    private val mapView: MapView,
    private val model: MapViewModel
) {
    init{
        initializeFabs()
    }

    private fun initializeFabs() {
        binding.fabLocate.setOnClickListener {
            model.getLocation()
        }

        binding.fabZoomIn.setOnClickListener {
            adjustZoom(1)
        }

        binding.fabZoomOut.setOnClickListener {
            adjustZoom(-1)
        }
    }

    private fun adjustZoom(zoom: Int) {
        val cameraPosition = mapView.map.cameraPosition
        val newZoomLevel = cameraPosition.zoom + zoom
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