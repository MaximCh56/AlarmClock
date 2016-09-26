package com.example.maxim.wakeupalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.maxim.wakeupalarm.model.AlarmClock;
import com.example.maxim.wakeupalarm.model.AlarmClockLab;

import java.util.Date;
import java.util.List;

/**
 * Created by Maxim on 05.09.2016.
 */
public class AlarmListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AlarmClock mAlarmClock;
        private TextView mTitleTextView;
        private CheckBox checkBox;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_list_item_titleTextView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
        public void bindCrime(AlarmClock alarmClock) {
            mAlarmClock = alarmClock;
            mTitleTextView.setText(DateFormat.format("'Alarm clock' HH:mm", new Date(mAlarmClock.getTime())).toString());
            if(mAlarmClock.getActive()==1){
                checkBox.setChecked(true);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), AlarmClockActivity.class);
            intent.putExtra("test",mAlarmClock.getId());
            startActivity(intent);
        }
    }
    private void updateUI() {
        AlarmClockLab alarmLab=AlarmClockLab.getInstance(getActivity());
        mAdapter = new CrimeAdapter(alarmLab.getAlarmClockList());
        alarmLab.test();
        mCrimeRecyclerView.setAdapter(mAdapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Intent intent = new Intent(getActivity(), AlarmClockActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<AlarmClock> mCrimes;
        public CrimeAdapter(List<AlarmClock> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            AlarmClock crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

}
