package com.example.maxim.wakeupalarm;

import android.support.v4.app.Fragment;

public class AlarmListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AlarmListFragment();
    }
}