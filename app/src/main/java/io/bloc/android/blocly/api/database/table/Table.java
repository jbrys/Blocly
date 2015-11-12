package io.bloc.android.blocly.api.database.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jeffbrys on 11/10/15.
 */
public abstract class Table {

    public static final String COLUMN_ID = "id";

    public abstract String getName();

    public abstract String getCreateStatement();

    public void onUpgrade(SQLiteDatabase writableDatabase, int oldVersion, int newVersion) {

    }
}
