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
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = helper.getReadableDatabase();
            c = db.rawQuery("select * from todo", null);
            c.moveToFirst();
            assertEquals(1, c.getCount());
            assertEquals("sample", c.getString(c.getColumnIndex("title")));
            assertEquals(1, c.getLong(c.getColumnIndex("_id")));
        } finally {
            if (c != null) c.close();
            if (db != null) db.close();
        }
    }

    @Test
    public void testLoadTodoAll() throws Exception {
        int listCount = 5;
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int i = 0; i < listCount; i++) {
            db.execSQL(getInsertSql("todo", i, "title" + i));
        }

        // exec
        List<TodoEntity> results = helper.loadTodoAll();

        assertEquals(listCount, results.size());
        for (int i = 0; i < listCount; i++) {
            TodoEntity entity = results.get(i);
            long id = listCount - 1 - i;
            assertEquals(id, entity.getId());
            assertEquals("title" + id, entity.getTitle());

        }
    }

    @Test
    public void testFindTodoById() throws Exception {
        int listCount = 5;
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int i = 0; i < listCount; i++) {
            db.execSQL(getInsertSql("todo", i, "title" + i));
        }

        for (int i = 0; i < listCount; i++) {
            TodoEntity entity = helper.findTodoById(i);
            assertEquals(i, entity.getId());
            assertEquals("title" + i, entity.getTitle());
        }
        // 存在しないidはnull
        TodoEntity entity = helper.findTodoById(listCount);
        assertNull(entity);
    }

    @Test
    public void testUpdateTodo() throws Exception {
        TodoEntity entity = new TodoEntity();
        entity.setId(1);
        entity.setTitle("hoge");
        helper.insertTodo(entity);
        TodoEntity result = helper.findTodoById(1);
        assertEquals("hoge", result.getTitle());
        TodoEntity updateEntity = new TodoEntity();
        updateEntity.setId(1);
        updateEntity.setTitle("fuga");
        int count = helper.updateTodo(updateEntity);
        assertEquals(1, count);
        result = helper.findTodoById(1);
        assertEquals("fuga", result.getTitle());
    }

    @Test
    public void testUpdateTodoNotExists() throws Exception {
        TodoEntity updateEntity = new TodoEntity();
        updateEntity.setId(1);
        updateEntity.setTitle("fuga");
        assertEquals(0, helper.updateTodo(updateEntity));
    }

    @Test
    public void testIsExist() throws Exception {
        TodoEntity entity = new TodoEntity();
        entity.setId(1);
        entity.setTitle("hoge");
        assertTrue(helper.isExist(entity));
        entity.setId(0);
        assertFalse(helper.isExist(entity));
    }

    @Test
    public void testGetDatabaseName() throws Exception {
        assertEquals("Todo.db", helper.getDatabaseName());
    }

    private String getInsertSql(String tableName, int id, String title) {
        Date date = new Date();
        return String.format("insert into %s(_id,title,create_at,update_at) values(%d,\"%s\",%d,%d)",
                tableName, id, title, date.getTime(), date.getTime());

    }
}