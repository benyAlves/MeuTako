package com.udacity.maluleque.meutako;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.udacity.maluleque.meutako.adapters.FragmentAdapter;
import com.udacity.maluleque.meutako.utils.DateUtils;
import com.udacity.maluleque.meutako.utils.NetworkNotifier;
import com.udacity.maluleque.meutako.utils.NetworkServiceChecker;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements TransactionListFragment.FabButtonVisibilityListener, NetworkNotifier {

    private static final String TAG = "MainActivity";
    private static final int TRANSACTION_LIST = 0;
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

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivity(intent);
        });

        NetworkServiceChecker networkServiceChecker = new NetworkServiceChecker(this, this);
        networkServiceChecker.execute();

        adapter = new FragmentAdapter(getSupportFragmentManager(), DateUtils.generateDates(), TRANSACTION_LIST);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(10);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);


    }



    @Override
    public void hideFabButton() {
        fab.hide();
    }

    @Override
    public void showFabButton() {
        fab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void notifyInternetConnection(boolean hasConnection) {
        if (!hasConnection) {
            Toast.makeText(this, "Connect to the internet to backup data", Toast.LENGTH_SHORT).show();
        }
    }
}
