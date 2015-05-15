package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * Created by Hideyuki.Kikuma on 15/05/04.
 */
public class TodoOpenHelperV1Test extends AndroidTestCase {

    // テスト用のSQLiteのためのプレフィックス
    private static final String TEST_FILE_PREFIX = "test_";
    private TodoOpenHelper helper;

    @Before
    public void setUp() throws Exception {
        // RenamingDelegatingContextを使うことで毎回クリーンなDB環境が取得できる
        Context context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        mContext = context;
        helper = new TodoOpenHelper(context, 1);
    }

    @After
    public void tearDown() throws Exception {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

    /**
     * V1のDBにはdeadlineカラムが存在しないのでエラー
     *
     * @throws Exception
     */
    @Test(expected = SQLiteException.class)
    public void onUpgradeBefore() throws Exception {
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.execSQL(getInsertSqlV2("todo", 1, "title", new Date().getTime()));

        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * マイグレーション後はdeadlineカラムが存在しているのでinsert成功
     *
     * @throws Exception
     */
    @Test
    public void testOnUpgrade() throws Exception {
        TodoOpenHelper helperV2 = new TodoOpenHelper(mContext);
        SQLiteDatabase db = helperV2.getWritableDatabase();
        Date date = new Date();
        db.execSQL(getInsertSqlV2("todo", 1, "title", date.getTime()));
        TodoEntity entity = helperV2.findTodoById(1);
        assertEquals("title", entity.getTitle());
        assertEquals(date, entity.getDeadline());
    }

    private String getInsertSqlV2(String tableName, int id, String title, long deadline) {
        Date date = new Date();
        return String.format("insert into %s(_id,title,create_at,update_at,deadline) values(%d,\"%s\",%d,%d,%d)",
                tableName, id, title, date.getTime(), date.getTime(), deadline);

    }
}