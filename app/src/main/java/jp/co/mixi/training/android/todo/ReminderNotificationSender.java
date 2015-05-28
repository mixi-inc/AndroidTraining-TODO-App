package jp.co.mixi.training.android.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

public class ReminderNotificationSender extends BroadcastReceiver {
    private static String CONTENT_TITLE_NAME = "content_title";
    private static String CONTENT_TEXT_NAME = "content_text";
    private static String SMALL_ICON_NAME = "small_icon";
    private static String TICKER_NAME = "ticker";
    private static String ID_NAME = "todo_id";

    public ReminderNotificationSender() {
    }

    @Override
    public void onReceive(Context context, Intent receivedIntent) {
        // 起動対象を設定
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setWhen(System.currentTimeMillis())
                .setContentTitle(receivedIntent.getStringExtra(CONTENT_TITLE_NAME))
                .setContentText(receivedIntent.getStringExtra(CONTENT_TEXT_NAME))
                // iconのデフォルトはとりあえずランチャーアイコン
                .setSmallIcon(receivedIntent.getIntExtra(SMALL_ICON_NAME, R.drawable.ic_launcher))
                .setTicker(receivedIntent.getStringExtra(TICKER_NAME))
                // この辺りの設定もbuilderに含めて設定できるようにしてもいいかもしれない
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(receivedIntent.getIntExtra(ID_NAME, 0),notification);
    }

    /**
     * 必要とするIntentの内容が複雑なのでbuilderでIntentを組み立てる
     */
    public static class IntentBuilder {
        private final Context context;

        private String title;
        private String text;
        private int smallIconId;
        private String ticker;
        private int id;

        public IntentBuilder(@NonNull Context context) {
            this.context = context;
        }

        public Intent build() {
            Intent intent = new Intent(context, ReminderNotificationSender.class);
            intent.putExtra(CONTENT_TITLE_NAME, title)
                    .putExtra(ID_NAME, id)
                    .putExtra(CONTENT_TEXT_NAME, text)
                    .putExtra(SMALL_ICON_NAME, smallIconId)
                    .putExtra(TICKER_NAME, ticker);
            return intent;
        }

        public IntentBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public IntentBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public IntentBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public IntentBuilder setSmallIconId(int smallIconId) {
            this.smallIconId = smallIconId;
            return this;
        }

        public IntentBuilder setTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }
    }
}
