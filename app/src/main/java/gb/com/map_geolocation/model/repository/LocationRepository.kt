package gb.com.map_geolocation.model.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import gb.com.map_geolocation.model.LocationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class LocationRepository(
    private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun requestLocation() = callbackFlow {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            val task = fusedLocationClient.lastLocation
            task.addOnSuccessListener {location: Location? ->
                location?.let {
                    trySend(LocationState.Success(it)).isSuccess
                } ?: trySend(LocationState.Error("Location is null")).isSuccess
            }.addOnFailureListener { error ->
                trySend(LocationState.Error(error.message ?: "Unknown error")).isSuccess
            }
        } catch (e: Exception){
            trySend(LocationState.Error(e.message ?: "Unknown error"))
        }

        awaitClose {}

    }.flowOn(Dispatchers.IO)
}
