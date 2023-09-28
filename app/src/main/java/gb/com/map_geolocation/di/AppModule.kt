package gb.com.map_geolocation.di

import android.content.Context
import androidx.room.Room
import gb.com.map_geolocation.model.datasource.local.AppDatabase
import gb.com.map_geolocation.model.repository.LocationRepository
import gb.com.map_geolocation.view.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        MapViewModel(get())
    }

    single {
        val context: Context = get()
        LocationRepository(context)
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "placemarks_database")
            .build()
    }

    factory { get<AppDatabase>().placemarkDao() }
}