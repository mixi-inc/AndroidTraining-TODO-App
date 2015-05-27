package jp.co.mixi.training.android.todo;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleActionSave(String json) {
        Log.v(TAG, "json:" + json);

        TodoEntity entity = TodoEntity.fromJson(json);
        TodoOpenHelper helper = new TodoOpenHelper(this);
        long id = entity.getId();
        if (helper.isExist(entity)) {
            helper.updateTodo(entity);
        } else {
            id = helper.insertTodo(entity);
        }

        // 本来ならすでに設定されているが、締め切りを消した場合はAlarmManagerから消す処理とかも入れた方がいい
        if (entity.getDeadline() != null) {
            Intent intent = new ReminderNotificationSender.IntentBuilder(this)
                    .setTitle(entity.getTitle())
                            // intのmax値は2147483647なので、そんな数のTODOをこなすのは人類には不可能なのでダウンキャストしても大丈夫と信じる　
                    .setId((int) id)
                            // todoに種別を持たせてそれごとにアイコン設定とかあってもいいかもしれないけど、今回は用意してないので固定
                    .setSmallIconId(R.drawable.ic_launcher)
                    .setTicker(entity.getTitle())
                            // 詳細とかあれば、ここで設定してあげてもいい気がする
                    .setText(entity.getTitle())
                    .build();
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // kitkat以前ならam.setでOK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 締め切りを過ぎたら通知が出る
                // 多分本来はリマインダーとして事前になるように設定すべき
                am.setExact(AlarmManager.RTC_WAKEUP, entity.getDeadline().getTime(), pendingIntent);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, entity.getDeadline().getTime(), pendingIntent);
            }

        }

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(new Intent(ACTION_COMPLETED_SAVE));
    }
}
