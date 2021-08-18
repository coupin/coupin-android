package com.kibou.abisoyeoke_lawal.coupinapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.kibou.abisoyeoke_lawal.coupinapp.R

class OnBoardingViewPagerAdapter(private val context : Context, private val sliderImages : Array<Int>,
                                 private val headerTexts : Array<Int>) : PagerAdapter() {

    override fun getCount(): Int {
        return sliderImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.onboarding_slider, null)

        val onBoardingImageView = view.findViewById<ImageView>(R.id.onboarding_img)
        val headerTextView = view.findViewById<TextView>(R.id.header_texts)

        onBoardingImageView.apply {
            setImageResource(sliderImages[position])
            setLayerType(View.LAYER_TYPE_SOFTWARE,null)
        }

        headerTextView.text = context.getString(headerTexts[position])

        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }

}

