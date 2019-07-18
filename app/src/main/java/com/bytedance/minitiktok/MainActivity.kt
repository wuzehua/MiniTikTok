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
import com.bytedance.minitiktok.fragment.FavoriteVideoFragment
import com.bytedance.minitiktok.fragment.UserFragment
import com.bytedance.minitiktok.model.User
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

    private lateinit var mCurrentFragment:Fragment

    private val REQUEST_EXTERNAL_CAMERA = 101

    private val permissions = arrayOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var user = User()
        user.userName = getString(R.string.un_registe_user_name)
        user.passwd = getString(R.string.un_registe_passwd)
        initRadio()
        DataBase.getInstance(this@MainActivity).insertUser(user)

        val videoListFragment = VideoListFragment(getService())
        val likeVideoFragment = LikeVideoFragment(this@MainActivity, getService())
        val userFragment = UserFragment(this)
        val favoriteFragment = FavoriteVideoFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, videoListFragment).commit()
        mCurrentFragment = videoListFragment
        //val pagerAdapter = FragmentViewPagerAdapter(supportFragmentManager)
        //pagerAdapter.addFragment(videoListFragment)
        //vp_viewPager.adapter = pagerAdapter

        radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: RadioGroup?, radioId: Int) {
                when (radioId) {
                    R.id.recommend_tab -> {
                        switchFragment(videoListFragment)
                        textView.text = "最新发布"
                    }
                    R.id.record_tab -> {
                        switchFragment(favoriteFragment)
                        textView.text = "记录"
                    }
                    R.id.like_tab -> {
                        switchFragment(likeVideoFragment)
                        textView.text = "Like"
                    }
                    R.id.my_tab -> {
                        textView.text = "我的"
                        switchFragment(userFragment)
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

    private fun switchFragment(fragment: Fragment)
    {
        if(mCurrentFragment != fragment)
        {
            if(fragment.isAdded)
            {
                supportFragmentManager
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .show(fragment)
                    .commit()
            }
            else
            {
                supportFragmentManager
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .add(R.id.fragment_container,fragment)
                    .commit()
            }

            mCurrentFragment = fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

            object : AsyncTask<Intent, Int, String>() {
                override fun doInBackground(vararg data: Intent): String {
                    try {
                        val coverImagePart =
                            getMultipartFromPath("cover_image", data.get(0).extras.getString("mImgPath"))
                        val videoPart = getMultipartFromPath("video", data.get(0).extras.getString("mVideoPath"))
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
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data)
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

    private fun initRadio() {
        val draw_main = resources.getDrawable(R.drawable.tab_main_selector)
        draw_main.setBounds(0, 0, 100, 100)
        recommend_tab.setCompoundDrawables(null, draw_main, null, null)

        val draw_record = resources.getDrawable(R.drawable.tab_record_selector)
        draw_record.setBounds(0, 0, 100, 100)
        record_tab.setCompoundDrawables(null, draw_record, null, null)

        val draw_like = resources.getDrawable(R.drawable.tab_like_selector)
        draw_like.setBounds(0, 0, 100, 100)
        like_tab.setCompoundDrawables(null, draw_like, null, null)

        val draw_me = resources.getDrawable(R.drawable.tab_me_selector)
        draw_me.setBounds(0, 0, 100, 100)
        my_tab.setCompoundDrawables(null, draw_me, null, null)

    }
}
