package com.bytedance.minitiktok.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bytedance.minitiktok.R
import com.bytedance.minitiktok.db.DataBase
import com.bytedance.minitiktok.model.User
import kotlinx.android.synthetic.main.user_fragment.*
import kotlinx.android.synthetic.main.user_fragment.view.*

open class UserFragment(context: Context) : Fragment() {

    private val sharedPreference = context!!.getSharedPreferences("MiniTikTok", Context.MODE_PRIVATE)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        view.editText.setHint(
            sharedPreference.getString(
                "user_name",
                context!!.getString(R.string.un_registe_user_name)
            )
        )

        view.button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var user = User()
                user.userName = editText.text.toString()
                user.passwd = context!!.getString(R.string.un_registe_passwd)
                DataBase.getInstance(context!!).insertUser(user)
                sharedPreference.edit().putString("user_name", editText.text.toString()).apply()
                view.editText.setHint(view.editText.text)
            }
        })
        return view
    }
}