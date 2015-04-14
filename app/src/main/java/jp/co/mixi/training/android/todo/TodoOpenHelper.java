package jp.co.mixi.training.android.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * db操作用のクラス
 * ここでは１テーブルしか使わないので、テーブルごとのクラス定義はしないが
 * 1dbに複数テーブルを作るのであれば、テーブルごとに操作を行うクラスを作ったほうが良い
 * Created by Hideyuki.Kikuma on 15/04/12.
 */
public class TodoOpenHelper extends SQLiteOpenHelper {

    // データベースのマイグレーションNOと考える
    private static final int DATABASE_VERSION = 1;

    // DB設定
    public static final String DATABASE_NAME = "Todo.db";
    // どうせよく使うので定義しておく
    // 余裕があれば、builderパターンでCREATE TABLE,DROP TABLEを出力できるようにしておいたほうが良い
    private static final String TYPE_INTEGER = " INTEGER ";
    private static final String TYPE_TEXT = " TEXT ";
    private static final String TYPE_REAL = " REAL ";
    private static final String TYPE_BLOB = " BLOB ";
    private static final String CONSTRAINT_PRIMARY_KEY = " PRIMARY KEY ";
    private static final String CONSTRAINT_NOT_NULL = " NOT NULL ";

    // システム的にcreate_at,update_atは定義しておいた方がよいので定義しておく、アプリケーション側からは見ない項目
    private static final String COMMON_COLUMN_NAME_CREATE_AT = "create_at";
    private static final String COMMON_COLUMN_NAME_UPDATE_AT = "update_at";


    // テーブル定義
    public static final String TODO_TABLE_NAME = "todo";
    // カラム名を定義していく
    public static final String TODO_COLUMN_NAME_TITLE = "title";

    private static final String TODO_TABLE_CREATE =
            "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                    BaseColumns._ID + TYPE_INTEGER + CONSTRAINT_PRIMARY_KEY + ", " +
                    TODO_COLUMN_NAME_TITLE + TYPE_TEXT + CONSTRAINT_NOT_NULL + ", " +
                    COMMON_COLUMN_NAME_CREATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL + ", " +
                    COMMON_COLUMN_NAME_UPDATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL +
                    ");";
    private static final String TODO_TABLE_DELETE = "DROP TABLE IF EXISTS " + TODO_TABLE_NAME;

    public TodoOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ここでアップデート条件を判定する
        db.execSQL(TODO_TABLE_DELETE);
        onCreate(db);
    }

    public long insertTodo(TodoEntity entity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put(TODO_COLUMN_NAME_TITLE, entity.getTitle());
            values.put(COMMON_COLUMN_NAME_CREATE_AT, date.getTime());
            values.put(COMMON_COLUMN_NAME_UPDATE_AT, date.getTime());
            long resId = db.insert(TODO_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            return resId;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Cursorを返されても扱いにくいのでORマッパー的な層を設けてそこで解決するようにする
     * @return
     */
    public List<TodoEntity> loadTodoAll() {
        SQLiteDatabase db = getReadableDatabase();
        // 取得するカラムのリストを定義する
        // 固定的になるので、static finalな定義にしてもいいかもしれない
        String[] projection = {
                BaseColumns._ID,
                TODO_COLUMN_NAME_TITLE,
        };
        // クエリの実行
        // 全件取得なので条件はなし
        Cursor cursor = db.query(TODO_TABLE_NAME, projection, null, null, null, null, BaseColumns._ID + " DESC");
        List<TodoEntity> list = new ArrayList<>();
        // とりあえず最初のレコードに移動する
        boolean hasNext = cursor.moveToFirst();
        while (hasNext) {
            TodoEntity entity = new TodoEntity();
            entity.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
            entity.setTitle(cursor.getString(cursor.getColumnIndex(TODO_COLUMN_NAME_TITLE)));
            list.add(entity);
            hasNext = cursor.moveToNext();
        }

        return list;
    }

}
