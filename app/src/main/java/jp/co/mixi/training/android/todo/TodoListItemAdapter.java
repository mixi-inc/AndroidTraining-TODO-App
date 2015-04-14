package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoListItemAdapter extends BindableAdapter<TodoEntity> {
    private static final int LAYOUT_ID = R.layout.todo_list_item;
    private ViewHolder holder;

    public TodoListItemAdapter(Context context, List<TodoEntity> objects) {
        super(context, objects);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(LAYOUT_ID, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final TodoEntity item, int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(item.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (context == null) return;
                Intent intent = new Intent(context, InputTodoActivity.class);
                intent.putExtra(InputTodoActivity.TODO, item.toJson());
                context.startActivity(intent);
            }
        });

    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView title;

        public ViewHolder(View view) {
            this.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            this.title = (TextView) view.findViewById(R.id.title);
        }
    }
}
