package com.egyabaah.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.egyabaah.mymaps.models.UserMap

private const val TAG = "MapsAdapter"
class MapsAdapter (val context: Context, val userMaps: List<UserMap>, val onClickListener : OnClickListener) : RecyclerView.Adapter<MapsAdapter.ViewHolder>(), Filterable {
    private val filteredUserMaps : MutableList<UserMap> = userMaps.toMutableList()
//    private val filteredUserMaps : List<UserMap> = userMaps.toMutableList()

    interface OnClickListener {
        fun onItemClick (position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = filteredUserMaps[position]
        holder.itemView.setOnClickListener{
            Log.i(TAG, "Tapped on position $position")
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<TextView>(R.id.tvMapTitle)
        val textViewPlacesCount = holder.itemView.findViewById<TextView>(R.id.tvPlacesCount)
        textViewTitle.text = userMap.title
        textViewPlacesCount.text = userMap.places.size.toString()
    }

    override fun getItemCount() = filteredUserMaps.size



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun getFilter(): Filter {
        return userMapFilter
    }

    private val userMapFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList : MutableList<UserMap> = mutableListOf()
            if (constraint.isNullOrEmpty()){
                filteredList.addAll(userMaps)
            }
            else{
                val filterPattern = constraint.toString().trim().lowercase()
                for (userMap in userMaps){
                    if (userMap.title.lowercase().contains(filterPattern)){
                        filteredList.add(userMap)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null) {
                with (filteredUserMaps) {
                    clear()
                    addAll(results.values as List<UserMap>)
                }
                notifyDataSetChanged()
            }

        }

    }

}
