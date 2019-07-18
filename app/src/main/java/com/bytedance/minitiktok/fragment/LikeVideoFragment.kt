package com.bytedance.minitiktok.fragment

import android.content.Context
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Video

class LikeVideoFragment(service: IMiniDouyinService?) : VideoListFragment(service) {
    override fun getResultFromDB(): List<Video>? {
        return DataBase.getInstance(activity!!).getUserLikeVideo(
            context!!.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE).getString(
                "usr_name",
                getString(R.string.un_registe_user_name)
            )!!
        )
    }
}