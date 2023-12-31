package gb.com.map_geolocation.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import gb.com.map_geolocation.model.repository.LocationRepository
import gb.com.map_geolocation.model.LocationState
import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity
import gb.com.map_geolocation.model.repository.PlacemarkRepository
import kotlinx.coroutines.launch


class MapViewModel(
    private val repository: LocationRepository,
    private val placemarkRepository: PlacemarkRepository
) : ViewModel() {

    private val _locationState = MutableLiveData<LocationState>()
    val locationState: LiveData<LocationState> = _locationState

    private val _isPermissionGranted = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> get() = _isPermissionGranted

    private val _currentPoint = MutableLiveData<Point>()
    val currentPoint: LiveData<Point> get() = _currentPoint

    val placemarks: LiveData<List<PlacemarkEntity>> = placemarkRepository.getPlacemarks().asLiveData()

    fun checkPermission(context: Context) {
       _isPermissionGranted.value =
           ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                   PackageManager.PERMISSION_GRANTED ||
                   ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                   PackageManager.PERMISSION_GRANTED
    }

    fun getLocation() {
        viewModelScope.launch{
            repository.requestLocation().collect {state ->
                _locationState.value = state
            }
        }
    }

    fun savePlacemark(point: Point, name: String? = null, annotation: String? = null) {
        viewModelScope.launch {
            placemarkRepository.savePlacemark(PlacemarkEntity(
                latitude = point.latitude,
                longitude = point.longitude,
                name = name,
                annotation = annotation
            ))
        }
    }

    fun deletePlacemark(placemark: PlacemarkEntity) {
        viewModelScope.launch {
            placemarkRepository.deletePlacemark(placemark)
        }
    }

    fun updatePlacemark(placemark: PlacemarkEntity) {
        viewModelScope.launch {
            placemarkRepository.updatePlacemark(placemark)
        }
    }

    fun setCurrentPoint(point: Point) {
        _currentPoint.value = point
    }
}