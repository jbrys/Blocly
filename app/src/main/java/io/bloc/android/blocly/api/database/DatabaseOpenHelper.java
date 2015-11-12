package io.bloc.android.blocly.api.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.bloc.android.blocly.api.database.table.Table;

/**
 * Created by jeffbrys on 11/11/15.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "blocly_db";

    private static final int VERSION = 1;

    private Table[] tables;

    public DatabaseOpenHelper(Context context, Table... tables) {

        super(context, NAME, null, VERSION);
        this.tables = tables;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (Table table : tables) {
            sqLiteDatabase.execSQL(table.getCreateStatement());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for (Table table : tables) {
            table.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }

    }
}
