package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hideyuki.Kikuma on 15/04/12.
 */
public class TodoOpenHelper extends SQLiteOpenHelper {

    // データベースのマイグレーションNOと考える
    private static final int DATABASE_VERSION = 1;

    // DB設定
    public static final String DATABASE_NAME = "Todo.db";

    public TodoOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
