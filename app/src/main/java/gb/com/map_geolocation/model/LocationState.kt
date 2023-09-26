package gb.com.map_geolocation.model

import android.location.Location

sealed class LocationState {
    data class Success(val location: Location) : LocationState()
    data class Error(val message: String) : LocationState()
    object Loading : LocationState()
}