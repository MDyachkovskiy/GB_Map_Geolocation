package gb.com.map_geolocation.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gb.com.map_geolocation.model.repository.LocationRepository
import gb.com.map_geolocation.model.LocationState
import kotlinx.coroutines.launch


class MapViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    private val _locationState = MutableLiveData<LocationState>()
    val locationState: LiveData<LocationState> = _locationState

    private val _isPermissionGranted = MutableLiveData(false)
    val isPermissionGranted: LiveData<Boolean> get() = _isPermissionGranted

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
}