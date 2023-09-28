package gb.com.map_geolocation.model.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placemarks")
data class PlacemarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val annotation: String? = null
)