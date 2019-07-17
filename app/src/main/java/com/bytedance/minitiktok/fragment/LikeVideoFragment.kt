package com.bytedance.minitiktok.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.recyclerview.VideoListViewAdapter
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.video_list_fragment.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LikeVideoFragment(service: IMiniDouyinService?) : Fragment() {
    private var mService: IMiniDouyinService? = service
    private var mAdapter: VideoListViewAdapter?
    private var mVideos: List<Video>
    private var mVideosDB: List<Video> = ArrayList()

    init {
        mVideos = ArrayList()
        mAdapter = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_list_fragment, container, false)
        val recyclerView = view.mVideoListRV
        val refreshLayout = view.mRefreshLayout

        refreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                fetchFeed()
            }

        })

        mAdapter = VideoListViewAdapter(activity)
        recyclerView.layoutManager = VegaLayoutManager()
        recyclerView.adapter = mAdapter
        fetchFeed()

        return view
    }

    private fun fetchFeed() {
        @SuppressLint("StaticFieldLeak")
        class GetVideosAsyncTask() : AsyncTask<Objects, Objects, List<Video>>() {

            override fun doInBackground(vararg p0: Objects?): List<Video> {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val sharedPreferences = context!!.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)
                val lastUpdateTime =
                    simpleDateFormat.parse(
                        sharedPreferences.getString(
                            "lastUpdateTime",
                            "1800-01-01T00:00:00.000UTC"
                        ).replace("Z", "UTC")
                    )
                val dataBase = DataBase.getInstance(activity!!)
                if (mService == null) {
                    Log.println(Log.WARN, "Service", "NULL Service")
                } else {
                    try {
                        val response = mService!!.videos.execute()
                        if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                            var currentUpdateTime = lastUpdateTime
                            val videos = response.body()!!.videos
                            for (video in videos) {
                                val videoUpdateTime = simpleDateFormat.parse(video.updateDate.replace("Z", "UTC"))
                                if (videoUpdateTime > lastUpdateTime) {
                                    if (videoUpdateTime > currentUpdateTime) {
                                        currentUpdateTime = videoUpdateTime
                                    }
                                    dataBase.insertVideo(video)
                                }
                            }
                            sharedPreferences.edit()
                                .putString("lastUpdateTime", simpleDateFormat.format(currentUpdateTime)).apply()
                        }
                    } catch (e: IOException) {
                        Log.e("mService execute", "IOException", e)
                    }
                }
                val usr_name = sharedPreferences.getString("usr_name", "unResisted")
                val videos = dataBase.getAllVideos()
                val res: MutableList<Video> = emptySequence<Video>().toMutableList()
                for (video in videos) {
                    if (dataBase.getisLiked(usr_name, video.videoId)) {
                        res += video
                    }
                }

                return res
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                if (result != null) {
                    mVideos = result
                    mAdapter?.setItems(mVideos)
                    mAdapter?.notifyDataSetChanged()
                    view?.mRefreshLayout?.isRefreshing = false
                    Toast.makeText(activity, "列表已更新", Toast.LENGTH_SHORT).show()
                }
            }
        }

        var getVideosAsyncTask = GetVideosAsyncTask()
        getVideosAsyncTask.execute()
    }
}