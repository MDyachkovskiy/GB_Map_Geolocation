package gb.com.map_geolocation.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import gb.com.map_geolocation.databinding.DialogEditPlacemarkBinding
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
                showDialogToEnterName { name, annotation ->
                    model.savePlacemark(it, name, annotation)
                }
            } ?: run {
                showAlertDialog()
            }
        }

        with(binding) {
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerOpened(drawerView: View) {
                    blockingView.visibility = View.VISIBLE
                }

                override fun onDrawerClosed(drawerView: View) {
                    blockingView.visibility = View.GONE
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            })

            fabOpenList.setOnClickListener {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
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

    private fun showDialogToEnterName(onNameEntered: (String, String) -> Unit) {
        val binding = DialogEditPlacemarkBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setTitle("Введите название маркера и комментарий")
            .setView(binding.root)
            .setPositiveButton("Сохранить") {_,_ ->
                val name = binding.editName.text.toString()
                val annotation = binding.editAnnotation.text.toString()
                if(name.isNotBlank()) {
                    onNameEntered(name, annotation)
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