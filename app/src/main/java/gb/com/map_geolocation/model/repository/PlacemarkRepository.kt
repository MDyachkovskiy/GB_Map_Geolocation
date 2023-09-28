package gb.com.map_geolocation.model.repository

import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity
import kotlinx.coroutines.flow.Flow

interface PlacemarkRepository {
    suspend fun savePlacemark(placemark: PlacemarkEntity)
    fun getPlacemarks(): Flow<List<PlacemarkEntity>>
}