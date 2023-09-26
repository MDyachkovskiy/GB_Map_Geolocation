package gb.com.map_geolocation

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import gb.com.map_geolocation.model.LocationState
import gb.com.map_geolocation.utils.MINIMAL_DISTANCE
import gb.com.map_geolocation.utils.REFRESH_PERIOD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationRepository {

    fun requestLocationManager(locationManager: LocationManager): Flow<LocationState> = flow {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
            provider?.let {
                val location = suspendCancellableCoroutine<Location?> { continuation ->
                    val locationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            location.let {
                                continuation.resumeWith(Result.success(it))
                                locationManager.removeUpdates(this)
                            }
                        }
                    }

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        REFRESH_PERIOD,
                        MINIMAL_DISTANCE,
                        locationListener
                    )

                    continuation.invokeOnCancellation {
                        locationManager.removeUpdates(locationListener)
                    }
                }
            } ?: emit(LocationState.Error("No GPS provider available"))
        } else {
            emit(LocationState.Error("GPS provider is disabled"))
        }
    }
}