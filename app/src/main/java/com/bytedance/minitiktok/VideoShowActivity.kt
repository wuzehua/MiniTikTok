package com.bytedance.minitiktok

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.minitiktok.player.VideoPlayerIJK
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.player.VideoPlayerListener
import com.bytedance.minitiktok.recyclerview.VideoViewAdapter
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager
import kotlinx.android.synthetic.main.activity_ijk_video.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class VideoShowActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: VideoViewAdapter
    private lateinit var mLayoutManager: ViewPagerLayoutManager
    private var mVideosDB: List<Video>? = null
    private lateinit var mPlayer: VideoPlayerIJK
    private var mVideoWidth = 0
    private var mVideoHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ijk_video)
        mPlayer = VideoPlayerIJK(this)
        mRecyclerView = rv_videoRecycler
        mAdapter = VideoViewAdapter()
        mLayoutManager = ViewPagerLayoutManager(this, OrientationHelper.VERTICAL)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter
        val position = intent.getIntExtra("position", 0)

        mLayoutManager.setOnViewPagerListener(object : OnViewPagerListener {
            override fun onInitComplete() {
                playVideo(0)
            }

            override fun onPageRelease(isNext: Boolean, position: Int) {
                var index = 0
                if (isNext) {
                    index = 0
                } else {
                    index = 1
                }
                releaseVideo(index)
            }

            override fun onPageSelected(postion: Int, isBottom: Boolean) {
                playVideo(0)
            }

        })

        mPlayer.setListener(object : VideoPlayerListener() {
            override fun onVideoSizeChanged(iMediaPlayer: IMediaPlayer?, i: Int, i1: Int, i2: Int, i3: Int) {
                //super.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3)
                if (iMediaPlayer != null) {
                    mVideoWidth = iMediaPlayer.videoWidth
                    mVideoHeight = iMediaPlayer.videoHeight
                }
            }

            override fun onPrepared(iMediaPlayer: IMediaPlayer?) {
                if (iMediaPlayer != null) {
                    mVideoWidth = iMediaPlayer.videoWidth
                    mVideoHeight = iMediaPlayer.videoHeight
                }
                initPlayer()
                super.onPrepared(iMediaPlayer)
            }
        })

        initFromDB(position)
    }

    private fun playVideo(position: Int) {
        val itemView = mRecyclerView.getChildAt(0)
        //val player:VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
        //player.start()
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.ry_relative)
        mPlayer.setVideoPath(relativeLayout.tag as String)
        relativeLayout.addView(mPlayer)

    }

    private fun releaseVideo(position: Int) {
        val itemView = mRecyclerView.getChildAt(position)
//        val player: VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
//        player.release()
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.ry_relative)
        relativeLayout.removeView(mPlayer)
        mPlayer.stop()
    }

    private fun initFromDB(position: Int) {
        class LoadDBAsyncTask() : AsyncTask<Objects, Objects, List<Video>>() {
            override fun doInBackground(vararg p0: Objects?): List<Video> {
                mVideosDB = DataBase.getInstance(this@VideoShowActivity).getVideo()
                return mVideosDB!!
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                if (result != null) {
                    mAdapter.setItems(mVideosDB!!)
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.scrollToPosition(position)
                }
            }
        }

        var getVideosAsyncTask = LoadDBAsyncTask()
        getVideosAsyncTask.execute()
    }


    private fun initPlayer() {
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val height = windowManager.defaultDisplay.height
        val width = windowManager.defaultDisplay.width

        var ratio = width.toFloat() / height.toFloat()

        if (width < height) {
            ratio = height.toFloat() / width.toFloat()
        }


        val param = mPlayer.layoutParams as RelativeLayout.LayoutParams
        param.width = width
        param.height = (ratio * mVideoHeight.toFloat()).toInt()
        param.addRule(RelativeLayout.CENTER_IN_PARENT)
        mPlayer.layoutParams = param
        mPlayer.start()

    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()

    }
}