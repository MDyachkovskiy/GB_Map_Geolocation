package gb.com.map_geolocation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gb.com.map_geolocation.databinding.ItemPlacemarkBinding
import gb.com.map_geolocation.model.datasource.local.PlacemarkEntity

class PlacemarkAdapter(
    private var placemarks: List<PlacemarkEntity>
) : RecyclerView.Adapter<PlacemarkAdapter.PlacemarkViewHolder>() {

    inner class PlacemarkViewHolder(
        val binding: ItemPlacemarkBinding
        ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacemarkViewHolder {
        val binding = ItemPlacemarkBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacemarkViewHolder(binding)
    }

    override fun getItemCount() = placemarks.size

    override fun onBindViewHolder(holder: PlacemarkViewHolder, position: Int) {
        val placemark = placemarks[position]
        holder.binding.placemarkName.text = placemark.name
        holder.binding.placemarkAnnotation.text = placemark.annotation
    }

    fun updatePlacemarks(newPlacemarks: List<PlacemarkEntity>) {
        placemarks = newPlacemarks
        notifyDataSetChanged()
    }

}