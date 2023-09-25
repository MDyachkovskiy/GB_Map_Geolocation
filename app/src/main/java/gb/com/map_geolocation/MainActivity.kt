package gb.com.map_geolocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("54730c83-7ec0-4ecf-93da-c64ad0b860d6")
        MapKitFactory.initialize(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeMap()
    }

    private fun initializeMap() {
        mapView = binding.mapView

        mapView
            .map
            .move(CameraPosition(Point(55.755864, 37.617698),
                11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 300f), null)
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
}