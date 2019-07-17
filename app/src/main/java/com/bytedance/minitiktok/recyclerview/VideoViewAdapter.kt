package com.bytedance.minitiktok.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.model.Video

class VideoViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var items: ArrayList<Video> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoViewHolder.create(parent.context,parent)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoViewHolder).bind(items[position],position)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setItems(data: List<Video>)
    {
        items = data as ArrayList<Video>
    }


}