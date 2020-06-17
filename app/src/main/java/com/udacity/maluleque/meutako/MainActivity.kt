package com.udacity.maluleque.meutako

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.udacity.maluleque.meutako.TransactionListFragment.FabButtonVisibilityListener
import com.udacity.maluleque.meutako.adapters.FragmentAdapter
import com.udacity.maluleque.meutako.utils.DateUtils
import com.udacity.maluleque.meutako.utils.NetworkNotifier
import com.udacity.maluleque.meutako.utils.NetworkServiceChecker

class MainActivity : AppCompatActivity(), FabButtonVisibilityListener, NetworkNotifier {
    @JvmField
    @BindView(R.id.fab)
    var fab: FloatingActionButton? = null

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
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        toolbar!!.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
        fab!!.setOnClickListener { view: View? ->
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
        val networkServiceChecker = NetworkServiceChecker(this, this)
        networkServiceChecker.execute()
        adapter = FragmentAdapter(supportFragmentManager, DateUtils.generateDates(), TRANSACTION_LIST)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = 3
        viewPager!!.currentItem = 10
        tabLayout!!.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout!!.setupWithViewPager(viewPager)
    }

    override fun hideFabButton() {
        fab!!.hide()
    }

    override fun showFabButton() {
        fab!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun notifyInternetConnection(hasConnection: Boolean) {
        if (!hasConnection) {
            Toast.makeText(this, "Connect to the internet to backup data", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val TRANSACTION_LIST = 0
    }
}