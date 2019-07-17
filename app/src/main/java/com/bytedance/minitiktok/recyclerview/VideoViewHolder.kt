package com.bytedance.minitiktok.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.R
import kotlinx.android.synthetic.main.video_view_layout.view.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    private var mVideoPlayer: IjkMediaPlayer? = null
    private var mSurfaceView: SurfaceView
    private var mProgressBar: ProgressBar

    init {
        mSurfaceView = view.findViewById(R.id.sv_surfaceView)
        mProgressBar = view.findViewById(R.id.pb_videoProgress)
    }

    companion object
    {
        fun create(context: Context, root: ViewGroup): VideoViewHolder
        {
            val view = LayoutInflater.from(context).inflate(R.layout.video_view_layout,root,false)
            return VideoViewHolder(view)
        }
    }



}