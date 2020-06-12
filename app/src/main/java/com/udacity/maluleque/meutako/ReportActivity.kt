package com.udacity.maluleque.meutako

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.udacity.maluleque.meutako.adapters.FragmentAdapter
import com.udacity.maluleque.meutako.utils.DateUtils

class ReportActivity : AppCompatActivity(), OnTabSelectedListener {
    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.tabLayout)
    var tabLayout: TabLayout? = null

    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null
    private var adapter: FragmentAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        ButterKnife.bind(this)
        toolbar!!.setTitle(R.string.report)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = FragmentAdapter(supportFragmentManager, DateUtils.generateDates(), TRANSACTIONS_REPORT)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = 3
        viewPager!!.currentItem = 10
        tabLayout!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.addOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}

    companion object {
        private const val TRANSACTIONS_REPORT = 1
    }
}