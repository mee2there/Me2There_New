package com.innovation.me2there;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;

public class StartActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private Button launchBtn;
    private TextView greeting;
    private UiLifecycleHelper uiHelper;
    private ProfilePictureView profilePictureView;
    private GraphUser currentUser;

    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    public final static String FB_ID = "com.innovation.me2there.FBID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LaunchActivity.EXTRA_MESSAGE);


        greeting = (TextView) findViewById(R.id.greeting);
        loginBtn = (LoginButton) findViewById(R.id.login_button);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        launchBtn = (Button)findViewById(R.id.btnLaunch);

      //  loginBtn.setReadPermissions(Arrays.asList("email"));
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback()
        {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    currentUser = user;
                    greeting.setText("You are currently logged in as " + user.getName());
                    profilePictureView.setProfileId(user.getId());
                    launchBtn.setVisibility(View.VISIBLE);


                } else {
                    greeting.setText("You are not logged in.");
                    //todo get the fb working
                    //launchBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("MainActivity", "Facebook session opened.");
            } else if (state.isClosed()) {
                Log.d("MainActivity",state.toString());
                Log.d("MainActivity",exception.getMessage());
                Log.d("MainActivity", "Facebook session closed.");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }


    public void btnLaunchClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from Start screen");
        //intent.putExtra(FB_ID,currentUser.getName() );
        intent.putExtra(FB_ID,"Ashley George");
        startActivity(intent);
    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException) {
            new AlertDialog.Builder(this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();

        } else {

            Session sessionObj = Session.getActiveSession();

            if ((sessionObj != null && sessionObj.isOpened())) {
                // Kill login activity and go back to main
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fb_session", sessionObj);
                startActivity(intent);
            }
        }
    }
}