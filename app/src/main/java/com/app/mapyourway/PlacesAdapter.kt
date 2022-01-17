package com.app.mapyourway

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mapyourway.models.Place

class PlacesAdapter(
    val context: Context,
    val places: List<Place>,
    val onClickListener: PlacesAdapter.OnClickListener
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_marker_place, parent, false)
        return PlacesAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        val textViewTitle = holder.itemView.findViewById<TextView>(R.id.txtPlaceTitle)
        textViewTitle.text = place.title
        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = places.size

}