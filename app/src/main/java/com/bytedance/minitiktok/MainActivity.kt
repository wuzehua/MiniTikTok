package com.bytedance.minitiktok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bytedance.minitiktok.api.IMiniDouyinService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var miniDouyinService: IMiniDouyinService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun getService(): IMiniDouyinService?
    {
        if (retrofit == null)
        {
            retrofit = Retrofit.Builder()
                .baseUrl(IMiniDouyinService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        if(miniDouyinService == null)
        {
            miniDouyinService = retrofit?.create(IMiniDouyinService::class.java)
        }

        return miniDouyinService
    }
}
