package gb.com.map_geolocation.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.FragmentMapBinding
import gb.com.map_geolocation.view.MapViewModel

class FabHandler(
    private val binding: FragmentMapBinding,
    private val mapView: MapView,
    private val model: MapViewModel,
    lifecycleOwner: LifecycleOwner,
    private val context: Context
) {

    private var currentPoint: Point? = null
    init{
        initializeFabs()
        observePointChanges(lifecycleOwner)
    }

    private fun observePointChanges(lifecycleOwner: LifecycleOwner) {
        model.currentPoint.observe(lifecycleOwner) {point ->
            currentPoint = point
        }
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

        binding.fabAddLocation.setOnClickListener {
            currentPoint?.let {
                showDialogToEnterName { name ->
                    model.savePlacemark(it, name)
                }
            } ?: run {
                showAlertDialog()
            }
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

    private fun showDialogToEnterName(onNameEntered: (String) -> Unit) {
        val editText = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Введите название маркера на карте")
            .setView(editText)
            .setPositiveButton("Сохранить") {_,_ ->
                val name = editText.text.toString()
                if(name.isNotBlank()) {
                    onNameEntered(name)
                } else {
                    Toast.makeText(context, "Название не может быть пустым",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    private fun showAlertDialog(){
        AlertDialog.Builder(context)
            .setTitle("Ошибка")
            .setMessage("Пожалуйста, сначала отметьте место на карте")
            .setPositiveButton("Ok", null)
            .show()
    }

}