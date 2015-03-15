package jp.co.mixi.training.android.todo.entity;

/**
 * TODOを表現するためのEntity
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public class TodoEntity {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString(){
        return title;
    }

    //TODO builderパターンにする
}
