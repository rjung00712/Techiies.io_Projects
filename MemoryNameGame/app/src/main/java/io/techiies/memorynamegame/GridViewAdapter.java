package io.techiies.memorynamegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

/**
 * Created by Richard on 5/1/17.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private final String[] classList;

    public GridViewAdapter(Context context, String[] classList) {
        this.context = context;
        this.classList = classList;
    }

    @Override
    public int getCount() {
        return classList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            gridView = layoutInflater.inflate(R.layout.class_grid_item, null);

            Button button = (Button) gridView.findViewById(R.id.classItem);
            button.setText(classList[position]);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }
}
