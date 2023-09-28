package gb.com.map_geolocation.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlacemarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacemark(placemark: PlacemarkEntity)

    @Query("SELECT * FROM placemarks")
    suspend fun getPlacemarks(): List<PlacemarkEntity>
}