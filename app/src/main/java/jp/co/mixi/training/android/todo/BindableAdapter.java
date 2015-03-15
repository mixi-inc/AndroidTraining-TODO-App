package jp.co.mixi.training.android.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * ViewHolderパターンをイイカンジに使うためのクラス
 *
 * @see <a href="http://rejasupotaro.github.io/2014/02/06/34.html">ListViewのデータのbindはこうする2014</a>
 * Created by Hideyuki.Kikuma on 2015/03/15.
 */
public abstract class BindableAdapter<T> extends ArrayAdapter<T> {
    private LayoutInflater inflater;

    public BindableAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public final View getView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newView result must not be null.");
            }
        }
        bindView(getItem(position), position, view);
        return view;
    }

    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    public abstract void bindView(T item, int position, View view);
}
