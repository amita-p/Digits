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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Twins on 24/07/2015.
 */
public class Instructions1 extends Activity {
    Button playAgain;//lets the user play again
    Context context;//needed to start a new intent;
    TextView guess;
    TextView numbers;
    ImageView pic;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions1);
        guess = (TextView) findViewById(R.id.guess);
        numbers = (TextView) findViewById(R.id.numbers);
        context=this;//activity is a subclass of context
        Intent i = getIntent();

//declaring fonts
        Typeface tf_light = Typeface.createFromAsset(getAssets(),
                "font_light.ttf");


        //find screen width
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //scale picture
        pic = (ImageView) findViewById(R.id.topClouds);
        scaleImage(pic,metrics.widthPixels);

        //does textstuff, copied from Kevin's code, sets, size, font, and color

        guess.setTextSize((int) (0.03 * getWidth()));
        guess.setTypeface(tf_light);
        guess.setTypeface(guess.getTypeface(),Typeface.BOLD);
        guess.setTextColor(Color.parseColor("#ff437863"));

        numbers.setTextSize((int) (0.02 * getWidth()));
        numbers.setTypeface(tf_light);
        numbers.setTextColor(Color.parseColor("#ff437863"));
        numbers.setTypeface(numbers.getTypeface(),Typeface.BOLD);
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
            Intent i= new Intent(context,Instructions2.class);
            startActivity(i);
            finish();
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
