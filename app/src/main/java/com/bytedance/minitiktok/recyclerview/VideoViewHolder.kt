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
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.player.VideoPlayerIJK
import kotlinx.android.synthetic.main.video_view_layout.view.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    private var mVideoPlayer: IjkMediaPlayer? = null
    private var mPlayer: VideoPlayerIJK
    private var mProgressBar: ProgressBar

    init {
        mPlayer = view.findViewById(R.id.ijkPlayer)
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

    fun bind(data: Video?, position: Int)
    {
        if(data == null) return

        mPlayer.setVideoPath(data.videoUrl)
        mProgressBar.progress = 0
    }

}