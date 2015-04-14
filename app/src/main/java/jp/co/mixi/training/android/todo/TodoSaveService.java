package jp.co.mixi.training.android.todo;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import jp.co.mixi.training.android.todo.entity.TodoEntity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class TodoSaveService extends IntentService {
    private static final String TAG = TodoSaveService.class.getSimpleName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SAVE = "jp.co.mixi.training.android.todo.action.save";
    public static final String ACTION_COMPLETED_SAVE = "jp.mixi.sample.android.intent.action.saveCompleted";

    private static final String EXTRA_TODO_JSON = "jp.co.mixi.training.android.todo.extra.todoJson";

    /**
     * Starts this service to perform action Save with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSave(Context context, TodoEntity entity) {
        String json = entity.toJson();
        Intent intent = new Intent(context, TodoSaveService.class);
        intent.setAction(ACTION_SAVE);
        intent.putExtra(EXTRA_TODO_JSON, json);
        context.startService(intent);
    }

    public TodoSaveService() {
        super("TodoSaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE.equals(action)) {
                final String json = intent.getStringExtra(EXTRA_TODO_JSON);
                handleActionSave(json);
            } else {
                Log.e(TAG, "unknown action name:" + action);
            }
        }
    }

    /**
     * Handle action Save in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSave(String json) {
        Log.v(TAG, "json:" + json);

        TodoEntity entity = TodoEntity.fromJson(json);
        TodoOpenHelper helper = new TodoOpenHelper(this);
        long id = helper.insertTodo(entity);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(new Intent(ACTION_COMPLETED_SAVE));
    }
}
