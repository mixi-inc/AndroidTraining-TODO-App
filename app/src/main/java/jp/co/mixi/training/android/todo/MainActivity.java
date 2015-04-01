package jp.co.mixi.training.android.todo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.mixi.training.android.todo.entity.TodoEntity;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int INPUT_TODO_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private TodoListItemAdapter todoListItemAdapter;
        private BroadcastReceiver todoChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (todoListItemAdapter == null) return;
                todoListItemAdapter.clear();
                todoListItemAdapter.addAll(loadTodo());
            }
        };

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        public void onStart() {
            super.onStart();

            List<TodoEntity> list = loadTodo();
            ListView listView = (ListView) getActivity().findViewById(R.id.todoList);
            todoListItemAdapter = new TodoListItemAdapter(getActivity(), list);
            listView.setAdapter(todoListItemAdapter);
            View addTodo = getActivity().findViewById(R.id.add_todo);
            addTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "onClick");
                    Activity activity = getActivity();
                    if (activity == null) return;
                    Intent intent = new Intent(getActivity(), InputTodoActivity.class);
                    getActivity().startActivity(intent);
                }
            });
            IntentFilter filter = new IntentFilter(TodoSaveService.ACTION_COMPLETED_SAVE);

            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
            manager.registerReceiver(todoChangeReceiver, filter);
        }

        @Override
        public void onStop() {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
            manager.unregisterReceiver(todoChangeReceiver);
            super.onStop();

        }

        private List<TodoEntity> loadTodo() {
            List<TodoEntity> list = new ArrayList<>();
            SharedPreferences sp = getActivity().getSharedPreferences("todo", MODE_PRIVATE);
            Map<String, ?> map = sp.getAll();
            for (String key : map.keySet()) {
                String value = (String) map.get(key);
                TodoEntity entity = TodoEntity.fromJson(value);
                list.add(entity);
            }
            return list;

        }
    }
}
