package com.bytedance.minitiktok

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.Window
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bytedance.minitiktok.api.IMiniDouyinService
import com.bytedance.minitiktok.fragment.LikeVideoFragment
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.fragment.VideoListFragment
import com.bytedance.minitiktok.viewpager.FragmentViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var miniDouyinService: IMiniDouyinService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val videoListFragment = VideoListFragment(getService())
        val likeVideoFragment = LikeVideoFragment(getService())
        //val pagerAdapter = FragmentViewPagerAdapter(supportFragmentManager)
        //pagerAdapter.addFragment(videoListFragment)
        //vp_viewPager.adapter = pagerAdapter

        radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: RadioGroup?, radioId: Int) {
                when (radioId) {
                    R.id.recommend_tab -> {
                        supportFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, videoListFragment).commit()
                    }
                    R.id.add_tab -> {
//                        supportFragmentManager.beginTransaction().addToBackStack(null)
//                            .replace(R.id.fragment_container, VideoListFragment(getService())).commit()
                    }
                    R.id.like_tab -> {
                        supportFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, likeVideoFragment).commit()
                    }
                    R.id.my_tab -> {
                        supportFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, VideoListFragment(getService())).commit()
                    }
                }
            }

        })
        DataBase.getInstance(this@MainActivity)
    }

    private fun getService(): IMiniDouyinService? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(IMiniDouyinService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        if (miniDouyinService == null) {
            miniDouyinService = retrofit?.create(IMiniDouyinService::class.java)
        }

        if (miniDouyinService == null)
            println("Service NULL")
        return miniDouyinService
    }
}
