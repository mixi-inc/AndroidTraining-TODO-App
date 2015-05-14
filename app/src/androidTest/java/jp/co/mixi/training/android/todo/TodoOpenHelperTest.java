package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * Created by Hideyuki.Kikuma on 15/05/04.
 */
public class TodoOpenHelperTest extends AndroidTestCase {

    // テスト用のSQLiteのためのプレフィックス
    private static final String TEST_FILE_PREFIX = "test_";
    private TodoOpenHelper helper;

    @Before
    public void setUp() throws Exception {
        // RenamingDelegatingContextを使うことで毎回クリーンなDB環境が取得できる
        Context context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
        helper = new TodoOpenHelper(context);
    }

    @After
    public void tearDown() throws Exception {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

    @Test
    public void testOnUpgrade() throws Exception {
    }

    @Test
    public void testInsertTodo() throws Exception {
        TodoEntity entity = new TodoEntity();
        entity.setId(1);
        entity.setTitle("sample");
        helper.insertTodo(entity);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("select * from todo", null);
            c.moveToFirst();
            assertEquals(1, c.getCount());
            assertEquals("sample", c.getString(c.getColumnIndex("title")));
            assertEquals(1, c.getLong(c.getColumnIndex("_id")));
        } finally {
            if (c != null) c.close();
        }
    }

    @Test
    public void testLoadTodoAll() throws Exception {
        List<TodoEntity> results = helper.loadTodoAll();
    }

    @Test
    public void testFindTodoById() throws Exception {

    }

    @Test
    public void testUpdateTodo() throws Exception {

    }

    @Test
    public void testIsExist() throws Exception {

    }

    @Test
    public void testGetDatabaseName() throws Exception {

    }

}