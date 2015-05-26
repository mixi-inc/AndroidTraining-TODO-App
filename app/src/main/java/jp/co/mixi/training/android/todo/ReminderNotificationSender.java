package jp.co.mixi.training.android.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class ReminderNotificationSender extends BroadcastReceiver {
    private static String CONTENT_TITLE_NAME = "content_title";
    private static String CONTENT_TEXT_NAME = "content_text";
    private static String SMALL_ICON_NAME = "small_icon";
    private static String TICKER_NAME = "ticker";
    private static String ID_NAME = "todo_id";

    public ReminderNotificationSender() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }

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
