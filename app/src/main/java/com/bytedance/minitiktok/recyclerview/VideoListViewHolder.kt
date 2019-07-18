package com.bytedance.minitiktok.recyclerview

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bytedance.minitiktok.R
import android.util.Pair
import com.bytedance.minitiktok.VideoActivity
import com.bytedance.minitiktok.VideoShowActivity
import com.bytedance.minitiktok.model.Video
import java.text.FieldPosition
import androidx.core.content.ContextCompat.startActivity




class VideoListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var mView: View = view
    private var mCard: CardView = view.findViewById(R.id.card)
    private var mNameText: TextView = view.findViewById(R.id.nameText)
    private var mDateText: TextView = view.findViewById(R.id.updateDateText)
    private var mImage: ImageView = view.findViewById(R.id.img)
    var mLikedButton: LottieAnimationView = view.findViewById(R.id.like)

    companion object {
        fun create(context: Context, root: ViewGroup): VideoListViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.video_item_view, root, false)
            return VideoListViewHolder(v)
        }
    }

    fun bind(data: Video?, activity: Activity?, position: Int, liked: Boolean) {
        if (data == null || activity == null) return

        Glide.with(activity).load(data.imageUrl).into(mImage)
        mNameText.text = data.userName
        mDateText.text = data.updateDate
        mCard.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(activity,VideoShowActivity::class.java)
                intent.putExtra("position",position)
//                val namePair = Pair.create<View?,String?>(mNameText,"userName")
//                val datePair = Pair.create<View?, String?>(mDateText,"updateDate")
//                val optionsCompat = ActivityOptions.makeSceneTransitionAnimation(activity,namePair,datePair)
//
//                activity.startActivity(intent,optionsCompat.toBundle())
                activity.startActivity(intent)

            }

        })

        if(liked)
        {
            mLikedButton.progress = 1f
        }else
        {
            mLikedButton.progress = 0f
        }

    }
}