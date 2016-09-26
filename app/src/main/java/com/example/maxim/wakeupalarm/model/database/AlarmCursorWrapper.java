package com.example.maxim.wakeupalarm.model.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.maxim.wakeupalarm.model.AlarmClock;

import static com.example.maxim.wakeupalarm.model.database.AlarmDbSchema.*;

public class AlarmCursorWrapper extends CursorWrapper {
    public AlarmCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public AlarmClock getAlarmClock() {
        long time = getLong(getColumnIndex(AlarmTable.Cols.TIME));
        int uuid = getInt(getColumnIndex(AlarmTable.Cols.UUID));
        int activeInt=getInt(getColumnIndex(AlarmTable.Cols.ACTIVE));
        AlarmClock alarmClock=new AlarmClock(time,uuid,activeInt);
        return alarmClock;
    }
}