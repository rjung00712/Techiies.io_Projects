package io.techiies.memorynamegame;

import android.content.Context;
import android.content.Intent;
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
    private final String mode;

    public GridViewAdapter(Context context, String[] classList, String mode) {
        this.context = context;
        this.classList = classList;
        this.mode = mode;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            gridView = layoutInflater.inflate(R.layout.class_grid_item, null);

            Button button = (Button) gridView.findViewById(R.id.classItem);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mode.equals("Easy"))
                    {
                        Intent intent = new Intent(context, EasyGameActivity.class);
                        intent.putExtra("Class Name", classList[position]);
                        context.startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(context, HardGameActivity.class);
                        intent.putExtra("Class Name", classList[position]);
                        context.startActivity(intent);
                    }
                }
            });
            button.setText(classList[position]);

        } else {
            gridView = convertView;
        }

        return gridView;
    }
}
