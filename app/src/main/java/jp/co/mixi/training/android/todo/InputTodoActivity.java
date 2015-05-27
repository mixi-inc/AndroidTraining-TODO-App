package jp.co.mixi.training.android.todo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import jp.co.mixi.training.android.todo.entity.TodoEntity;


public class InputTodoActivity extends ActionBarActivity {

    public static final String TODO = "todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_todo);
        View submitButton = findViewById(R.id.submit);
        final EditText todoText = (EditText) findViewById(R.id.input_todo);
        Intent intent = getIntent();
        final long entityId;
        long paramEntityId = 0;
        final CheckBox deadlineEnable = (CheckBox) findViewById(R.id.deadlineCheckBox);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.deadlineDatePicker);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.deadlineTimePicker);
        timePicker.setIs24HourView(true);
        Date deadlineDate = null;
        if (intent != null) {
            // 前の画面から渡ってきたパラメータをチェックする
            String todoJson = intent.getStringExtra(TODO);
            // todoのパラメータが存在していた場合は、初期表示時にその値を表示するようにする
            if (todoJson != null) {
                TodoEntity entity = TodoEntity.fromJson(todoJson);
                todoText.setText(entity.getTitle());
                paramEntityId = entity.getId();
                deadlineDate = entity.getDeadline();
            }
        }
        // deadlineがある場合だけ設定
        if (deadlineDate != null) {
            deadlineEnable.setChecked(true);
            Calendar cal = Calendar.getInstance();
            cal.setTime(deadlineDate);
            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }

        entityId = paramEntityId;
        submitButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                TodoEntity entity = new TodoEntity();
                                                entity.setId(entityId);
                                                entity.setTitle(todoText.getText().toString());
                                                if (deadlineEnable.isChecked()) {
                                                    Calendar cal = Calendar.getInstance();
                                                    cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                                                    entity.setDeadline(cal.getTime());
                                                }
                                                Activity activity = InputTodoActivity.this;
                                                TodoSaveService.startActionSave(activity, entity);
                                                activity.finish();
                                            }
                                        }
        );
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
}
