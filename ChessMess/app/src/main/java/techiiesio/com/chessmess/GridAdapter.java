package techiiesio.com.chessmess;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Richard on 4/12/17.
 */

public class GridAdapter extends BaseAdapter {

    Context context;
    String[] values;
    int[] images;
    int[] pieces;
    View view;
    LayoutInflater layoutInflater;
    ChessActivity chessActivity;

    public GridAdapter(Context context, int[] images, int[] pieces, ChessActivity chessActivity) {
        this.context = context;
        this.images = images;
        this.pieces = pieces;
        this.chessActivity = chessActivity;
    }

    @Override
    public int getCount() {
        return images.length;
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
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null) {
//            view = new View(context);
//            view = layoutInflater.inflate(R.layout.item, null);

        // get dimensions of width and height respective of screen resolution
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int squareWidth = display.widthPixels / 8;
        int squareHeight = display.heightPixels / 8;


        ImageView pieceImage = new ImageView(context);

        final ImageView squareImage = new ImageView(context);
        squareImage.setLayoutParams(new GridView.LayoutParams(squareWidth, squareWidth));
        squareImage.setBackgroundResource(images[position]);
        if(pieces[position] != -1)
            squareImage.setImageResource(pieces[position]);
        squareImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chessActivity.isHasOne()) {
                        chessActivity.setPosition2(position);
                        chessActivity.move();
                        chessActivity.setHasOne(false);
                    } else {
                        chessActivity.setPosition1(position);
                        chessActivity.setHasOne(true);
                        squareImage.setBackgroundResource(R.drawable.red_square);
                    }
                }
                return true;
            }
        });


//        }
        return squareImage;
    }
}
