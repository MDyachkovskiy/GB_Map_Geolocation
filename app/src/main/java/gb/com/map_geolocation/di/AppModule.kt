package gb.com.map_geolocation.di

import android.content.Context
import gb.com.map_geolocation.model.LocationRepository
import gb.com.map_geolocation.view.MapViewModel
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