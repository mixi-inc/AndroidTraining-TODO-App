package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoListItemAdapter extends ArrayAdapter<TodoEntity> {
    public TodoListItemAdapter(Context context, int resource, List<TodoEntity> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.todo_list_item, null);
        }
        TodoEntity entity = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(entity.getTitle());

        return convertView;
    }
}
