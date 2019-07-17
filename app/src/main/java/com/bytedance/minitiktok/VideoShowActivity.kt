package com.bytedance.minitiktok

import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.minitiktok.player.VideoPlayerIJK
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.recyclerview.VideoViewAdapter
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager
import kotlinx.android.synthetic.main.activity_ijk_video.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class VideoShowActivity : AppCompatActivity()
{

    private lateinit var mRecyclerView:RecyclerView
    private lateinit var mAdapter: VideoViewAdapter
    private lateinit var mLayoutManager: ViewPagerLayoutManager
    private var mVideosDB:List<Video>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ijk_video)

        mRecyclerView = rv_videoRecycler
        mAdapter = VideoViewAdapter()
        mLayoutManager = ViewPagerLayoutManager(this,OrientationHelper.VERTICAL)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        mLayoutManager.setOnViewPagerListener(object: OnViewPagerListener{
            override fun onInitComplete() {
                playVideo(0)
            }

            override fun onPageRelease(isNext:Boolean, position: Int) {
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

        val position = intent.getIntExtra("position",0)
        initFromDB(position)
    }

    private fun playVideo(position: Int)
    {
        val itemView = mRecyclerView.getChildAt(0)
        val player:VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
        player.start()
    }

    private fun releaseVideo(position: Int)
    {
        val itemView = mRecyclerView.getChildAt(position)
        val player: VideoPlayerIJK = itemView.findViewById(R.id.ijkPlayer)
        player.release()
    }

    private fun initFromDB(position: Int)
    {
        class LoadDBAsyncTask(): AsyncTask<Objects, Objects, List<Video>>()
        {
            override fun doInBackground(vararg p0: Objects?): List<Video> {
                mVideosDB = DataBase.getInstance(this@VideoShowActivity).getAllVideos()
                return mVideosDB!!
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                if(result != null)
                {
                    mAdapter.setItems(mVideosDB!!)
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.scrollToPosition(position)
                }
            }
        }

        var getVideosAsyncTask = LoadDBAsyncTask()
        getVideosAsyncTask.execute()
    }
}