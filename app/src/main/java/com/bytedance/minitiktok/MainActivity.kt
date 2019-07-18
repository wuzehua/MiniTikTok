package com.bytedance.minitiktok

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.View
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
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.AsyncTask
import android.widget.Button
import android.widget.Toast
import com.bytedance.minitiktok.utils.ResourceUtils
import com.bytedance.minitiktok.utils.Utils
import com.bytedance.minitiktok.utils.Utils.reuqestPermissions
import com.bytedance.minitiktok.utils.Utils.isPermissionsReady
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var miniDouyinService: IMiniDouyinService? = null

    private val REQUEST_VIDEO_CAPTURE = 1

    private val REQUEST_EXTERNAL_CAMERA = 101

    private val permissions = arrayOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val videoListFragment = VideoListFragment(getService())
        val likeVideoFragment = LikeVideoFragment(getService())
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, videoListFragment).commit()
        //val pagerAdapter = FragmentViewPagerAdapter(supportFragmentManager)
        //pagerAdapter.addFragment(videoListFragment)
        //vp_viewPager.adapter = pagerAdapter

        radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: RadioGroup?, radioId: Int) {
                when (radioId) {
                    R.id.recommend_tab -> {
                        supportFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, videoListFragment).commit()
                        textView.text = "最新发布"
                    }
                    R.id.add_tab -> {
                    }
                    R.id.like_tab -> {
                        supportFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container, likeVideoFragment).commit()
                        textView.text = "Like"
                    }
                    R.id.my_tab -> {
                        textView.text = "我的"
//                        supportFragmentManager.beginTransaction().addToBackStack(null)
//                            .replace(R.id.fragment_container, VideoListFragment(getService())).commit()
                    }
                }
            }

        })

        post_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (Utils.isPermissionsReady(this@MainActivity, permissions)) {
                    startActivityForResult(Intent(this@MainActivity, VideoActivity::class.java), REQUEST_VIDEO_CAPTURE)
                } else {
                    Utils.reuqestPermissions(this@MainActivity, permissions, REQUEST_EXTERNAL_CAMERA)
                }
            }

        })

        DataBase.getInstance(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

            val coverImagePart = getMultipartFromPath("cover_image", data!!.extras.getString("mImgPath"))
            val videoPart = getMultipartFromPath("video", data!!.extras.getString("mVideoPath"))
            object : AsyncTask<Any, Int, String>() {
                override fun doInBackground(vararg objects: Any): String {
                    try {
                        val postResponse =
                            getService()?.postVideo(
                                "3170105369",
                                "shenmishajing",
                                coverImagePart,
                                videoPart
                            )!!.execute()
                                .body()
                        return postResponse?.getUrl()!!
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return "connection break"
                    }

                }

                override fun onPostExecute(s: String) {
                    super.onPostExecute(s)
                    Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
                }
            }.execute()
        }
    }

    private fun getMultipartFromPath(name: String, path: String): MultipartBody.Part {
        val f = File(path)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f)
        return MultipartBody.Part.createFormData(name, f.name, requestFile)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_CAMERA -> if (Utils.isPermissionsReady(this, permissions))
                startActivityForResult(Intent(this@MainActivity, VideoActivity::class.java), REQUEST_VIDEO_CAPTURE)
        }
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
