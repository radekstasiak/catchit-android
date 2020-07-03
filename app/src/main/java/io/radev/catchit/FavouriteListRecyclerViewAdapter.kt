package io.radev.catchit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.radev.catchit.db.FavouriteStop
import io.radev.catchit.dummy.DummyContent.DummyItem


class FavouriteListRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = arrayListOf<FavouriteStop>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FavouriteItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favourite_item,
                parent,
                false
            )
        )

    fun setData(list: List<FavouriteStop>) {
        data = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as FavouriteItemViewHolder
        val item = data[position]
        vh.atcocode.text = item.atcocode
        vh.timestamp.text = item.createdAt.toString()
    }
}

class FavouriteItemViewHolder : RecyclerView.ViewHolder {

    val atcocode: TextView
    val timestamp: TextView

    constructor(view: View) : super(view) {

        atcocode = view.findViewById(R.id.tv_atcocode)
        timestamp = view.findViewById(R.id.tv_timestamp)
    }
}
