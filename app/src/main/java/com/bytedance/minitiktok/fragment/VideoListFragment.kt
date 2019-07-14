package com.bytedance.minitiktok.fragment

import android.annotation.SuppressLint
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
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.recyclerview.VideoListViewAdapter
import com.bytedance.minitiktok.response.GetResponse
import com.stone.vega.library.VegaLayoutManager
import retrofit2.Retrofit
import kotlinx.android.synthetic.main.video_list_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoListFragment(service: IMiniDouyinService?) : Fragment()
{
    private var mService: IMiniDouyinService? = service
    private var mAdapter: VideoListViewAdapter?
    private var mVideos: List<Video>

    init {
        mVideos = ArrayList()
        mAdapter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_list_fragment,container,false)
        val recyclerView = view.mVideoListRV
        val refreshLayout = view.mRefreshLayout

        refreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener
        {
            override fun onRefresh() {
                fetchFeed()
                refreshLayout.isRefreshing = false
            }

        })

        mAdapter = VideoListViewAdapter(activity)
        recyclerView.layoutManager = VegaLayoutManager()
        recyclerView.adapter = mAdapter
        fetchFeed()

        return view
    }


    private fun fetchFeed()
    {
        if(mService == null)
        {
            Log.println(Log.WARN,"Service","NULL Service")
        }
        val call = mService!!.videos
        call.enqueue(object : Callback<GetResponse> {
            override fun onResponse(call: Call<GetResponse>, response: Response<GetResponse>) {
                if (response.body() != null && response.body()!!.success) {
                    mVideos = response.body()!!.videos
                    Log.println(Log.INFO,"Fetch","In fetch feed")
                    mAdapter?.setItems(mVideos)
                    mAdapter?.notifyDataSetChanged()
                    println(mVideos.size)
                }
            }

            @SuppressLint("ShowToast")
            override fun onFailure(call: Call<GetResponse>, t: Throwable) {
                Toast.makeText(activity, "Fail", Toast.LENGTH_LONG)
                println("Fail")
                println(mVideos.size)
            }
        })
    }



}