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
    private static final int DATABASE_VERSION = 2;

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
    public static final String TODO_COLUMN_NAME_DEADLINE = "deadline";

    private static final String TODO_TABLE_CREATE_V1 =
            "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                    BaseColumns._ID + TYPE_INTEGER + CONSTRAINT_PRIMARY_KEY + ", " +
                    TODO_COLUMN_NAME_TITLE + TYPE_TEXT + CONSTRAINT_NOT_NULL + ", " +
                    COMMON_COLUMN_NAME_CREATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL + ", " +
                    COMMON_COLUMN_NAME_UPDATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL +
                    ");";
    private static final String TODO_TABLE_CREATE_V2 =
            "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                    BaseColumns._ID + TYPE_INTEGER + CONSTRAINT_PRIMARY_KEY + ", " +
                    TODO_COLUMN_NAME_TITLE + TYPE_TEXT + CONSTRAINT_NOT_NULL + ", " +
                    TODO_COLUMN_NAME_DEADLINE + TYPE_INTEGER + ", " +
                    COMMON_COLUMN_NAME_CREATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL + ", " +
                    COMMON_COLUMN_NAME_UPDATE_AT + TYPE_INTEGER + CONSTRAINT_NOT_NULL +
                    ");";
    private static final String TODO_TABLE_DELETE = "DROP TABLE IF EXISTS " + TODO_TABLE_NAME;
    private static final String ALTER_TABLE_FOR_V2 = "alter table " + TODO_TABLE_NAME + " add column " + TODO_COLUMN_NAME_DEADLINE + " " + TYPE_INTEGER;

    private final int currentVersion;
    /* package */ TodoOpenHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        currentVersion =version;
    }
    public TodoOpenHelper(Context context) {
        this(context, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (currentVersion == 1) {
            db.execSQL(TODO_TABLE_CREATE_V1);
            return;
        }
        db.execSQL(TODO_TABLE_CREATE_V2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ここでアップデート条件を判定する
        if (oldVersion < 2) {
            db.execSQL(ALTER_TABLE_FOR_V2);
        }
    }

    public long insertTodo(TodoEntity entity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put(TODO_COLUMN_NAME_TITLE, entity.getTitle());
            if (entity.getDeadline() == null) {
                values.putNull(TODO_COLUMN_NAME_DEADLINE);
            } else {
                values.put(TODO_COLUMN_NAME_DEADLINE, entity.getDeadline().getTime());
            }
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
     *
     * @return
     */
    public List<TodoEntity> loadTodoAll() {
        SQLiteDatabase db = getReadableDatabase();
        // 取得するカラムのリストを定義する
        // 固定的になるので、static finalな定義にしてもいいかもしれない
        String[] projection = {
                BaseColumns._ID,
                TODO_COLUMN_NAME_TITLE,
                TODO_COLUMN_NAME_DEADLINE,
        };
        // クエリの実行
        // 全件取得なので条件はなし
        Cursor cursor = null;
        List<TodoEntity> list = new ArrayList<>();
        try {
            cursor = db.query(TODO_TABLE_NAME, projection, null, null, null, null, BaseColumns._ID + " DESC");
            // とりあえず最初のレコードに移動する
            boolean hasNext = cursor.moveToFirst();
            while (hasNext) {
                TodoEntity entity = new TodoEntity();
                entity.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                entity.setTitle(cursor.getString(cursor.getColumnIndex(TODO_COLUMN_NAME_TITLE)));
                if (!cursor.isNull(cursor.getColumnIndex(TODO_COLUMN_NAME_DEADLINE))) {
                    entity.setDeadline(cursor.getLong(cursor.getColumnIndex(TODO_COLUMN_NAME_DEADLINE)));
                }
                list.add(entity);
                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    /**
     * id指定で取得するメソッド
     *
     * @param id 取得するID
     * @return 取得できたTodoEntity 取得できなかった場合null
     */
    public TodoEntity findTodoById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        // 取得するカラムのリストを定義する
        // 固定的になるので、static finalな定義にしてもいいかもしれない
        String[] projection = {
                BaseColumns._ID,
                TODO_COLUMN_NAME_TITLE,
                TODO_COLUMN_NAME_DEADLINE,
        };
        // 条件文
        String selection = BaseColumns._ID + " = ?";
        // 条件のパラメータ
        String[] selectionArgs = {
                String.valueOf(id)
        };
        // クエリの実行
        Cursor cursor = db.query(TODO_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            TodoEntity entity = new TodoEntity();
            entity.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
            entity.setTitle(cursor.getString(cursor.getColumnIndex(TODO_COLUMN_NAME_TITLE)));
            if (!cursor.isNull(cursor.getColumnIndex(TODO_COLUMN_NAME_DEADLINE))) {
                entity.setDeadline(cursor.getLong(cursor.getColumnIndex(TODO_COLUMN_NAME_DEADLINE)));
            }
            return entity;
        }

        return null;
    }

    public int updateTodo(TodoEntity entity) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // 条件文
            String selection = BaseColumns._ID + " = ?";
            // 条件のパラメータ
            String[] selectionArgs = {
                    String.valueOf(entity.getId())
            };
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put(TODO_COLUMN_NAME_TITLE, entity.getTitle());
            if (entity.getDeadline() == null) {
                values.putNull(TODO_COLUMN_NAME_DEADLINE);
            } else {
                values.put(TODO_COLUMN_NAME_DEADLINE, entity.getDeadline().getTime());
            }
            values.put(COMMON_COLUMN_NAME_UPDATE_AT, date.getTime());
            int count = db.update(TODO_TABLE_NAME, values, selection, selectionArgs);
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 本来はちゃんとチェックすべきだが、簡単なチェックのみにする
     *
     * @param entity 存在確認したいtodo
     * @return
     */
    public boolean isExist(TodoEntity entity) {
        // デフォルト値が0なので、0かをチェック
        if (entity.getId() != 0) {
            return true;
        }
        return false;

    }

}
