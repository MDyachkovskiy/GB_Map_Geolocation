package gb.com.map_geolocation.model.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlacemarkEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placemarkDao(): PlacemarkDao
}