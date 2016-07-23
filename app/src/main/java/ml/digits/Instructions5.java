package ml.digits;

/**
 * Created by Twins on 27/08/2015.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import blurEffect.BlurBehind;

public class Instructions5 extends Activity {
    Context context; //needed to start a new intent;
    TextView ready;
    TextView start;
    ImageView pic;
    boolean isMulti;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions5);
        ready = (TextView) findViewById(R.id.ready);
        start = (TextView) findViewById(R.id.start);
        context = this; // Activity is a subclass of context
        Intent i = getIntent();

        // Blur background
        BlurBehind.getInstance()
                .withAlpha(100)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);

        // Declare fonts
        Typeface tf_light = Typeface.createFromAsset(getAssets(),
                "font_light.ttf");

        //find screen width
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //scale picture
        pic = (ImageView) findViewById(R.id.topClouds);

        //to center the pic in code
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(pic.getLayoutParams());
        marginParams.setMargins((int)(0.15*getWidth()), (int)(0.45*getHeight()), (int)(0.05*getWidth()), (int)(0.05*getHeight()));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        scaleImage(pic,metrics.widthPixels);

        // Stylizes text
        ready.setTextSize((int) (0.06 * getWidth()));
        ready.setTypeface(tf_light);
        ready.setTypeface(ready.getTypeface(),Typeface.BOLD);
        ready.setTextColor(Color.parseColor("#ff437863"));
        // TODO Add some sort of animation here

        start.setTextSize((int) (0.02 * getWidth()));
        start.setTypeface(tf_light);
        start.setTextColor(Color.parseColor("#ff437863"));
        start.setTypeface( start.getTypeface(),Typeface.BOLD);
        //  onClick();//what happens if you click the button
    }


    private void scaleImage(ImageView view, int boundBoxInDp)
    {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP ) {

            System.out.println("HEYYYYYY");
            Intent i= new Intent(context,MainActivity.class);
            i.putExtra("mul",isMulti);
            startActivity(i);
        }
        return true;
    }

    public float getWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        return screenWidth;
    }

    public float getHeight()
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        return height;
    }



}

