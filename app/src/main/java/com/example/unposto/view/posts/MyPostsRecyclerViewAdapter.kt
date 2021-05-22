package com.example.unposto.view.posts

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.unposto.R
import com.example.unposto.data.firebase.domain.FirebasePost
import com.squareup.picasso.Picasso

class MyPostsRecyclerViewAdapter(

) : RecyclerView.Adapter<MyPostsRecyclerViewAdapter.ViewHolder>() {
    private var values: List<FirebasePost> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.description.text = item.description
        holder.geolocation.text = "${item.geolocation.latitude} ${item.geolocation.longitude}"
        val uri =  Uri.parse(item.imageUrls.first());
        Picasso.get().load(uri).into(holder.image)
    }

    override fun getItemCount(): Int = values.size

    fun setData(data: List<FirebasePost>) {
        values = data
        notifyDataSetChanged();
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val geolocation: TextView = view.findViewById(R.id.geolocation)
        val description: TextView = view.findViewById(R.id.description)

    }
}