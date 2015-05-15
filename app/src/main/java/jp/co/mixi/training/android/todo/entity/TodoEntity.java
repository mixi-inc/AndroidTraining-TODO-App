package jp.co.mixi.training.android.todo.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * TODOを表現するためのEntity
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoEntity {
    private static final Gson GSON = new GsonBuilder().create();
    private long id;
    private String title;
    private Date deadline;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public Date getDeadline() {
        return deadline;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeadline(Date date){
        this.deadline = date;
    }
    public void setDeadline(Long longDate){
        Date date = null;
        // nullだった場合はnullのままにしておく
        if (longDate != null) {
            date = new Date(longDate);
        }
        setDeadline(date);
    }
    public String toString() {
        return title;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static TodoEntity fromJson(String json) {
        return GSON.fromJson(json, TodoEntity.class);
    }
    //TODO builderパターンにする
}
