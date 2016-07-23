package ml.digits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import blurEffect.BlurBehind;
import blurEffect.OnBlurCompleteListener;


public class MultiMainActivity extends Activity  {
    TextView textview; //used to create the 40 textviews
    LinearLayout linearlayout;
    Chronometer chronometer;
    ImageView circleImage;
    ImageView checkmarkImage;
    LinearLayout[] linearlayouts = new LinearLayout[10];
    TextView[] textviews = new TextView[40]; //40 text views for each of the 40 cells, array used to store them
    GridLayout layout; //gridlayout which is used
    GridLayout circlesGridLayout;
    Rect outRect = new Rect();
    int[] location = new int[2];
    int randomNum = getRandomNumber(); //get a random number
    //int randomNum=5239;
    int[] digits = new int[4]; //array which stores the digits of the answer
    int time = 0;
    Chronometer timer;
    float width;
    float height;
    RelativeLayout view;
    int currentRow = 1; //the row which the player is currently on (starts off with row 1)
    boolean won = false; //keeps track of whether the player has won
    boolean lost = false; //keeps track of whether player has lost
    Button submitButton; //button to submit answer
    Context context;
    ImageView roundedRect;
    // The following variables are for the app background
    int[] gradient1 = new int[]{android.graphics.Color.rgb(0, 36, 53), android.graphics.Color.rgb(0, 36, 53), android.graphics.Color.rgb(56, 115, 115)};
    int[] gradient2 = new int[]{android.graphics.Color.rgb(70, 24, 0), android.graphics.Color.rgb(70, 24, 0), android.graphics.Color.rgb(163, 83, 114)};
    int[] gradient3 = new int[]{android.graphics.Color.rgb(0, 70, 55), android.graphics.Color.rgb(0, 70, 55), android.graphics.Color.rgb(138, 163, 83)};
    int[] gradient4 = new int[]{android.graphics.Color.rgb(90, 75, 65), android.graphics.Color.rgb(90, 75, 65), android.graphics.Color.rgb(75, 0, 0)};
    int[] gradient5 = new int[]{android.graphics.Color.rgb(55, 68, 82), android.graphics.Color.rgb(55, 68, 82), android.graphics.Color.rgb(75, 0, 71)};
    int[][] gradients = new int[][]{gradient1, gradient2, gradient3, gradient4, gradient5};
    static int[] currentGradient;
    static String currentAccent;
    int num = 0;
    int previousNum = 0;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    MediaPlayer currentLoop;
    MediaPlayer loop1;
    MediaPlayer loop2;
    MediaPlayer loop3;
    Boolean playingLoop = false;
	Boolean soundEnabled;
    int loop = 0;
    int previousLoop = 0;
    float volume;


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUMED ", "RESUMED");
        try {
            if (playingLoop == false) {
                // Play background music
                // Loads background music loops
                loop1 = MediaPlayer.create(getApplicationContext(), R.raw.loop1);
                loop2 = MediaPlayer.create(getApplicationContext(), R.raw.loop2);
                loop3 = MediaPlayer.create(getApplicationContext(), R.raw.loop3);
                MediaPlayer[] loops = {loop1, loop2, loop3};
                // SharedPreferences stores variables in memory
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                loop = prefs.getInt("loop", 0);
                previousLoop = prefs.getInt("previousLoop", 0);
                // Pick a random loop track
                while (loop == previousLoop) {
                    loop = (int) (Math.floor(Math.random() * 3));
                    System.out.println("We accidentally chose the same loop twice in a row, so I'm switching it up right now");
                }
                previousLoop = loop;
                // Set loop properties
                currentLoop = loops[loop];
                currentLoop.setVolume(0.3f, 0.3f);
                currentLoop.setLooping(true);
                editor = prefs.edit();
                editor.putInt("loop", loop); // value to store
                editor.putInt("previousLoop", previousLoop); // value to store
                editor.commit(); // Store to android memory
                currentLoop.start();
                playingLoop = true;
            }
        } catch (IllegalStateException e) {

        }
        // Logs 'install' and 'app activate' App Events.
        //  AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("PAUSED ", "PAUSED");
        try {
            if (playingLoop == true) {
                // Stop playing background music
                currentLoop.stop();
                currentLoop.release();
                currentLoop = null;
                playingLoop = false;
            }
        } catch (IllegalStateException e) {

        }
    }
    //multipleyer listeners





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_multi);

        updateBG(); // Makes new BG gradient

		soundEnabled = prefs.getBoolean("soundEnabled", true);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y - getStatusBarHeight(); //we need to change this because height isn't always equal to the screen size-statuc bar height (some android phones have a bottom bar too)
        view = (RelativeLayout) findViewById(R.id.fullScreenLayout); //this is the relative layout
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        layout = (GridLayout) findViewById(R.id.grid); //set the layout to the grid layout
        circlesGridLayout=(GridLayout)findViewById(R.id.circlesgrid);
        roundedRect=(ImageView)findViewById(R.id.imageView2);
        roundedRect.setBackgroundResource(R.drawable.roundedrectangle);
        checkmarkImage=new ImageView(this);
        checkmarkImage.setImageDrawable(getResources().getDrawable(R.drawable.checkmark3x));


        //checkmarkImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins((int)(0.066666*width)+(int) ((0.094444 * width) / 2), (int)(0.030394415*height)+(int)((0.013961605*height)/2), 0, 0); //1280 20

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) chronometer.getLayoutParams();
        params2.setMargins((int) (0.05375218*height), (int)(0.032394415*height), 0, 0); //1280 1
        layout.setLayoutParams(params);
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) roundedRect.getLayoutParams();
        params3.setMargins((int)(0.066666*width),(int)(0.030394415*height),0,0);
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) circlesGridLayout.getLayoutParams();
        params4.setMargins((int)(0.0555555555*width),(int)(0.030394415*height)+(int)((0.013961605*height)/2),0,0); //MAKE CIRCLES SIZE RELATIVE TO WIDTH
        circlesGridLayout.setLayoutParams(params4);
        System.out.println("WIDTHWIDTHWIDTHWIDTH: "+width);
        roundedRect.setLayoutParams(params3);

        //layout.setBackgroundResource(R.drawable.image339);
        chronometer.setLayoutParams(params2);
        context = this;
        submitButton = (Button) findViewById(R.id.button);

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        int dig1 = Integer.parseInt(Character.toString(Integer.toString(randomNum).charAt(0)));
        int dig2 = Integer.parseInt(Character.toString(Integer.toString(randomNum).charAt(1)));
        int dig3 = Integer.parseInt(Character.toString(Integer.toString(randomNum).charAt(2)));
        int dig4 = Integer.parseInt(Character.toString(Integer.toString(randomNum).charAt(3)));
        for (int counter = 0; counter < 40; counter++) { //creating the 40 textvies
            textview = new TextView(this); //creating a new textview
            textview.setText("0"); //by default I set the text of each textview as 0
            if (counter<36) {
                textview.setBackgroundResource(R.drawable.textviewbg);
            }
            textview.setWidth((int) (width * 0.175)); //this is where I set the width of the cells
            textview.setHeight((int) (0.082024432 * height)); //1280 87, 2560 174
            System.out.print("Height: ");
            System.out.println(height);
            textview.setGravity(Gravity.CENTER);

            //by the way, to set the height of entire grid, you can go into the xml and change the height of the gridlayout
            //you can also set the height of each individual cell using textview.setHeight()
            textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.05 * height); //this is where I set the font size, set it to whatever you want
            textview.setTextColor(Color.WHITE);
            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "font.ttf");
            textview.setTypeface(tf);
            chronometer.setTypeface(tf);
            chronometer.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.04 * height);
            textviews[counter] = textview; //store the newly created textview in the array
            layout.addView(textview);//add the textviews to the layout, the textviews get added right and  downwards, to textviews[0] would be the one in the top left corner
        }
        for (int counter=0;counter<10;counter++)
        {
            linearlayout=new LinearLayout(context);
            linearlayout.setOrientation(LinearLayout.VERTICAL);
            android.view.ViewGroup.LayoutParams layoutParamss = roundedRect.getLayoutParams();
            layoutParamss.height = ((int) (0.082029432 * height));
            layoutParamss.width=((int) (0.08 * width));
            linearlayout.setLayoutParams(layoutParamss);
            linearlayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearlayouts[counter]=linearlayout;

            circlesGridLayout.addView(linearlayout);

        }
        linearlayouts[9].addView(checkmarkImage);
        LinearLayout.LayoutParams paramz = (LinearLayout.LayoutParams) checkmarkImage.getLayoutParams();
        paramz.width=((int) (0.035 * width));
        checkmarkImage.setLayoutParams(paramz);
        checkmarkImage.setAdjustViewBounds(true);
        android.view.ViewGroup.LayoutParams layoutParams = roundedRect.getLayoutParams();

        layoutParams.width = ((int) (width * 0.175))*4 +(int)(0.094444*width);
        layoutParams.height = ((int) (0.082024432 * height))*10 + (int)(0.013961605*height);
        roundedRect.setLayoutParams(layoutParams);
        // roundedRect.getLayoutParams().height=(layout.getHeight()+5);
        //roundedRect.setMaxHeight(layout.getHeight()+5);
        for (int counter = 0; counter < 36; counter++) {
            textviews[counter].setText(" ");
        }
        /*for (int counter=0;counter<10;counter++)
        {
            for (int counter2=0;counter2<4;counter2++)
            {
                circleImage=new ImageView(context);
                circleImage.setBackgroundResource(R.drawable.unfilledcircle);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (counter2!=0){
                    lp.setMargins(0,5,0,0);}
                circleImage.setLayoutParams(lp);
                linearlayouts[counter].addView(circleImage);
            }
        }*/
        //set the last 4 textviews (the ones on the bottom row) to have text that are blue
        //this indicates to the player which row they are on (they start off on the bottom row)

        //fill in the digits array with the digits of the answer
        digits[0] = dig1;
        digits[1] = dig2;
        digits[2] = dig3;
        digits[3] = dig4;
        System.out.println(randomNum);



//for testing, lets me know if its multiplayer or not

        clickSubmit(); //when the submit button is clicked
    }



    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public void clickSubmit() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make all digit textviews in the previous rows slightly transparent
                for (int counter = 36-(4*(currentRow-1)); counter < 40; counter++) {
                    textviews[counter].setTextColor(Color.argb(200, 255, 255, 255));
                }
                //the game goes on until the current row is smaller than or equal to 10 and if the player hasn't won or lost yet
                if (currentRow <= 10 && won == false && lost == false) {
                    //once the user clicks submit, the program will look through their answer
                    linearlayouts[10-currentRow].removeView(checkmarkImage);
                    if (currentRow<10){
                        linearlayouts[9-currentRow].addView(checkmarkImage);}
                    int numCorrect = 0; //stores how many digits are right but in the wrong position (empty circle)
                    int numExactlyCorrect = 0; //stores how many digits are in the right position (filled in circle)
                    int[] ansDigits = new int[4]; //array to store the digits of the answer
                    ArrayList<Integer> otherDigits = new ArrayList<Integer>();
                    ArrayList<Integer> otherDigitsAns = new ArrayList<Integer>();
                    //set the digits of the answer by taking the integers in the corresponding textviews
                    ansDigits[0] = Integer.parseInt(textviews[-4 * currentRow + 40].getText().toString());
                    ansDigits[1] = Integer.parseInt(textviews[-4 * currentRow + 41].getText().toString());
                    ansDigits[2] = Integer.parseInt(textviews[-4 * currentRow + 42].getText().toString());
                    ansDigits[3] = Integer.parseInt(textviews[-4 * currentRow + 43].getText().toString());

                    //go through the digits of the actual answer and given answer to see how many are correctly positioned
                    for (int counter = 0; counter < digits.length; counter++) {
                        if (digits[counter] == ansDigits[counter]) {
                            numExactlyCorrect++;

                        } else { //if it wasn't correctly positioned, add to the arraylists
                            otherDigits.add(digits[counter]);
                            otherDigitsAns.add(ansDigits[counter]);
                        }
                    }
                    for (int counter = 0; counter < 10; counter++) {
                        int num = 0;
                        int num2 = 0;
                        for (int counter2 = 0; counter2 < otherDigits.size(); counter2++) {
                            if (counter == otherDigits.get(counter2)) {
                                num++;
                            }
                            if (counter == otherDigitsAns.get(counter2)) {
                                num2++;
                            }

                        }

                        if (((num <= num2) && num != 0)){
                            numCorrect++;
                        }


                    }

                    if (numExactlyCorrect == 4) //if they got 4 numbers exactly, right, that means they guessed the right number
                    {
                        won = true; //set the won variable to true
                        // Sends blurredActivity info that player won, so play the won sound effect
                        editor = prefs.edit();
                        editor.putBoolean("won", true); // value to store
                        editor.commit();
                        System.out.println("YOU WON!!! =D");
                        chronometer.stop();
                        final long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        System.out.print(elapsedMillis + " sa");
                        //Starts a new activity
                        BlurBehind.getInstance().execute(MultiMainActivity.this, new OnBlurCompleteListener() {
                            @Override
                            public void onBlurComplete() {
                                Intent intent = new Intent(MultiMainActivity.this, MultiBlurredActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("val", 2);//sends the class info that the user won
                                intent.putExtra("time", elapsedMillis);
                                String ans=String.valueOf(digits[0])+String.valueOf(digits[1])+String.valueOf(digits[2])+String.valueOf(digits[3]);
                                intent.putExtra("answer", ans);
                                long scoreVal= 100000000/(long)(elapsedMillis)*(long)(currentRow);//this is the score, to be sent to the next activity
                                intent.putExtra("scoreV", scoreVal);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                                //10000/time*rowsused
                            }
                        });
                    }
                    System.out.print(numExactlyCorrect);
                    System.out.println(numCorrect);
                    int numCircles=numExactlyCorrect+numCorrect;

                    for (int counter2=0;counter2<numExactlyCorrect;counter2++)
                    {
                        circleImage=new ImageView(context);
                        circleImage.setBackgroundResource(R.drawable.filledcircle);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (counter2!=0){
                            lp.setMargins(0,(int)(0.00606504*height),0,0);}
                        circleImage.setLayoutParams(lp);
                        linearlayouts[10-(currentRow)].addView(circleImage);
                    }
                    for (int counter2=0;counter2<numCorrect;counter2++)
                    {
                        circleImage=new ImageView(context);
                        circleImage.setBackgroundResource(R.drawable.unfilledcircle);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (counter2!=0 || numExactlyCorrect>0){
                            lp.setMargins(0,(int)(0.00606504*height),0,0);}
                        circleImage.setLayoutParams(lp);
                        linearlayouts[10-(currentRow)].addView(circleImage);
                    }


                    currentRow++; //increment the current row by 1

                    //set the textviews in the current row to have blue font but first they need to become visible to the user
                    if (currentRow <= 10) {
                        textviews[-4 * currentRow + 40].setText("0");
                        textviews[-4 * currentRow + 41].setText("0");
                        textviews[-4 * currentRow + 42].setText("0");
                        textviews[-4 * currentRow + 43].setText("0");


                    }
                    //if the current row is greater than 10 and they still haven't won yet, they've lost
                    if (won == false && currentRow > 10) {
                        // Sends blurredActivity info that player lost, so play the lost sound effect
                        editor = prefs.edit();
                        editor.putBoolean("won", false); // value to store
                        editor.commit();
                        lost = true;
                        System.out.println("BOO YOU LOST");
                        chronometer.stop();
                        final long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        System.out.print(elapsedMillis + " sa");
                        //this starts the new activity
                        BlurBehind.getInstance().execute(MultiMainActivity.this, new OnBlurCompleteListener() {
                            @Override
                            public void onBlurComplete() {
                                Intent intent = new Intent(MultiMainActivity.this, MultiBlurredActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("val", 1);//sends the class info that the user lost
                                intent.putExtra("time", elapsedMillis);
                                String ans=String.valueOf(digits[0])+String.valueOf(digits[1])+String.valueOf(digits[2])+String.valueOf(digits[3]);
                                intent.putExtra("answer", ans);




                                long scoreVal= 0;
                                intent.putExtra("scoreV", scoreVal);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        });
                    }
                }

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            // Play submit sound effect
                            MediaPlayer submit = MediaPlayer.create(getApplicationContext(), R.raw.submit);
                            submit.start();
                            Thread.sleep(1000);
                            submit.release();
                            submit = null;
                        } catch (IllegalStateException e) {

                        } catch (InterruptedException e) {

                        }
                    }
                }).start();
            }
        });
    }

    //this method just returns of a coordinate(x,y) lies within a given view
    private boolean inViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && won == false && lost == false) {
            int x = (int) event.getRawX(); //get x and y coordinates of the touch
            int y = (int) event.getRawY();
            //the touch only applied for the textviews that are in the current row
            for (int counter = -4 * currentRow + 40; counter < -4 * currentRow + 44; counter++) {
                if (inViewInBounds(textviews[counter], x, y)) {
                    int num = Integer.parseInt(textviews[counter].getText().toString()); //take the number in the texview
                    if (num == 9) { //if the number of equal to 9 set it back to 0
                        textviews[counter].setText("0");
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(1000);
                                        click.release();
                                        click = null;
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    } else { //if it isn't equal to 9, increment it up by 1
                        try {
                            textviews[counter].setText(Integer.toString(num + 1));
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(1000);
                                        click.release();
                                        click = null;
                                        Log.i(":D","as");
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                }
            }
            if (inViewInBounds(linearlayouts[10-currentRow],x,y)){
                submitButton.performClick();
                System.out.println("HEYYYYYY");
            }
        }
        return true;
    }

    @Override //this method doesn't do anything
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //random number generator
    public static int getRandomNumber() {
        int num1 = (int) (Math.random() * 10);
        int num2 = (int) (Math.random() * 10);
        int num3 = (int) (Math.random() * 10);
        int num4 = (int) (Math.random() * 10);
        while ((num1 == num2 || num1 == num3 || num1 == num4 || num2 == num3 || num3 == num4 || num4 == num2) || num1 == 0) {
            num1 = (int) (Math.random() * 10);
            num2 = (int) (Math.random() * 10);
            num3 = (int) (Math.random() * 10);
            num4 = (int) (Math.random() * 10);
        }
        String num1Str = Integer.toString(num1);
        String num2Str = Integer.toString(num2);
        String num3Str = Integer.toString(num3);
        String num4Str = Integer.toString(num4);
        return Integer.parseInt(num1Str + num2Str + num3Str + num4Str);
    }

    @Override //this method doesn't do anything
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method picks a new BG gradient and draws it
    public void updateBG() {
        // SharedPreferences stores variables in memory
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        num = prefs.getInt("num", 0);
        previousNum = prefs.getInt("previousNum", 0);
        // Pick a random background gradient
        Random random = new Random();
        while (num == previousNum) {
            num = random.nextInt(5);
            System.out.println("We accidentally chose the same bg twice in a row, so I'm switching it up right now");
        }
        currentAccent = String.valueOf(gradients[num][2]); // Send to BlurredActivity.java
        currentGradient = gradients[num];
        // Draw gradient background
        View layout = findViewById(R.id.fullScreenLayout);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                currentGradient);
        layout.setBackgroundDrawable(gd);
        previousNum = num;
        editor = prefs.edit();
        editor.putInt("num", num); // value to store
        editor.putInt("previousNum", previousNum); // value to store
        editor.putInt("currentGradient1", currentGradient[0]); // value to store
        editor.putInt("currentGradient2", currentGradient[1]); // value to store
        editor.putInt("currentGradient3", currentGradient[2]); // value to store
        editor.commit(); // Store to android memory
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
