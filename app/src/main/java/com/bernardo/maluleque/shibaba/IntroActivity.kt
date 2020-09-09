package com.bernardo.maluleque.shibaba

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import com.bernardo.maluleque.shibaba.preferences.PreferencesManager
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    @BindView(R.id.view_pager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.btn_skip)
    lateinit var btnSkip: Button

    @BindView(R.id.btn_next)
    lateinit var btnNext: Button

    private lateinit var layouts: IntArray
    private var preferences: PreferencesManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        ButterKnife.bind(this)

        preferences = PreferencesManager.getInstance(getSharedPreferences(LAUNCH_PREF, Context.MODE_PRIVATE))
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (!preferences!!.isFirstLaunch) {
            openSignInScreen()
        }
        layouts = intArrayOf(
                R.layout.intro_view_track,
                R.layout.intro_view_notification,
                R.layout.intro_view_history
        )
        addBottomDots(0)
        changeStatusBarColor()
        val viewPagerAdapter = ViewPagerAdapter()
        viewPager.adapter = viewPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        btnSkip.setOnClickListener { v: View? -> openSignInScreen() }
        btnNext.setOnClickListener { v: View? ->
            val current = getItem(+1)
            if (current < layouts.size) {
                viewPager!!.currentItem = current
            } else {
                openSignInScreen()
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts.size)
        layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(getColor(R.color.dot_inactive))
            layoutDots.addView(dots[i])
        }
        if (dots.size > 0) dots[currentPage]!!.setTextColor(getColor(R.color.dot_active))
    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts.size - 1) {
                btnNext.text = getString(R.string.start)
                btnSkip.visibility = View.GONE
            } else {
                btnNext.text = getString(R.string.next)
                btnSkip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun openSignInScreen() {
        preferences!!.isFirstLaunch = false
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    inner class ViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    companion object {
        private const val LAUNCH_PREF = "launch-prefs"
    }
}