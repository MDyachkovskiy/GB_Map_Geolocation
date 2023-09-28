package gb.com.map_geolocation.model.repository

import gb.com.map_geolocation.model.datasource.local.PlacemarkDao
import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity

class PlacemarkRepositoryImpl(
    private val placemarkDao: PlacemarkDao
): PlacemarkRepository {
    override suspend fun savePlacemark(placemark: PlacemarkEntity) {
        placemarkDao.insertPlacemark(placemark)
    }

    override suspend fun getPlacemerks(): List<PlacemarkEntity> {
        return placemarkDao.getPlacemarks()
    }
}