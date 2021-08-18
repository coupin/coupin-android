package com.kibou.abisoyeoke_lawal.coupinapp.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.OnBoardingViewPagerAdapter
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.jetbrains.anko.toast


class OnboardingActivity : AppCompatActivity(), View.OnClickListener {

    private val onBoardingImages = arrayOf(R.drawable.img_onboarding_1, R.drawable.img_onboarding_2, R.drawable.img_onboarding_3, R
        .drawable.img_onboarding_4)

    private val  headerTexts = arrayOf(R.string.onboarding_1, R.string.onboarding_2, R.string.onboarding_3, R.string.onboarding_4)

    override fun onClick(v: View?) {
        when(v?.id){
            next_btn.id ->{
                when(view_pager.currentItem){
                    in 0..2 -> {
                        view_pager.setCurrentItem(view_pager.currentItem + 1, true)
                    }
                    3 -> {
                        PreferenceMngr.setIsFirstRun(true)
                        startActivity(Intent(this, LandingActivity::class.java))
                        finish()
                    }
                }
            }
            skip_btn.id -> {
                PreferenceMngr.setIsFirstRun(true)
                startActivity(Intent(this, LandingActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_onboarding)

        setUpOnClickListeners()
        setUpViewPager()
    }

    private fun setUpOnClickListeners(){
        next_btn.setOnClickListener(this)
        skip_btn.setOnClickListener(this)
    }

    private fun setUpViewPager(){
        view_pager.adapter = OnBoardingViewPagerAdapter(this, onBoardingImages, headerTexts)
        indicator.setupWithViewPager(view_pager, true)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when(position){
                    3 -> {
                        PreferenceMngr.setIsFirstRun(true)
                        next_btn.text = getString(R.string.let_s_start)
                    }
                }
            }
        })
    }
}