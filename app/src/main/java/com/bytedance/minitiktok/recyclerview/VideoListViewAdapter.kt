package com.bytedance.minitiktok.recyclerview

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.VideoActivity
import com.bytedance.minitiktok.model.Video

class VideoListViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private var items: List<Video> = ArrayList()

    private var mActivity: Activity?

    constructor(activity: Activity?):super()
    {
        mActivity = activity
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder.create(parent.context, parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoListViewHolder).bind(items[position], mActivity, position)
    }

    fun setItems(value: List<Video>)
    {
        items = value
    }

}