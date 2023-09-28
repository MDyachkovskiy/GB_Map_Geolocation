package gb.com.map_geolocation.model.repository

import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity

interface PlacemarkRepository {
    suspend fun savePlacemark(placemark: PlacemarkEntity)
    suspend fun getPlacemerks(): List<PlacemarkEntity>
}