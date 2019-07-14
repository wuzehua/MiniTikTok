package com.bytedance.minitiktok.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.model.Video
import com.bytedance.minitiktok.response.GetResponse
import retrofit2.Retrofit
import kotlinx.android.synthetic.main.video_list_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoListFragment : Fragment
{
    private var mService: IMiniDouyinService?
    private var mVideos: List<Video>

    constructor(service: IMiniDouyinService?): super()
    {
        mService = service
        mVideos = ArrayList<Video>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_list_fragment,container,false)
        val recyclerView = view.mVideoListRV



        return view
    }


    private fun fetchFeed()
    {
        val call = mService!!.videos
        call.enqueue(object : Callback<GetResponse> {
            override fun onResponse(call: Call<GetResponse>, response: Response<GetResponse>) {
                if (response.body() != null && response.body()!!.success) {
                    mVideos = response.body()!!.videos
                    //initRecyclerView();
                    TODO("Update Adapter")
                }
            }

            @SuppressLint("ShowToast")
            override fun onFailure(call: Call<GetResponse>, t: Throwable) {
                Toast.makeText(activity, "Fail", Toast.LENGTH_LONG)
            }
        })
    }



}