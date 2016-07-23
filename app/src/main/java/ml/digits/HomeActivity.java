package ml.digits;

/**
 * Created by Twins on 27/08/2015.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;


/* NAME: Digits
 * COMPANY: Lazydevelopers Studios
 * AUTHORS: Amita, Naireen, Sabahat, Kevin
 * DATE: September 1, 2015
 * DESCRIPTION:
 *    This class contains all code that powers the logic and GUI of the Home Screen.
 * @author Lazydevelopers
 * @version 1.0
 */
// TODO: Clean code up; Add text animations; Add language support; Add Multiplayer (Google Play Leaderboard)

public class HomeActivity extends Activity {
    Context context; // Needed to start a new intent
    ImageButton instructions;
    ImageButton play;
    ImageButton multiplayer;
    ImageView pic;
    LinearLayout linearLayout;
    TextView best;
    MediaPlayer loop;
    Boolean playingLoop = false;
    float volume;
    int imageHeight;
    Space space1;
    Space space2;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
	Boolean soundEnabled;

    @Override
    protected void onResume() {
        super.onResume();



			soundEnabled = prefs.getBoolean("soundEnabled", true);

        if (soundEnabled) {
            try {
                if (playingLoop == false) {
                    // Play background music
                    loop = MediaPlayer.create(getApplicationContext(), R.raw.loop_home);
                    loop.setLooping(true);
                    loop.start();
                    fadeAudioIn(loop);
                    playingLoop = true;
                }
            } catch (IllegalStateException e) {

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundEnabled) {
            try {
                if (playingLoop == true) {
                    // Stop playing background music
                    fadeAudioOut(loop);
                    loop.stop();
                    loop.release();
                    loop = null;
                    playingLoop = false;
                }
            } catch (IllegalStateException e) {

            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        instructions = (ImageButton) findViewById(R.id.instructions);
        play = (ImageButton) findViewById(R.id.play);
        multiplayer = (ImageButton) findViewById(R.id.multiplayer);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        context = this;// Activity is a subclass of context
        RelativeLayout.LayoutParams buttonsLayoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        buttonsLayoutParams.setMargins(0, 0, 0, (int) (0.1 * getHeight()));
        buttonsLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(buttonsLayoutParams);

		try {
			soundEnabled = prefs.getBoolean("soundEnabled", true);
			Log.e("soundEnabled Value:", String.valueOf(soundEnabled));
		} catch (NullPointerException e) {
			soundEnabled = true;
		}

        // Play launch sound effect
        if (soundEnabled) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        MediaPlayer next = MediaPlayer.create(getApplicationContext(), R.raw.next_level);
                        next.start();
                        Thread.sleep(2000);
                        next.release();
                        next = null;
                    } catch (IllegalStateException e) {

                    } catch (InterruptedException e) {

                    }
                }
            }).start();
        }

        // Declaring fonts
        Typeface tf_light = Typeface.createFromAsset(getResources().getAssets(),
                "font_light.ttf");
        LinearLayout.LayoutParams instructionsParams = (LinearLayout.LayoutParams) instructions.getLayoutParams();
        instructionsParams.width = ((int) ((0.1389) * getHeight() * 0.5965202983));
        instructionsParams.height = ((int) ((0.1389) * getHeight() * 0.5965202983));
        LinearLayout.LayoutParams multiplayerParams = (LinearLayout.LayoutParams) multiplayer.getLayoutParams();
        multiplayerParams.width = ((int) ((0.1389) * getHeight() * 0.5965202983));
        multiplayerParams.height = ((int) ((0.1389) * getHeight() * 0.5965202983));
        LinearLayout.LayoutParams playParams = (LinearLayout.LayoutParams) play.getLayoutParams();
        playParams.width = ((int) ((0.1389) * getHeight() * 0.5965202983));
        playParams.height = ((int) ((0.1389) * getHeight() * 0.5965202983));
        space1 = (Space) (findViewById(R.id.space1));
        space2 = (Space) (findViewById(R.id.space2));
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) space1.getLayoutParams();
        params2.width = (int) (0.0416666667 * getWidth());
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) space2.getLayoutParams();
        params3.width = (int) (0.0416666667 * getWidth());

        // Find screen width
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Scale picture and text
        pic = (ImageView) findViewById(R.id.imageView3);
        scaleImage(pic, (int) (getHeight() / (2.5)));

        // Center the pic in code
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(pic.getLayoutParams());
        marginParams.setMargins(0, (int) (0.25 * getHeight()), 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //pic.setLayoutParams(marginParams);
        pic.setLayoutParams(layoutParams);


        // Create Settings/Share buttons
        final ImageButton shareButton = (ImageButton) findViewById(R.id.share);
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) shareButton.getLayoutParams();
        params4.width = (int) (0.07 * getHeight());
        params4.height = (int) (0.07 * getHeight());
        params4.setMargins(0, (int) (0.046875 * getHeight() - 0.0215447154 * getHeight()), (int) (0.08333333 * getWidth() - 0.0215447154 * getHeight()), 0);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        shareButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    shareButton.setImageResource(R.drawable.share_pressed);
                    if (soundEnabled) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(500);
                                        click.release();
                                        click = null;
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    shareButton.setImageResource(R.drawable.share);
                }
                return false;
            }
        });
        final ImageButton settingsButton = (ImageButton) findViewById(R.id.setting);
        RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) settingsButton.getLayoutParams();
        params5.width = (int) (0.07 * getHeight());
        params5.height = (int) (0.07 * getHeight());
        params5.setMargins((int) (0.08333333 * getWidth() - 0.0215447154 * getHeight()), (int) (0.046875 * getHeight() - 0.0215447154 * getHeight()), 0, 0);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //7687687
                settings();
            }
        });

        //code for settings button
       /* settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsButton.setImageResource(R.drawable.settings_pressed);
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                boolean isMulti = false;
                intent.putExtra("mul", isMulti);
                try {
                    // Play click sound effect
                    MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                    click.start();
                    Thread.sleep(500);
                    click.release();
                    //start settings intent
                    startActivity(intent);
                    onPause();
                } catch (IllegalStateException e) {

                } catch (InterruptedException e) {

                }
            }
        });*/

       settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    settingsButton.setImageResource(R.drawable.settings_pressed);
                    if (soundEnabled) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(500);
                                        click.release();
                                        click = null;
                                        //start settings intent
                                        // Intent intent = new Intent(getApplicationContext(), Settings.class);
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    settingsButton.setImageResource(R.drawable.settings);
                }
                return false;
            }
        });

        // Creating the "BEST" textview
        best = (TextView) findViewById(R.id.best);
        //This gets the shared preferences
        prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        //it will load the previous score
        long score = prefs.getLong("key", 0); //0 is the default value
        best.setText("BEST: " + score);
        best.setTypeface(tf_light);
        best.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (0.09 * imageHeight));
        // To center/hi
        RelativeLayout.LayoutParams bestLayoutParams = new RelativeLayout.LayoutParams(best.getLayoutParams());
        bestLayoutParams.setMargins(0, (int) (0.04 * getHeight()), 0, 0);
        bestLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        best.setLayoutParams(bestLayoutParams);

        // Create the three Instructions/Play/Multiplayer buttons
        instructions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    instructions.setImageResource(R.drawable.instructions_pressed);
                    if (soundEnabled) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(500);
                                        click.release();
                                        click = null;
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    instructions.setImageResource(R.drawable.instructions);
                }
                return false;
            }
        });
        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    play.setImageResource(R.drawable.play_pressed);
                    if (soundEnabled) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(500);
                                        click.release();
                                        click = null;
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    play.setImageResource(R.drawable.play);
                }
                return false;
            }
        });
        multiplayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    multiplayer.setImageResource(R.drawable.multiplayer_pressed);
                    if (soundEnabled) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        // Play click sound effect
                                        MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click);
                                        click.start();
                                        Thread.sleep(500);
                                        click.release();
                                        click = null;
                                    } catch (IllegalStateException e) {

                                    } catch (InterruptedException e) {

                                    }
                                }
                            }).start();
                        } catch (IllegalStateException e) {

                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    multiplayer.setImageResource(R.drawable.multiplayer);
                }
                return false;
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Instructions5.class);
                boolean isMulti =false;
                intent.putExtra("mul",isMulti);
                startActivity(intent);
            }
        });
        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Instructions1.class);
                startActivity(intent);
            }
        });
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MultiPlayerSignIn.class);
                boolean isMulti =true;
                intent.putExtra("mul",isMulti);
                startActivity(intent);
            }
        });
    }

    /*
PURPOSE: To scale an ImageView to fit within a bounding box specified in Dp
*/
    private void scaleImage(ImageView view, int boundBoxInDp) {
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
        imageHeight = params.height;

    }

    /*
PURPOSE: To get the width of the device's screen
 */
    public float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        return screenWidth;

    }

    /*
PURPOSE: To get the height of the device's screen
 */
    public float getHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        return height;
    }

    /*
    PURPOSE: To share user activity to social networks. Works using Android's built-in share functionality
     */
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

    public void settings()
    {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
        onPause();
    }


    /*
    PURPOSE: To fade MediaPlayer audio out
     */
    public void fadeAudioOut(MediaPlayer mediaPlayer)
    {
        while(volume > 0f) {
            volume -= 0.0005f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /*
   PURPOSE: To fade MediaPlayer audio in
    */
    public void fadeAudioIn(MediaPlayer mediaPlayer)
    {
        while(volume < 0.3f) {
            volume += 0.0005f;
            mediaPlayer.setVolume(volume, volume);
        }
    }
}

