package jp.co.mixi.training.android.todo.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * TODOを表現するためのEntity
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoEntity {
    private static final Gson GSON = new GsonBuilder().create();
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
