package com.example.finalcloudproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.finalcloudproject.user.ChatUserFragment
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.ActivityDashBoardUserBinding
import com.example.finalcloudproject.user.HomeUserFragment
import com.example.finalcloudproject.user.ProfileUserFragment
import com.example.finalcloudproject.user.SearchFragment
import com.example.finalcloudproject.user.SubsecribeFragment
import com.example.finalcloudproject.utils.BaseActivity

class DashBoardUserActivity : BaseActivity() {

    private lateinit var binding: ActivityDashBoardUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_fragment_user -> makeCurrentFragment(HomeUserFragment())
                R.id.search_fragment_user -> makeCurrentFragment(SearchFragment())
                R.id.subsecribe_fragment_user -> makeCurrentFragment(SubsecribeFragment())
                R.id.profile_fragment_user -> makeCurrentFragment(ProfileUserFragment())
                R.id.chat_user_fragment -> makeCurrentFragment(ChatUserFragment())

                else -> {

                }
            }
            true
        }

    }
    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container_user, fragment).addToBackStack(null)
            commit()
        }
    }


    override fun onBackPressed() {
        doubleBackToExit()
    }


}



