package com.example.plswork;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StatisticsActivity extends AppCompatActivity {
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Enable the back button on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Statistics");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle back navigation
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Income");
                            break;
                        case 1:
                            tab.setText("Expenses");
                            break;
                    }
                }).attach();
    }

    private void setupViewPager(ViewPager2 viewPager) {
        FragmentStateAdapter pieChartAdapter = new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return new IncomePieChart(); // Your Income Pie Chart fragment
                } else {
                    return new ExpensePieChart(); // Your Expense Pie Chart fragment
                }
            }

            @Override
            public int getItemCount() {
                return 2; // Two fragments: Income and Expenses
            }
        };
        viewPager.setAdapter(pieChartAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Back button pressed
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
