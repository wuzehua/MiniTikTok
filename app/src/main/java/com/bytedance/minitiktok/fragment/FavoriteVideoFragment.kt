package com.bytedance.minitiktok.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video

//class FavoriteVideoFragment(context: Context, service: IMiniDouyinService?, favoriteName: String) :
//    VideoListFragment(service) {
//    private val favoriteName = favoriteName
//    override fun getResultFromDB(): List<Video>? {
//        return DataBase.getInstance(activity!!).getFavoriteVideo(
//            context!!.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE).getString(
//                "user_name",
//                context!!.getString(R.string.un_registe_user_name)
//            )!!, favoriteName
//        )
//    }
//}

class FavoriteVideoFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_video,container,false)
    }
}