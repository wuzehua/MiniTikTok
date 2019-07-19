package com.bytedance.minitiktok

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.minitiktok.player.VideoPlayerIJK
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
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
    private var mDBType = 0
    private var mUsrName: String? = null
    private val MSG_REFRESH = 1001
    private val MSG_SET_ZERO = 1002
    private var mLoading: LottieAnimationView? = null


    private lateinit var handler: Handler


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
        mDBType = intent.getIntExtra("DB", 0)

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_REFRESH -> if (mPlayer.isPlaying()) {
                        refresh()
                        handler.sendEmptyMessageDelayed(MSG_REFRESH, 50)
                    }
                    MSG_SET_ZERO -> {
                        progressBar.progress = 0
                    }
                }

            }
        }

        val sharedPreference = getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)
        mUsrName = sharedPreference.getString("user_name", getString(R.string.un_registe_user_name))

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

            override fun onCompletion(iMediaPlayer: IMediaPlayer?) {
                progressBar.progress = 0
                //mPlayer.seekTo(0)
                mPlayer.start()
                refresh()
            }

            override fun onPrepared(iMediaPlayer: IMediaPlayer?) {
                if (iMediaPlayer != null) {
                    mVideoWidth = iMediaPlayer.videoWidth
                    mVideoHeight = iMediaPlayer.videoHeight
                }
                initPlayer()
                mLoading?.cancelAnimation()
                mLoading?.visibility = View.GONE
                super.onPrepared(iMediaPlayer)
            }
        })

        initFromDB(position)


    }

    override fun onResume() {
        super.onResume()
        handler.sendEmptyMessageDelayed(MSG_REFRESH, 300)
    }

    private fun refresh() {
        val current = mPlayer.getCurrentPosition()
        val duration = mPlayer.getDuration()
        if (duration > 0) {
            progressBar.progress = (current * 100 / duration).toInt()
        }

    }

    private fun playVideo(position: Int) {
        handler.sendEmptyMessage(MSG_SET_ZERO)
        val itemView = mRecyclerView.getChildAt(0)
        //val player:VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
        //player.start()
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.ry_relative)
        mLoading = itemView.findViewById(R.id.loading)
        mPlayer.setVideoPath(relativeLayout.tag as String)
        val parent = mPlayer.parent as ViewGroup?
        if (parent != null) {
            parent.removeView(mPlayer)
        }
        relativeLayout.addView(mPlayer, 0)
        mLoading?.visibility = View.VISIBLE
        mLoading?.playAnimation()
    }

    private fun releaseVideo(position: Int) {
        val itemView = mRecyclerView.getChildAt(position)
//        val player: VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
//        player.release()
        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.ry_relative)

        relativeLayout.removeView(mPlayer)
        try {
            mPlayer.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initFromDB(position: Int) {
        class LoadDBAsyncTask() : AsyncTask<Objects, Objects, List<Video>>() {
            override fun doInBackground(vararg p0: Objects?): List<Video> {
                when (mDBType) {
                    1 -> {
                        //mVideosDB = DataBase.getInstance(this@VideoShowActivity).getAllLikes(mUsrName)
                        val sharedPreferences = getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)
                        val userName =
                            sharedPreferences.getString("user_name", getString(R.string.un_registe_user_name))
                        mVideosDB = DataBase.getInstance(this@VideoShowActivity).getUserLikeVideo(userName!!)
                    }
                    else -> {
                        mVideosDB = DataBase.getInstance(this@VideoShowActivity).getVideo()
                    }
                }

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

        val factor = Math.min(height.toDouble() / mVideoHeight.toDouble(), width.toDouble() / mVideoWidth.toDouble())

        val param = mPlayer.layoutParams as RelativeLayout.LayoutParams
        param.width = (mVideoWidth * factor).toInt()
        param.height = (factor * mVideoHeight).toInt()
        param.addRule(RelativeLayout.CENTER_IN_PARENT)
        mPlayer.layoutParams = param
        try {
            mPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        handler.sendEmptyMessageDelayed(MSG_REFRESH, 50)

    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }
}