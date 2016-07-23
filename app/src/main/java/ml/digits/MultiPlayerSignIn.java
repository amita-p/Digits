package ml.digits;

/**
 * Created by Twins on 27/08/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import blurEffect.BlurBehind;
import blurEffect.OnBlurCompleteListener;

public class MultiPlayerSignIn extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener

{


      /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */

    final static String TAG = "ButtonClicker2000";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_LEADERBOARD = 100;


    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;


    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;

    //sabahat
    SharedPreferences prefs;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;
    TextView theyLeft;
    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[4];
    int numCorrect = 0;
    int numExactlyCorrect = 0;
    int numCorrectOpponent = 0;
    int numExactlyCorrectOpponent = 0;
    int currentRowOpponent = 1;
    TextView textview; //used to create the 40 textviews
    LinearLayout linearlayout;
    LinearLayout linearlayout2;
    Chronometer chronometer;
    ImageView circleImage;
    ImageView checkmarkImage;
    LinearLayout[] linearlayouts = new LinearLayout[10];
    LinearLayout[] linearlayouts2 = new LinearLayout[10];
    TextView[] textviews = new TextView[40]; //40 text views for each of the 40 cells, array used to store them
    GridLayout layout; //gridlayout which is used
    GridLayout circlesGridLayout;
    GridLayout circlesGridLayout2;
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
    SharedPreferences.Editor editor;
    MediaPlayer currentLoop;
    MediaPlayer loop1;
    MediaPlayer loop2;
    MediaPlayer loop3;
    Boolean playingLoop = false;
    int loop = 0;
    int previousLoop = 0;
    boolean gameStarted = false;
    //Button singlePlayer;
    Button quickGame;
    Button seeLeaderboard;
    Button signOut;
    Button home;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tester);
        //singlePlayer = (Button) findViewById(R.id.button_single_player_2);
        quickGame = (ButtonAwesome) findViewById(R.id.button_quick_game);
        seeLeaderboard = (ButtonAwesome) findViewById(R.id.button_view_leaderboard);
        signOut = (ButtonAwesome) findViewById(R.id.button_sign_out);
        home = (ButtonAwesome) findViewById(R.id.toHome);
        theyLeft=(TextView) findViewById(R.id.theyleft);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "font.ttf");
        MainActivity.currentAccent=null;


        //singlePlayer.setTypeface(face);
		// TODO fix this failed attempt to relativly size everything
   /*      quickGame.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.01 * height);
       seeLeaderboard.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.05 * height);
      signOut.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.1 * height);;
       home.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 0.4 * height);
		quickGame.setWidth((int) (0.05 * height));
		seeLeaderboard.setWidth((int) (0.05 * height));
		signOut.setWidth((int) (0.05 * height));
		home.setWidth((int) (0.05 * height));
		quickGame.setHeight((int) (0.05 * height));
		seeLeaderboard.setHeight((int) (0.05 * height));
		signOut.setHeight((int) (0.05 * height));
		home.setHeight((int) (0.05 * height));*/

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            //sabahat

            case R.id.toHome:
                backHome();
                break;
           /* case R.id.button_single_player_2:
                // play a single-player game
                resetGameVars();
                startGame(false);
                break;
                */
            case R.id.button_sign_in:
                // user wants to sign in
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_ID)) {
                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
                }

                // start the sign-in flow
                Log.d(TAG, "Sign-in button clicked");
                mSignInClicked = true;
                mGoogleApiClient.connect();
                break;
            case R.id.button_sign_out:
                // user wants to sign out
                // sign out.
                Log.d(TAG, "Sign-out button clicked");
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                switchToScreen(R.id.screen_sign_in);
                break;
            case R.id.button_invite_players:
                // show list of invitable players
                intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
                break;
            case R.id.button_see_invitations:
                // show list of pending invitations
                intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(intent, RC_INVITATION_INBOX);
                break;
            case R.id.button_accept_popup_invitation:
                // user wants to accept the invitation shown on the invitation popup
                // (the one we got through the OnInvitationReceivedListener).
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
                break;
            case R.id.button_quick_game:
                // user wants to play against a random opponent right now
                startQuickGame();
                break;
            case R.id.button_view_leaderboard:
                //call leaderboard sabahat

                prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                long score = prefs.getLong("key", 0); // 0 is the default value
                Log.d(TAG, "this is the score " + score);
                Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_ID), score);

                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                        getString(R.string.leaderboard_ID)), REQUEST_LEADERBOARD);
                break;

        }
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (responseCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    startGame(true);
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
                }
                break;
        }
        //super.onActivityResult(requestCode, responseCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        //original <code>
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }
//        //edits so only one player can be invited
//        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
//            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
//                    1,1, 0);
//            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria+"only tow players");
//        }
        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();
        // Log.d(TAG, "This is class id " + mRoomId);
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
        Log.d(TAG, "This is class id " + mRoomId);

//        Log.d(TAG, "This is class id " + mRoomId);

    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");

        // if we're in a room, leave it.
        leaveRoom();

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            switchToScreen(R.id.screen_sign_in);
        } else {
            switchToScreen(R.id.screen_wait);
        }
        super.onStop();
    }

    // Activity just got to the foreground. We switch to the wait screen because we will now
    // go through the sign-in flow (remember that, yes, every time the Activity comes back to the
    // foreground we go through the sign-in flow -- but if the user is already authenticated,
    // this flow simply succeeds and is imperceptible).
    @Override
    public void onStart() {
        switchToScreen(R.id.screen_wait);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.w(TAG,
                    "GameHelper: client was already connected on onStart()");
        } else {
            Log.d(TAG, "Connecting client.");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurScreen == R.id.screen_wait) {
            switchToScreen(R.id.screen_main);
        }
    }

    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_game) {
            leaveRoom();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            //mRoomId = null; sabahat
            switchToScreen(R.id.screen_wait);
        } else {
            switchToMainScreen();
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
            Toast toast = Toast.makeText(context, "Your opponent has left the game.", Toast.LENGTH_SHORT);
            //toast.show();
            //switchToScreen(R.id.screen_lose);
           // theyLeft.setText("They left");

        }
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

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in

        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(mCurScreen); // This will show the invitation popup
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        if (mIncomingInvitationId.equals(invitationId)) {
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen); // This will hide the invitation popup
        }
    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        switchToScreen(R.id.screen_sign_in);
    }

    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        //get participants and my ID:
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        switchToMainScreen();
       // switchToScreen(R.id.screen_lose);
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

    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        showGameError();
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
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
        Toast toast = Toast.makeText(context, "Your opponent has left the game.", Toast.LENGTH_SHORT);
        toast.show();
        //switchToScreen(R.id.screen_lose);
        //theyLeft.setText("They left");
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // save room ID so we can leave cleanly before the game starts.
        mRoomId = room.getRoomId();

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
    }


    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.
    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
            updatePeerScoresDisplay();
        }
    }

    /*
     * GAME LOGIC SECTION. Methods that implement the game's rules.
     */

    // Current state of the game:
    int mSecondsLeft = -1; // how long until the game ends (seconds)
    final static int GAME_DURATION = 20; // game duration, seconds.
    int mScore = 0; // user's current score

    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        gameStarted = false;
    }

    // Start the gameplay phase of the game.
    void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        //sabahat
      /*  if (!multiplayer) {
            Intent intent = new Intent(getApplicationContext(), Instructions5.class);
            startActivity(intent);
            //sabahat
        }*/

        System.out.println("GAME STARTED!!");
        randomNum = getRandomNumber();
        currentRow = 1;
        won = false;
        lost = false;
        num = 0;
        previousNum = 0;
        numCorrect = 0;
        numExactlyCorrect = 0;
        numCorrectOpponent = 0;
        numExactlyCorrectOpponent = 0;
        currentRowOpponent = 1;
        mMultiplayer = multiplayer;

        updateScoreDisplay();
        //broadcastScore(false);
        broadcastScore(false);
        switchToScreen(R.id.screen_game); //sabahat

        updateBG(); // Makes new BG gradient
        gameStarted = true;
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y - getStatusBarHeight(); //we need to change this because height isn't always equal to the screen size-statuc bar height (some android phones have a bottom bar too)
        view = (RelativeLayout) findViewById(R.id.fullScreenLayout); //this is the relative layout
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        layout = (GridLayout) findViewById(R.id.grid); //set the layout to the grid layout
        layout.removeAllViews();
        circlesGridLayout = (GridLayout) findViewById(R.id.circlesgrid);
        circlesGridLayout.removeAllViews();
        circlesGridLayout2 = (GridLayout) findViewById(R.id.circlesgrid2);
        circlesGridLayout2.removeAllViews();
        roundedRect = (ImageView) findViewById(R.id.imageView2);
        roundedRect.setBackgroundResource(R.drawable.roundedrectangle);
        checkmarkImage = new ImageView(this);
        checkmarkImage.setImageDrawable(getResources().getDrawable(R.drawable.checkmark3x));


        //checkmarkImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(0, (int) (0.030394415 * height) + (int) ((0.013961605 * height) / 2), 0, 0); //1280 20

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) chronometer.getLayoutParams();
        params2.setMargins((int) (0.05375218 * height), (int) (0.032394415 * height), 0, 0); //1280 1
        layout.setLayoutParams(params);
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) roundedRect.getLayoutParams();
        params3.setMargins(0, (int) (0.030394415 * height), 0, 0);
        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) circlesGridLayout.getLayoutParams();
        RelativeLayout.LayoutParams params7 = (RelativeLayout.LayoutParams) circlesGridLayout2.getLayoutParams();
        params4.setMargins((int) (0.04 * width), (int) (0.030394415 * height) + (int) ((0.013961605 * height) / 2), 0, 0); //MAKE CIRCLES SIZE RELATIVE TO WIDTH
        params7.setMargins((int) (0.04 * width), (int) (0.030394415 * height) + (int) ((0.013961605 * height) / 2), 0, 0);
        circlesGridLayout.setLayoutParams(params4);
        circlesGridLayout2.setLayoutParams(params7);
        System.out.println("WIDTHWIDTHWIDTHWIDTH: " + width);
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
            if (counter < 36) {
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
        for (int counter = 0; counter < 10; counter++) {
            linearlayout = new LinearLayout(context);
            linearlayout2 = new LinearLayout(context);
            linearlayout.setOrientation(LinearLayout.VERTICAL);
            linearlayout2.setOrientation(LinearLayout.VERTICAL);
            android.view.ViewGroup.LayoutParams layoutParamss = roundedRect.getLayoutParams();
            layoutParamss.height = ((int) (0.082029432 * height));
            layoutParamss.width = ((int) (0.05 * width));
            linearlayout.setLayoutParams(layoutParamss);
            linearlayout2.setLayoutParams(layoutParamss);
            linearlayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearlayout2.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearlayouts[counter] = linearlayout;
            linearlayouts2[counter] = linearlayout2;
            circlesGridLayout.addView(linearlayout);
            circlesGridLayout2.addView(linearlayout2);

        }
        linearlayouts[9].addView(checkmarkImage);
        LinearLayout.LayoutParams paramz = (LinearLayout.LayoutParams) checkmarkImage.getLayoutParams();
        paramz.width = ((int) (0.035 * width));
        checkmarkImage.setLayoutParams(paramz);
        checkmarkImage.setAdjustViewBounds(true);
        android.view.ViewGroup.LayoutParams layoutParams = roundedRect.getLayoutParams();

        layoutParams.width = ((int) (width * 0.175)) * 4 + (int) (0.094444 * width);
        layoutParams.height = ((int) (0.082024432 * height)) * 10 + (int) (0.013961605 * height);

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

        try {
            // Play background music
            currentLoop.start();
            playingLoop = true;
        } catch (IllegalStateException e) {

        }


        // run the gameTick() method every second to update the game.
        /*final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0)
                    return;
                gameTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);*/
    }


    /*
     * COMMUNICATIONS SECTION. Methods that implement the game's network
     * protocol.
     */

    // Score of other participants. We update this as we receive their scores
    // from the network.
    Map<String, Integer> mParticipantScore = new HashMap<String, Integer>();

    // Participants who sent us their final score.
    Set<String> mFinishedParticipants = new HashSet<String>();

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
        numExactlyCorrectOpponent=(int)buf[0];
        numCorrectOpponent=(int)buf[1];
        currentRowOpponent=(int)buf[2];



            updatePeerScoresDisplay();

            if (numExactlyCorrectOpponent==4) {
                won=false;
                editor = prefs.edit();
                editor.putBoolean("won", false); // value to store
                editor.commit();
                chronometer.stop();
                final long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                BlurBehind.getInstance().execute(MultiPlayerSignIn.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(MultiPlayerSignIn.this, BlurredActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("val", 1);//sends the class info that the user lost
                        intent.putExtra("time", elapsedMillis);
                        String ans = String.valueOf(digits[0]) + String.valueOf(digits[1]) + String.valueOf(digits[2]) + String.valueOf(digits[3]);
                        intent.putExtra("answer", ans);


                        long scoreVal = 0;
                        intent.putExtra("scoreV", scoreVal);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                });
                mFinishedParticipants.add(rtm.getSenderParticipantId());
               //
               //
               // switchToScreen(R.id.screen_lose); //jade
            }
            else if (numExactlyCorrectOpponent!=4 && currentRowOpponent>=10){
                won=true;
                lost=false;
                editor = prefs.edit();
                editor.putBoolean("won", true); // value to store
                editor.commit();
                chronometer.stop();
                final long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                System.out.print(elapsedMillis + " sa");
                //Starts a new activity/
               // switchToScreen(R.id.screen_lose);
                BlurBehind.getInstance().execute(MultiPlayerSignIn.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(MultiPlayerSignIn.this, BlurredActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("val", 2);//sends the class info that the user won
                        intent.putExtra("time", elapsedMillis);
                        String ans = String.valueOf(digits[0]) + String.valueOf(digits[1]) + String.valueOf(digits[2]) + String.valueOf(digits[3]);
                        intent.putExtra("answer", ans);
                       // long scoreVal = 100000000 / ((long) (elapsedMillis) * (long) (currentRow));//this is the score, to be sent to the next activity
                        long scoreVal =0;
                        intent.putExtra("scoreV", scoreVal);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        //10000/time*rowsused
                    }
                });
            }

    }

    // Broadcast my score to everybody else.
    void broadcastScore(boolean finalScore) {
        if (!mMultiplayer)
            return; // playing single-player mode

        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte) numExactlyCorrect;

        // Second byte is the score.
        mMsgBuf[1] = (byte) numCorrect;
        mMsgBuf[2]=(byte) currentRow;

        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;
            if (finalScore) {
                // final score notification must be sent via reliable message
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
                        mRoomId, p.getParticipantId());
            } else {
                // it's an interim score notification, so we can use unreliable
                Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, mMsgBuf, mRoomId,
                        p.getParticipantId());
            }
        }
    }

    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_accept_popup_invitation, R.id.button_invite_players,
            R.id.button_quick_game, R.id.button_see_invitations, R.id.button_sign_in,
            R.id.button_sign_out,  //R.id.button_single_player,
            //R.id.button_single_player_2,
            R.id.button_view_leaderboard, R.id.toHome
    };

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game, R.id.screen_main, R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else if (mMultiplayer) {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.screen_main);
        } else {
            // single-player: show on main screen and gameplay screen
            showInvPopup = (mCurScreen == R.id.screen_main || mCurScreen == R.id.screen_game);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //handler line of code is to use the handleMessage method
            handler.sendEmptyMessageDelayed(1, 1000);
            switchToScreen(R.id.screen_main);
        } else {
            handler.sendEmptyMessageDelayed(1, 1000);
            switchToScreen(R.id.screen_sign_in);
        }
    }

// method to delay starting new intents

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //Strat another Activity Here

                default:
                    break;
            }
            return false;
        }
    });

    // updates the label that shows my score
    void updateScoreDisplay() {
        //((TextView) findViewById(R.id.my_score)).setText(formatScore(mScore));
    }

    // formats a score as a three-digit number
   /* String formatScore(int i) {
        if (i < 0)
            i = 0;
        String s = String.valueOf(i);
        return s.length() == 1 ? "00" + s : s.length() == 2 ? "0" + s : s;
    }*/

    // updates the screen with the scores from our peers
    void updatePeerScoresDisplay() {


        if (mRoomId != null) {
            for (Participant p : mParticipants) {
                String pid = p.getParticipantId();
                if (pid.equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                int score = mParticipantScore.containsKey(pid) ? mParticipantScore.get(pid) : 0;

                for (int counter2=0;counter2<numExactlyCorrectOpponent;counter2++)
                {
                    circleImage=new ImageView(context);
                    circleImage.setBackgroundResource(R.drawable.filledcircle);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (counter2!=0){
                        lp.setMargins(0,(int)(0.00606504*height),0,0);}
                    circleImage.setLayoutParams(lp);
                    linearlayouts2[10-(currentRowOpponent)].addView(circleImage);
                }
                for (int counter2=0;counter2<numCorrectOpponent;counter2++)
                {
                    circleImage=new ImageView(context);
                    circleImage.setBackgroundResource(R.drawable.unfilledcircle);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (counter2!=0 || numExactlyCorrectOpponent>0){
                        lp.setMargins(0,(int)(0.00606504*height),0,0);}
                    circleImage.setLayoutParams(lp);
                    linearlayouts2[10-(currentRowOpponent)].addView(circleImage);
                }
            }
        }

    }

    /*
     * MISC SECTION. Miscellaneous methods.
     */


    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void clickSubmit() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the game goes on until the current row is smaller than or equal to 10 and if the player hasn't won or lost yet
                if (currentRow <= 10 && won == false && lost == false) {
                    //once the user clicks submit, the program will look through their answer
                    linearlayouts[10 - currentRow].removeView(checkmarkImage);
                    if (currentRow < 10) {
                        linearlayouts[9 - currentRow].addView(checkmarkImage);
                    }
                    numCorrect = 0; //stores how many digits are right but in the wrong position (empty circle)
                    numExactlyCorrect = 0; //stores how many digits are in the right position (filled in circle)
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

                        if (((num <= num2) && num != 0)) {
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
                        //Starts a new activity/
                        BlurBehind.getInstance().execute(MultiPlayerSignIn.this, new OnBlurCompleteListener() {
                            @Override
                            public void onBlurComplete() {
                                Intent intent = new Intent(MultiPlayerSignIn.this, BlurredActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("val", 2);//sends the class info that the user won
                                intent.putExtra("time", elapsedMillis);
                                String ans = String.valueOf(digits[0]) + String.valueOf(digits[1]) + String.valueOf(digits[2]) + String.valueOf(digits[3]);
                                intent.putExtra("answer", ans);
                                long scoreVal = 0;//this is the score, to be sent to the next activity
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
                    int numCircles = numExactlyCorrect + numCorrect;
                    broadcastScore(true);
                    for (int counter2 = 0; counter2 < numExactlyCorrect; counter2++) {
                        circleImage = new ImageView(context);
                        circleImage.setBackgroundResource(R.drawable.filledcircle);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (counter2 != 0) {
                            lp.setMargins(0, (int) (0.00606504 * height), 0, 0);
                        }
                        circleImage.setLayoutParams(lp);
                        linearlayouts[10 - (currentRow)].addView(circleImage);
                    }
                    for (int counter2 = 0; counter2 < numCorrect; counter2++) {
                        circleImage = new ImageView(context);
                        circleImage.setBackgroundResource(R.drawable.unfilledcircle);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (counter2 != 0 || numExactlyCorrect > 0) {
                            lp.setMargins(0, (int) (0.00606504 * height), 0, 0);
                        }
                        circleImage.setLayoutParams(lp);
                        linearlayouts[10 - (currentRow)].addView(circleImage);
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
                        BlurBehind.getInstance().execute(MultiPlayerSignIn.this, new OnBlurCompleteListener() {
                            @Override
                            public void onBlurComplete() {
                                Intent intent = new Intent(MultiPlayerSignIn.this, BlurredActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("val", 1);//sends the class info that the user lost
                                intent.putExtra("time", elapsedMillis);
                                String ans = String.valueOf(digits[0]) + String.valueOf(digits[1]) + String.valueOf(digits[2]) + String.valueOf(digits[3]);
                                intent.putExtra("answer", ans);


                                long scoreVal = 0;
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
        if (event.getAction() == MotionEvent.ACTION_UP && won == false && lost == false && gameStarted==true) {
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
        View layout = findViewById(R.id.screen_game);
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

    public void backHome()

    {
        Context here = this;
        Intent i = new Intent(here, HomeActivity.class);
        startActivity(i);
        finish();
    }

}







