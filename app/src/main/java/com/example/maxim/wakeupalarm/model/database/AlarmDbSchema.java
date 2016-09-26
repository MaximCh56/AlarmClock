package com.example.maxim.wakeupalarm.model.database;

public class AlarmDbSchema {
    public static final class AlarmTable {
        public static final String NAME = "alarmes";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TIME = "time";
            public static final String ACTIVE = "active";
        }
    }
}