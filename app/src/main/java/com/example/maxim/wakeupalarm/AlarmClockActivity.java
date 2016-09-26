package com.example.maxim.wakeupalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.maxim.wakeupalarm.model.AlarmClock;
import com.example.maxim.wakeupalarm.model.AlarmClockLab;

import java.util.Calendar;

public class AlarmClockActivity extends AppCompatActivity
{
    TimePicker alarmTimePicker;
    AlarmManager alarmManager;
    AlarmClockLab alarmLab;
    Button buttonSet;
    Button buttonRemove;
    AlarmClock alarmClock;
    boolean newAlarmClockBoolean;
    CheckBox checkBoxActive;
    long time;
    int activeInt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        buttonRemove=(Button)findViewById(R.id.buttonRemove);
        buttonSet=(Button)findViewById(R.id.buttonSet);
        checkBoxActive=(CheckBox)findViewById(R.id.checkBoxActive);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAlarmClock();
            }
        });
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAlarmClock();
            }
        });
        alarmLab=AlarmClockLab.getInstance(getApplicationContext());
        Intent intent = getIntent();
        newAlarmClockBoolean =intent.getIntExtra("test",-1)==-1;
        if (!newAlarmClockBoolean){
            alarmClock=alarmLab.get(intent.getIntExtra("test",-1));
            if(alarmClock.getActive()==1){
                checkBoxActive.setChecked(true);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarmClock.getTime());
            alarmTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            alarmTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        }
    }
    public void saveAlarmClock(){
        if(checkBoxActive.isChecked()){
            activeInt=1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            time = time + (1000 * 60 * 60 * 24);
        }
        if (!newAlarmClockBoolean){
            alarmLab.updateExistingAlarmClock(alarmClock,time,activeInt);
        }else {
            alarmClock=alarmLab.newAlarmClock(calendar.getTimeInMillis(),activeInt);
        }
        Toast.makeText(AlarmClockActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
    }
    public void removeAlarmClock()
    {
        if (alarmClock!=null){
            alarmLab.remove(alarmClock);
        }
        Toast.makeText(AlarmClockActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        finish();
    }

}
