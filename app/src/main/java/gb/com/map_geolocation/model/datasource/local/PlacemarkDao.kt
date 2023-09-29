package gb.com.map_geolocation.model.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacemarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacemark(placemark: PlacemarkEntity)

    @Query("SELECT * FROM placemarks")
    fun getPlacemarks(): Flow<List<PlacemarkEntity>>

    @Delete
    suspend fun deletePlacemark(placemark: PlacemarkEntity): Int
}