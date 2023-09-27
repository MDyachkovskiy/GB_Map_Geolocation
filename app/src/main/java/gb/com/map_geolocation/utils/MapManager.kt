package gb.com.map_geolocation.utils

import android.content.Context
import com.yandex.mapkit.MapKitFactory

class MapManager(private val context: Context) {
    fun initializeMapKit() {
        MapKitFactory.setApiKey("54730c83-7ec0-4ecf-93da-c64ad0b860d6")
        MapKitFactory.initialize(context)
    }
}