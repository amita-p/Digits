package ml.digits;

/**
 * Created by Twins on 27/08/2015.
 */


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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Twins on 24/07/2015.
 */
public class Settings extends Activity {

    Context context = this; //needed to start a new intent;
    TextView settingsText;
    TextView soundText;
    Boolean soundEnabled;
	Switch soundSwitch;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        context = this;//activity is a subclass of context
        settingsText = (TextView) findViewById(R.id.settingsText);
        soundText = (TextView) findViewById(R.id.soundText);
        soundSwitch = (Switch)  findViewById(R.id.soundButton);

        Intent i = getIntent();

        //declaring fonts
        Typeface tf_light = Typeface.createFromAsset(getAssets(),
                "font_light.ttf");

         //does textstuff, copied from Kevin's code, sets, size, font, and color
        settingsText.setTextSize((int) (0.04 * getWidth()));
        settingsText.setTypeface(tf_light);
        settingsText.setTypeface(settingsText.getTypeface(), Typeface.BOLD);
        settingsText.setTextColor(Color.parseColor("#ff437863"));
		// SharedPreferences stores variables in memory
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
		editor = prefs.edit();
		soundEnabled = prefs.getBoolean("soundEnabled", true);
		if(soundEnabled) {
		soundSwitch.setChecked(true);
		}
		else {

		}
		soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// Sound is enabled
					soundEnabled = true;
				} else {
					// Sound is disabled
					soundEnabled = false;
				}
				editor.putBoolean("soundEnabled", soundEnabled); // value to store
				editor.commit(); // Store to android memory
			}
		});

        // Style settings text
        soundText.setTypeface(tf_light);
        soundText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.05 * getWidth());
    }

    /*
        public float getHeight()
        {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            return height;
        }
        */
    public float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        return screenWidth;
    }
    public boolean getSoundState()
    {
        return this.soundEnabled;
    }
}