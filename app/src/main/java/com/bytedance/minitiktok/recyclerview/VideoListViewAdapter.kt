package com.bytedance.minitiktok.recyclerview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.minitiktok.VideoActivity
import com.bytedance.minitiktok.model.Video
import android.os.AsyncTask
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.Like


class VideoListViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private var items: List<Video> = ArrayList()
    private var likeItems: List<String> = ArrayList()

    private var mActivity: Activity?

    constructor(activity: Activity?):super()
    {
        mActivity = activity
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoListViewHolder.create(parent.context, parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var liked = false
        if(likeItems.contains(items[position].videoId))
        {
            liked = true
        }
        (holder as VideoListViewHolder).bind(items[position], mActivity, position,liked)

        holder.mLikedButton.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(view: View?) {

                object : AsyncTask<String, Int, String>() {
                    override fun doInBackground(vararg strings: String): String {
                        val sharedPreference = mActivity?.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)!!
                        val userName = sharedPreference.getString("user_name",getString(R.string.un_registe_user_name))
                        if(liked)
                        {
                            DataBase.getInstance(mActivity!!).deleteLike(userName, items[position].videoId)
                        }
                        else
                        {
                            val temp = Like()
                            temp.user_name = userName
                            temp.video_id = items[position].videoId
                            DataBase.getInstance(mActivity!!).insertLike(temp)
                        }
                        likeItems = DataBase.getInstance(mActivity!!).getLike(userName)
                        return "Done"
                    }

                    override fun onPostExecute(s: String) {
                        super.onPostExecute(s)
                        if (liked)
                        {
                            holder.mLikedButton.progress = 0f
                        }else
                        {
                            holder.mLikedButton.speed = 2f
                            holder.mLikedButton.playAnimation()
                        }
                    }
                }.execute()
            }

        })

    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as VideoListViewHolder).mLikedButton.cancelAnimation()
    }

    fun setItems(value: List<Video>)
    {
        items = value
    }

    fun setLikeItems(value: List<String>)
    {
        likeItems = value
    }
}