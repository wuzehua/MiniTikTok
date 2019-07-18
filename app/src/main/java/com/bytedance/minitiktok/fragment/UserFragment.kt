package com.bytedance.minitiktok.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bytedance.minitiktok.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_fragment.*

open class UserFragment(context: Context) : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val sharedPreference = context!!.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)
                sharedPreference.edit().putString("user_name", textView.text.toString()).apply()
            }
        })
        return view
    }
}