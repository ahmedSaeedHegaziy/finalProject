package com.example.finalcloudproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.finalcloudproject.admin.NotificationsFragment
import com.example.finalcloudproject.admin.ChatAdminFragment
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.ActivityDashBoardAdminBinding
import com.example.finalcloudproject.admin.HomeAdminFragment
import com.example.finalcloudproject.admin.ProfileAdminFragment
import com.example.finalcloudproject.user.SearchFragment
import com.example.finalcloudproject.utils.BaseActivity

class DashBoardAdminActivity : BaseActivity()
{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDashBoardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        makeCurrentFragment(HomeAdminFragment())



        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_fragment -> makeCurrentFragment(HomeAdminFragment())
                R.id.search_fragment -> makeCurrentFragment(SearchFragment())
                R.id.chat_admin_fragment -> makeCurrentFragment(ChatAdminFragment())
                R.id.profile_fragment -> makeCurrentFragment(ProfileAdminFragment())
                R.id.notifications_admin_fragment -> makeCurrentFragment(NotificationsFragment())

                else -> {

                }
            }
            true
        }
    }


    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).addToBackStack(null)
            commit()
        }
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }


}





















