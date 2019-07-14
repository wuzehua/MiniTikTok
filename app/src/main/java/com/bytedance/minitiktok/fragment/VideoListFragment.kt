package com.bytedance.minitiktok.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.recyclerview.VideoListViewAdapter
import com.bytedance.minitiktok.response.GetResponse
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.video_list_fragment.*
import retrofit2.Retrofit
import kotlinx.android.synthetic.main.video_list_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class VideoListFragment(service: IMiniDouyinService?) : Fragment() {
    private var mService: IMiniDouyinService? = service
    private var mAdapter: VideoListViewAdapter?
    private var mVideos: List<Video>

    init {
        mVideos = ArrayList()
        mAdapter = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_list_fragment, container, false)

        mRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                fetchFeed()
                mRefreshLayout.isRefreshing = false
            }

        })

        mAdapter = VideoListViewAdapter(activity)
        mVideoListRV.layoutManager = VegaLayoutManager()
        mVideoListRV.adapter = mAdapter
        fetchFeed()

        return view
    }


    private fun fetchFeed() {
        @SuppressLint("StaticFieldLeak")
        class GetVideosAsyncTask() : AsyncTask<Objects, Objects, List<Video>>() {

            override fun doInBackground(vararg p0: Objects?): List<Video> {
                if (mService == null) {
                    Log.println(Log.WARN, "Service", "NULL Service")
                    return emptyList()
                } else {
                    try {
                        val response = mService!!.videos.execute()
                        return if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                            response.body()!!.videos
                        } else {
                            emptyList()
                        }
                    } catch (e: IOException) {
                        Log.e("mService execute", "IOException", e)
                        return emptyList()
                    }
                }
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                if (result != null) {
                    if (result.isEmpty()) {
                        Toast.makeText(null, "Fail", Toast.LENGTH_LONG)
                    } else {
                        mVideos = result
                    }
                }
            }
        }

        var getVideosAsyncTask = GetVideosAsyncTask()
        getVideosAsyncTask.execute()
    }
}