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
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.recyclerview.VideoListViewAdapter
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.video_list_fragment.view.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class VideoListFragment(service: IMiniDouyinService?) : Fragment() {
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
        //fetchFeed()
        initFromDB()

        return view
    }


    private fun initFromDB()
    {
        class LoadDBAsyncTask(): AsyncTask<Objects,Objects,List<Video>>()
        {
            override fun doInBackground(vararg p0: Objects?): List<Video> {
                mVideosDB = DataBase.getInstance(activity!!).getAllVideos()
                if(mVideosDB.isEmpty())
                {
                    if(mService == null)
                    {
                        Log.println(Log.WARN, "Service", "NULL Service")
                        return emptyList()
                    }
                    else
                    {
                        try {
                            val response = mService!!.videos.execute()
                            if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                                DataBase.getInstance(activity!!).insertVideos(response.body()!!.videos)
                                mVideosDB = DataBase.getInstance(activity!!).getAllVideos()
                            } else {
                                return emptyList()
                            }
                        } catch (e: IOException) {
                            Log.e("mService execute", "IOException", e)
                            return emptyList()
                        }
                    }
                }

                return mVideosDB
            }

            override fun onPostExecute(result: List<Video>?) {
                super.onPostExecute(result)
                if(result != null)
                {
                    mAdapter?.setItems(mVideosDB)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        }

        var getVideosAsyncTask = LoadDBAsyncTask()
        getVideosAsyncTask.execute()
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
                        Toast.makeText(null, "Fail", Toast.LENGTH_LONG).show()
                        view?.mRefreshLayout?.isRefreshing = false
                    } else {
                        mVideos = result
                        mAdapter?.setItems(mVideos)
                        mAdapter?.notifyDataSetChanged()
                        view?.mRefreshLayout?.isRefreshing = false
                        Toast.makeText(activity,"列表已更新",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        var getVideosAsyncTask = GetVideosAsyncTask()
        getVideosAsyncTask.execute()
    }
}