package com.example.maxim.wakeupalarm.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.maxim.wakeupalarm.AlarmReceiver;
import com.example.maxim.wakeupalarm.model.database.AlarmBaseHelper;
import com.example.maxim.wakeupalarm.model.database.AlarmCursorWrapper;

import static com.example.maxim.wakeupalarm.model.database.AlarmDbSchema.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Maxim on 17.09.2016.
 */
public class AlarmClockLab {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static AlarmClockLab ourInstance;
    private Random random;
    Intent intent;
    AlarmManager alarmManager;

    public List<AlarmClock> getAlarmClockList() {
        List<AlarmClock> alarmes = new ArrayList<>();
        AlarmCursorWrapper cursor = queryAlarmes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                alarmes.add(cursor.getAlarmClock());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return alarmes;
    }




    final Observable<List<AlarmClock>> operationObservable = Observable.create(new Observable.OnSubscribe<List<AlarmClock>>() {
        @Override
        public void call(Subscriber subscriber) {
            subscriber.onNext(getAlarmClockList());
            subscriber.onCompleted();
        }
    })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());


    public void test(){

        operationObservable.subscribe(new Subscriber<List<AlarmClock>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<AlarmClock> alarmClocks) {
                Log.d("sd", String.valueOf(alarmClocks.size()));
            }

        });

    }

    public AlarmClock newAlarmClock(long time,int active){
        AlarmClock alarmClock=new AlarmClock(time,recursRandom(),active);
        addInDataBase(alarmClock);
        if (active == 1) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,alarmClock.getId(), intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000 * 60 * 60 * 24, pendingIntent);
        }
        return alarmClock;
    }
    private int recursRandom () {
        ArrayList<AlarmClock> alarmClockList= (ArrayList<AlarmClock>) getAlarmClockList();
        int mRandom=random.nextInt(600);
        for (int i=0;i<alarmClockList.size();i++){
            if (mRandom==alarmClockList.get(i).getId()){
                recursRandom();
            }
        }
        return mRandom;
    }
    private AlarmCursorWrapper queryAlarmes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                AlarmTable.NAME,
                null, // Columns - null выбирает все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new AlarmCursorWrapper(cursor);
    }
    public AlarmClock get(int id) {
        AlarmCursorWrapper cursor = queryAlarmes(AlarmTable.Cols.UUID + " = ?", new String[] {String.valueOf(id)});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getAlarmClock();
        } finally {
            cursor.close();
        }
    }


    public void updateExistingAlarmClock(AlarmClock alarmClock,long time,int activeInt){
        if (alarmClock.getActive()==activeInt){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,alarmClock.getId() , intent, 0);
            if (activeInt==0){
                alarmManager.cancel(pendingIntent);
            }
            else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000 * 60 * 60 * 24, pendingIntent);
            }
        }
        alarmClock.setTime(time);
        alarmClock.setActive(activeInt);
        updateAlarmClockInDataBase(alarmClock);
    }
    public static AlarmClockLab getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AlarmClockLab(context);
        }
        return ourInstance;
    }
    public void remove(AlarmClock alarmClock){
        String uuidString = String.valueOf(alarmClock.getId());
        if (alarmClock.getActive() == 1) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,alarmClock.getId(), intent, 0);
            alarmManager.cancel(pendingIntent);
        }
        mDatabase.delete(AlarmTable.NAME,AlarmTable.Cols.UUID + " = ?",new String[] { uuidString });

    }

    public void updateAlarmClockInDataBase(AlarmClock alarmClock) {
        String uuidString = String.valueOf(alarmClock.getId());
        ContentValues values = getContentValues(alarmClock);
        mDatabase.update(AlarmTable.NAME, values, AlarmTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    private AlarmClockLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AlarmBaseHelper(mContext).getWritableDatabase();
        random=new Random();
        intent = new Intent(mContext, AlarmReceiver.class);
        alarmManager = (AlarmManager)mContext.getSystemService(mContext.ALARM_SERVICE);
    }
    private static ContentValues getContentValues(AlarmClock alarmClock) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.Cols.UUID,String.valueOf(alarmClock.getId()));
        values.put(AlarmTable.Cols.TIME, alarmClock.getTime());
        values.put(AlarmTable.Cols.ACTIVE,alarmClock.getActive());
        return values;
    }
    private void addInDataBase(AlarmClock alarmClock) {
        ContentValues values = getContentValues(alarmClock);
        mDatabase.insert(AlarmTable.NAME, null, values);
    }
}
