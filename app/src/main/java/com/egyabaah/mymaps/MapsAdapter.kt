package com.egyabaah.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egyabaah.mymaps.models.UserMap

private const val TAG = "MapsAdapter"
class MapsAdapter(
    private val context: Context,
    initialUserMaps: List<UserMap>,
    private val onClickListener: OnClickListener,
) : ListAdapter<UserMap, MapsAdapter.ViewHolder>(UserMapDiffCallback) {

    object UserMapDiffCallback : DiffUtil.ItemCallback<UserMap>() {
        override fun areItemsTheSame(oldItem: UserMap, newItem: UserMap): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UserMap, newItem: UserMap): Boolean = oldItem == newItem
    }

    var fullUserMapList: MutableList<UserMap> = initialUserMaps.toMutableList()
        set(value) {
            field = value
            onListOrFilterChange()
        }

    var filter: CharSequence = ""
        set(value) {
            field = value
            onListOrFilterChange()
        }

    init {
        onListOrFilterChange()
    }


    fun updateData(newUserMaps: List<UserMap>) {
        fullUserMapList = newUserMaps.toMutableList()
        onListOrFilterChange()
    }

    fun addItem(userMap: UserMap) {
        Log.i(TAG, "Adding Item")
        fullUserMapList.add(userMap)
        onListOrFilterChange()
    }

    fun removeItem(userMap: UserMap) {
        fullUserMapList.remove(userMap)
        onListOrFilterChange()
    }

    fun updateItem(userMap: UserMap) {
        val index = fullUserMapList.indexOfFirst { it.id == userMap.id }
        if (index != -1) {
            fullUserMapList[index] = userMap
            onListOrFilterChange()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = getItem(position)
        holder.itemView.setOnClickListener {
            Log.i(TAG, "Tapped on position $position")
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<TextView>(R.id.tvMapTitle)
        val textViewPlacesCount = holder.itemView.findViewById<TextView>(R.id.tvPlacesCount)
        textViewTitle.text = userMap.title
        textViewPlacesCount.text = userMap.places.size.toString()
    }

    private fun onListOrFilterChange() {
        if (filter.isEmpty()) {
            submitList(fullUserMapList.toList())
            Log.i(TAG, "Adding full list filter is Empty")
            Log.i(TAG, "Applied Filter to List ${fullUserMapList.map { it -> it.id }}")


            return
        }
        val pattern = filter.toString().lowercase().trim()
        val filteredList = fullUserMapList.filter { pattern in it.title.lowercase() }
        Log.i(TAG, "Applied Filter to List ${fullUserMapList.map { it -> it.id }}")

        submitList(filteredList)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}