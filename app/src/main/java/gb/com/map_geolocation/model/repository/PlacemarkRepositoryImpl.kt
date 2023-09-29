package gb.com.map_geolocation.model.repository

import gb.com.map_geolocation.model.datasource.local.PlacemarkDao
import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity
import kotlinx.coroutines.flow.Flow

class PlacemarkRepositoryImpl(
    private val placemarkDao: PlacemarkDao
): PlacemarkRepository {
    override suspend fun savePlacemark(placemark: PlacemarkEntity) {
        placemarkDao.insertPlacemark(placemark)
    }

    override fun getPlacemarks(): Flow<List<PlacemarkEntity>> = placemarkDao.getPlacemarks()
    override suspend fun deletePlacemark(placemark: PlacemarkEntity) {
        placemarkDao.deletePlacemark(placemark)
    }

}