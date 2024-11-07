package com.example.plswork;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PieChartAdapter extends FragmentStateAdapter {

    public PieChartAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new IncomePieChart();
            case 1:
                return new ExpensePieChart();
            default:
                return new ExpensePieChart();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
