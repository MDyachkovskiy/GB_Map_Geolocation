package gb.com.map_geolocation.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {

    private val _isPermissionGranted = MutableLiveData<Boolean>(false)
    val isPermissionGranted: LiveData<Boolean> get() = _isPermissionGranted

    fun checkPermission(context: Context) {
       _isPermissionGranted.value =
           ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                   PackageManager.PERMISSION_GRANTED ||
                   ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                   PackageManager.PERMISSION_GRANTED
    }
}