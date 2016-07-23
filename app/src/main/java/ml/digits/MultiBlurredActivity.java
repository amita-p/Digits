package ml.digits;

/**
 * Created by Twins on 27/08/2015.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import blurEffect.BlurBehind;

public class MultiBlurredActivity extends Activity {
    LinearLayout circlesLinearLayout; // LinearLayout containing all 4 colored bubbles displaying the answer
    TextView result;// Displays if you won or lost & the time taken
    TextView score;// Displays your score (in really big text)
    TextView bestScore; // Displays your best score
    // The following TextViews display the correct answer (numbers that you had to guess)
    TextView answerTextView1;
    TextView answerTextView2;
    TextView answerTextView3;
    TextView answerTextView4;
    // The following TextViews display the colored circle below the answer (numbers that you had to guess)
    TextView circleTextView1;
    TextView circleTextView2;
    TextView circleTextView3;
    TextView circleTextView4;
    int imageHeight;
    Context context;//needed to start a new intent;
    SharedPreferences prefs;

    private int scaleImage(ImageView view, int boundBoxInDp) {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

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
        return params.height;

    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void onResume() { // Redraws the Share and Next Level buttons due to stupid bug that cuts off the rounded rectangle shape
        super.onResume();
        boolean won;
        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        won = prefs.getBoolean("won", false);
        if (won == false) {
            try {
                // Play lost sound effect
                MediaPlayer lose = MediaPlayer.create(getApplicationContext(), R.raw.lose);
                lose.start();
            } catch (IllegalStateException e) {

            }
        } else {
            try {
                // Play win sound effect
                MediaPlayer win = MediaPlayer.create(getApplicationContext(), R.raw.win);
                win.start();
            } catch (IllegalStateException e) {

            }
        }

        setContentView(R.layout.activity_blurred_multi);
        BlurBehind.getInstance()
                .withAlpha(250)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);


        // THE FOLLOWING CODE CALCULATES THE PLAYER'S SCORE

        Intent i = getIntent();
        long scoreData = i.getExtras().getLong("scoreV");


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ImageView pic = (ImageView) findViewById(R.id.imageView);
        imageHeight = scaleImage(pic, metrics.widthPixels);
        System.out.println(imageHeight / getHeight());


        // THE FOLLOWING CODE MAKES THE SHARE/SHOP/NEXT BUTTONS
        //the layout on which you are working
        LinearLayout layout = (LinearLayout) findViewById(R.id.buttons);
        RelativeLayout.LayoutParams params7 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params7.setMargins(0, 0, 0, (int) (0.15 * imageHeight));
        layout.setLayoutParams(params7);
        //set the properties for button
        final Button shareButton = new Button(this);
        //shareButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        shareButton.setText("Share");
        //shareButton.setId(R.id.share);
        shareButton.setGravity(Gravity.CENTER);
        shareButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (0.05 * imageHeight));
        shareButton.setTextColor(Color.rgb(255, 255, 255));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        params.setMargins((int) (0.01 * getWidth()), 0, (int) (0.01 * getWidth()), 0);
        shareButton.setLayoutParams(params);
        final GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(Integer.parseInt(MultiPlayerSignIn.currentAccent)); // Receive from MainActivity.java
        gdDefault.setCornerRadius(20);
        shareButton.setBackground(gdDefault);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "font.ttf");
        Typeface tf_light = Typeface.createFromAsset(getAssets(),
                "font_light.ttf");
        shareButton.setTypeface(tf);
        shareButton.setHeight((int) ((0.093155893) * imageHeight));
        shareButton.setPadding((int) (0.035185185 * getWidth()), 0, (int) (0.035185185 * getWidth()), 0);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  // Bring up social media panel, where they can share stuff
                Intent i = new Intent(context, SocialActivity.class);
                startActivity(i);
                finish();//ends current activity*/
                share();
            }
        });
        shareButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    GradientDrawable gdHover = new GradientDrawable();
                    gdHover.setColor(Color.rgb(255, 255, 255));
                    gdHover.setCornerRadius(20);
                    shareButton.setBackground(gdHover);
                    shareButton.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent)); // Receive from MainActivity.java
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    shareButton.setBackground(gdDefault);
                    shareButton.setTextColor(Color.rgb(255, 255, 255));
                }
                return false;
            }
        });
        layout.addView(shareButton);
        final Button homeButton = new Button(this);
        homeButton.setText(Html.fromHtml("&#8962;"));
        homeButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        homeButton.setId(R.id.loopGame);
        homeButton.setGravity(Gravity.CENTER);
        homeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (0.05 * imageHeight));
        homeButton.setTextColor(Color.rgb(255, 255, 255));
        homeButton.setLayoutParams(params);
        homeButton.setBackground(gdDefault);
        homeButton.setTypeface(tf);
        homeButton.setHeight((int) ((0.093155893) * imageHeight));
        homeButton.setPadding((int) (0.035185185 * getWidth()), 0, (int) (0.035185185 * getWidth()), 0);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starts a new activity
                Intent intent = new Intent(MultiBlurredActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        homeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    GradientDrawable gdHover = new GradientDrawable();
                    gdHover.setColor(Color.rgb(255, 255, 255));
                    gdHover.setCornerRadius(20);
                    homeButton.setBackground(gdHover);
                    homeButton.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent));  // Receive from MainActivity.java
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    homeButton.setBackground(gdDefault);
                    homeButton.setTextColor(Color.rgb(255, 255, 255));
                }
                return false;
            }
        });
        layout.addView(homeButton);
        final Button nextButton = new Button(this);
        nextButton.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        nextButton.setId(R.id.loopGame);
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (0.05 * imageHeight));
        nextButton.setTextColor(Color.rgb(255, 255, 255));
        nextButton.setLayoutParams(params);
        nextButton.setBackground(gdDefault);
        nextButton.setTypeface(tf);
        nextButton.setHeight((int) ((0.093155893) * imageHeight));
        nextButton.setPadding((int) (0.035185185 * getWidth()), 0, (int) (0.035185185 * getWidth()), 0);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the game goes on until the current row is smaller than or equal to 10 and if the player hasn't won or lost yet
                Intent i = new Intent(context, MultiPlayerSignIn.class);
                startActivity(i);
                try {
                    // Play click sound effect
                    MediaPlayer next = MediaPlayer.create(getApplicationContext(), R.raw.next_level);
                    next.start();
                } catch (IllegalStateException e) {

                }
                finish();//ends current activity
            }
        });
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    GradientDrawable gdHover = new GradientDrawable();
                    gdHover.setColor(Color.rgb(255, 255, 255));
                    gdHover.setCornerRadius(20);
                    nextButton.setBackground(gdHover);
                    nextButton.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent));  // Receive from MainActivity.java
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    nextButton.setBackground(gdDefault);
                    nextButton.setTextColor(Color.rgb(255, 255, 255));
                }
                return false;
            }
        });
        layout.addView(nextButton);

        circlesLinearLayout = (LinearLayout) findViewById(R.id.numberCircles);
        RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) circlesLinearLayout.getLayoutParams();
        params5.setMargins(0, (int) (0.188212927 * imageHeight), 0, 0);
        circlesLinearLayout.setLayoutParams(params5);

        circleTextView1 = (TextView) findViewById(R.id.c1);
        circleTextView2 = (TextView) findViewById(R.id.c2);
        circleTextView3 = (TextView) findViewById(R.id.c3);
        circleTextView4 = (TextView) findViewById(R.id.c4);
        circleTextView1.setMinimumHeight(140);
        circleTextView1.setMinimumWidth((int) (0.133079847 * imageHeight));
        circleTextView1.setMinimumHeight((int) (0.133079847 * imageHeight));
        circleTextView1.setLayoutParams(params);
        circleTextView2.setMinimumWidth((int) (0.133079847 * imageHeight));
        circleTextView2.setMinimumHeight((int) (0.133079847 * imageHeight));
        circleTextView2.setLayoutParams(params);
        circleTextView3.setMinimumWidth((int) (0.133079847 * imageHeight));
        circleTextView3.setMinimumHeight((int) (0.133079847 * imageHeight));
        circleTextView3.setLayoutParams(params);
        circleTextView4.setMinimumWidth((int) (0.133079847 * imageHeight));
        circleTextView4.setMinimumHeight((int) (0.133079847 * imageHeight));
        circleTextView4.setLayoutParams(params);
        GradientDrawable numberCircle = new GradientDrawable();
        numberCircle.setColor(Integer.parseInt(MultiPlayerSignIn.currentAccent));  // Receive from MainActivity.java
        numberCircle.setCornerRadius(((int) (0.14 * getWidth()) / 2));
        circleTextView1.setBackground(numberCircle);
        circleTextView2.setBackground(numberCircle);
        circleTextView3.setBackground(numberCircle);
        circleTextView4.setBackground(numberCircle);
//why
        result = (TextView) findViewById(R.id.result);
        RelativeLayout.LayoutParams params6 = (RelativeLayout.LayoutParams) result.getLayoutParams();
        params6.setMargins(0, (int) (0.04 * imageHeight), 0, 0);
        result.setLayoutParams(params6);
        result.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.05 * imageHeight));
        result.setTypeface(tf);
        result.setPadding(0, (int) (-0.189701897 * result.getTextSize()), 0, (int) (-0.135501355 * result.getTextSize()));
        result.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent)); // Receive from MainActivity.java
        result.forceLayout();
        score = (TextView) findViewById(R.id.score);
        score.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.28 * imageHeight));
        score.setText(String.valueOf(scoreData));
        score.setPadding(0, (int) (-0.23 * score.getTextSize()), 0, (int) (-0.26 * score.getTextSize()));
        score.setTypeface(tf_light);
        RelativeLayout.LayoutParams params8 = (RelativeLayout.LayoutParams) score.getLayoutParams();
        params8.setMargins(0, 0, 0, (int) (0.12 * imageHeight));
        score.setLayoutParams(params8);
        score.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent)); // Receive from MainActivity.java
        //the best score textView
        bestScore = (TextView) findViewById(R.id.bestScore);
        bestScore.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.05 * imageHeight));
        bestScore.setPadding(0, (int) (-0.23 * bestScore.getTextSize()), 0, (int) (-0.26 * bestScore.getTextSize()));
        bestScore.setTypeface(tf);
        bestScore.setText(String.valueOf(scoreData));
        RelativeLayout.LayoutParams params9 = (RelativeLayout.LayoutParams) bestScore.getLayoutParams();
        // params9.setMargins(0, (int)(1.150441064*imageHeight), 0, 0);//tablet
        //  params9.setMargins(0, (int)(1.250441064*imageHeight), 0, 0); //phone
        //params9.addRule(Layout.BELOW,nextBUTTON.getID());
        params9.setMargins(0, 0, 0, (int)(0.2971428571*imageHeight));
        // params9.setMargins(0, (int) (1.200441064 * imageHeight), 0, 0);
        System.out.println("Imageheight: " + imageHeight);
        bestScore.setLayoutParams(params9);
        bestScore.setTextColor(Integer.parseInt(MultiPlayerSignIn.currentAccent)); // Receive from MainActivity.java


        // This is for saving your highscore
        // This gets the shared preferences
        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        // It will load the previous score
        long score = prefs.getLong("key", 0); //0 is the default value

        if (score < scoreData) {
            //if the old score is less than the score they just got, then the new score is saved
            prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("key", scoreData);
            editor.commit();
            //This new saved score is retrieved and is shown to the user.
            prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            score = prefs.getLong("key", 0); //0 is the default value
        }
        bestScore.setText("Best Score: "+String.valueOf(score));

        answerTextView1 = (TextView) findViewById(R.id.c1);
        answerTextView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.037 * getHeight()));
        answerTextView1.setTypeface(tf);
        answerTextView1.setTextColor(Color.rgb(255, 255, 255));
        answerTextView2 = (TextView) findViewById(R.id.c2);
        answerTextView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.037 * getHeight()));
        answerTextView2.setTypeface(tf);
        answerTextView2.setTextColor(Color.rgb(255, 255, 255));
        answerTextView3 = (TextView) findViewById(R.id.c3);
        answerTextView3.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.037 * getHeight()));
        answerTextView3.setTypeface(tf);
        answerTextView3.setTextColor(Color.rgb(255, 255, 255));
        answerTextView4 = (TextView) findViewById(R.id.c4);
        answerTextView4.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.037 * getHeight()));
        answerTextView4.setTypeface(tf);
        answerTextView4.setTextColor(Color.rgb(255, 255, 255));

        context = this;//activity is a subclass of context

        int position = i.getExtras().getInt("val");
        long timeVal = i.getExtras().getLong("time");
        String answer = i.getExtras().getString("answer");
        char[] answers = answer.toCharArray();
        answerTextView1.setText(Character.toString(answers[0]));
        answerTextView2.setText(Character.toString(answers[1]));
        answerTextView3.setText(Character.toString(answers[2]));
        answerTextView4.setText(Character.toString(answers[3]));

        int time;
        int timeMinute;
        String timeString;
        time = (int) (timeVal / 1000);//you want it in seconds not milliseconds
        timeMinute = (int) Math.floor(time / 60);
        if (time > 60) {
            timeString = "Time: " + String.valueOf(timeMinute) + "m" + String.valueOf(time % 60) + "s";
        } else {
            timeString = "Time: " + String.valueOf(time) + "s";
        }
        if (position == 1) {
            result.setText("You Lost!" + " | " + timeString);
            nextButton.setText("Try Again");
        } else {
            result.setText("Got It!" + " | " + timeString);
            nextButton.setText("Next Level");
        }
    }

    public float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        return screenWidth;
    }

    public float getHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y - getStatusBarHeight();
        return screenHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void share() {
        // User's currently saved best score is retrieved and used in the shared message
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        long score = prefs.getLong("key", 0); // 0 is the default value
        // Share user's score when they click "share"
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Just got a #highscore of " + String.valueOf(score) + " on @DigitsGame. Loving this app www.digitsgame.ml";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check This Out");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via")); // Launch Android's built-in share chooser activity
    }
}
