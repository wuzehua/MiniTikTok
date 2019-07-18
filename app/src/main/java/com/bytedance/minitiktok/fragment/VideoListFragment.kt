package com.bytedance.minitiktok.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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

open class VideoListFragment(service: IMiniDouyinService?) : Fragment() {
    private var mService: IMiniDouyinService? = service
    private var mAdapter: VideoListViewAdapter?
    private var mVideos: List<Video>

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
                        )!!.replace("Z", "UTC")
                    )
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
                                    DataBase.getInstance(activity!!).insertVideo(video)
                                }
                            }
                            sharedPreferences.edit()
                                .putString("lastUpdateTime", simpleDateFormat.format(currentUpdateTime)).apply()
                        }
                    } catch (e: IOException) {
                        Log.e("mService execute", "IOException", e)
                    }
                }
                mAdapter?.setLikeItems(
                    DataBase.getInstance(activity!!).getLike(
                        sharedPreferences.getString(
                            "user_name",
                            getString(R.string.un_registe_user_name)
                        )!!
                    )
                )
                return getResultFromDB()!!
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                mVideos = result ?: emptyList()
                mAdapter?.setItems(mVideos)
                mAdapter?.notifyDataSetChanged()
                view?.mRefreshLayout?.isRefreshing = false
                Toast.makeText(activity, "列表已更新", Toast.LENGTH_SHORT).show()

            }
        }

        var getVideosAsyncTask = GetVideosAsyncTask()
        getVideosAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
    }

    protected open fun getResultFromDB(): List<Video>? {
        return DataBase.getInstance(activity!!).getVideo()
    }
}