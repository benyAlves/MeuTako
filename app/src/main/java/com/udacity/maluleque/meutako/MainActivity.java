package com.udacity.maluleque.meutako;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.udacity.maluleque.meutako.adapters.FragmentAdapter;
import com.udacity.maluleque.meutako.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, TransactionListFragment.FabButtonVisibilityListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivity(intent);
        });

        adapter = new FragmentAdapter(getSupportFragmentManager(), DateUtils.generateDates());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(10);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.i(TAG, "onTabSelected " + tab.getPosition());
        TransactionListFragment tListFragment = (TransactionListFragment) adapter.getItem(tab.getPosition());
        Log.i(TAG, "onTabSelected " + tListFragment.getArguments().getString("param2"));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.i(TAG, "onTabUnselected " + tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.i(TAG, "onTabReselected " + tab.getPosition());
    }

    @Override
    public void hideFabButton() {
        fab.hide();
    }

    @Override
    public void showFabButton() {
        fab.show();
    }

}
