package gb.com.map_geolocation.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
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

class MapManager(
    private val context: Context,
    private val mapView: MapView
    ) {

    private var locationLayerAdded = false
    private var isMapInitialized = false

    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject


    private val tapListener = GeoObjectTapListener { geoObjectTapEvent ->
        val geoObject: GeoObject = geoObjectTapEvent.geoObject
        val objectName = geoObject.name ?: "Название не найдено"
        Toast.makeText(context, objectName, Toast.LENGTH_SHORT).show()
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

            Toast.makeText(context, street, Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: Error) {
        }
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            Log.d("@@@", "Tap detected at $point")

            searchSession = searchManager.submit(point,20, SearchOptions(), searchListener)
            setMarker(point)
        }

        override fun onMapLongTap(map: Map, point: Point) {
        }
    }

    fun initializeMap(location: Location?) {
        Log.d("@@@", "Initialize Map with Location: ${location?.latitude}, ${location?.longitude}")

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        mapView.map.addInputListener(inputListener)
        mapView.map.addTapListener(tapListener)

        val currentZoom: Float = if (isMapInitialized) {
            mapView.map.cameraPosition.zoom
        } else {
            isMapInitialized = true
            15.0f
        }
        location?.let {
            mapView.map
                .move(
                    CameraPosition(Point(it.latitude, it.longitude),
                    currentZoom, 0.0f, 0.0f)
                )
        }

        if(!locationLayerAdded) {
            val locationOnMap = MapKitFactory
                .getInstance()
                .createUserLocationLayer(mapView.mapWindow)
            locationOnMap.isVisible = true
            locationLayerAdded = true
        }
    }

    private fun createBitmapFromVector(art: Int) : Bitmap? {
        val drawable = ContextCompat.getDrawable(context, art) ?: return null
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
        if(::placemarkMapObject.isInitialized){
            mapObjectCollection.remove(placemarkMapObject)
        }

        val marker = createBitmapFromVector(R.drawable.ic_location)
        val imageProvider = ImageProvider.fromBitmap(marker)
        mapObjectCollection = mapView.map.mapObjects
        placemarkMapObject = mapObjectCollection.addPlacemark(point, imageProvider)
    }
}