package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoListItemAdapter extends ArrayAdapter<TodoEntity> {
    public TodoListItemAdapter(Context context, int resource, List<TodoEntity> objects) {
        super(context, resource, objects);
    }
}
