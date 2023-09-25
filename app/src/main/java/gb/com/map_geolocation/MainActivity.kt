package gb.com.map_geolocation

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("54730c83-7ec0-4ecf-93da-c64ad0b860d6")
        MapKitFactory.initialize(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mapView = binding.mapView

        showLocationPermissionDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ){
                    initializeMap()
                } else {
                    Snackbar.make(binding.root,
                    "Location permission is required for this features",
                    Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(this)
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
        if(ContextCompat.checkSelfPermission(this,permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initializeMap()
        }
    }

    private fun initializeMap() {
        mapView
            .map
            .move(CameraPosition(Point(55.755864, 37.617698),
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
}