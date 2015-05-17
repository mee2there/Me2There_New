package com.innovation.me2there;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class SignupActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
          {

    private LoginButton loginBtn;
    private TextView txtUserName;
    private TextView txtEmail;
    private TextView txtLocation;

    MyExpandableListAdapter  listAdapter;
    ExpandableListView expListView;
    private GoogleApiClient mGoogleApiClient;
              private static final int REQUEST_RESOLVE_ERROR = 1001;
    private Location mLastLocation ;
    private Double latitute;
    private Double longitutde;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    ArrayList<ExpListGroupItem> expListGroupItems;
    HashMap<String, List<ExpListChildItem>>  expListChildItems;

    String get_id, get_name, get_gender, get_email, get_birthday, get_locale, get_location;


    private Button launchBtn;
    private TextView greeting;
    private UiLifecycleHelper uiHelper;
    private ProfilePictureView profilePictureView;
    private GraphUser currentUser;

    private  String LOG_TAG ="Signupactivity";
    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    public final static String FB_ID = "com.innovation.me2there.FBID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);


        openActiveSession(this, true, statusCallback, Arrays.asList(
                new String[]{"email", "user_location", "user_birthday",
                        "user_likes", "publish_actions"}), savedInstanceState);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpCategory);

        // preparing list data
        prepareListData();

        //listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        listAdapter = new MyExpandableListAdapter(this,expListGroupItems,expListChildItems);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expListGroupItems.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expListGroupItems.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        expListGroupItems.get(groupPosition)
                                + " : "
                                + expListChildItems.get(
                                expListGroupItems.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();


                return false;


            }
        });

        loginBtn = (LoginButton) findViewById(R.id.fbsignup_button);

        launchBtn = (Button) findViewById(R.id.btnEnroll);


        txtUserName = (TextView) findViewById(R.id.txtPersonName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtLocation = (TextView) findViewById(R.id.txtAddress);

        Log.d(LOG_TAG,"Location service started");






        //  loginBtn.setReadPermissions(Arrays.asList("email"));
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    currentUser = user;
                    //greeting.setText("You are currently logged in as " + user.getName());



                    txtUserName.setText(user.getName());
                    //  txtEmail.setText(user.getLocation().getName());

                    launchBtn.setVisibility(View.VISIBLE);


                } else {
                    //greeting.setText("You are not logged in.");
                    launchBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    private  Session openActiveSession(Activity activity, boolean allowLoginUI,
                                       Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity);

        openRequest.setPermissions(permissions);
        openRequest.setCallback(callback);

        Session session = Session.getActiveSession();
        Log.d(LOG_TAG, "" + session);
        Log.d(LOG_TAG," Open Active Session");
        if (session == null) {
            Log.d(LOG_TAG, "" + savedInstanceState);
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        Log.i("Main Activity","Trying to get the location info");

        if (! mGoogleApiClient.isConnected()) {

            Log.i("Main Activity","Google Api client is not connected $$$$$$$$$");
        }


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);



        if (mLastLocation != null) {
            latitute = mLastLocation.getLatitude();
            longitutde = mLastLocation.getLongitude();
        }


        Log.i("Main Activity", "lat:" + latitute.toString() + " long:" + longitutde.toString());

        String locationDetails = LocationProvider.getCityNameFromLocation(latitute,longitutde,this);

        txtLocation.setText(locationDetails);

        Log.i("Main Activity","Last Known location based on coordinates \n"+locationDetails);
    }




    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.i("MainActivity", "Location Connection Suspended ");
    }




    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        Log.i("Main Activity", "Location Connection Failed " + result.toString());
        Log.i("Main Activity", "Location Connection Failed Resolution " + result.getResolution());
        Log.i("Main Activity", "Location Connection Failed Error Code " + result.getErrorCode());
        Log.i("Main Activity", "Location Connection Failed Error Code " + result.hasResolution());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }

    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        Log.i("Main Activity", "On start - start");

        super.onStart();
        mGoogleApiClient.connect();

        Log.i("Main Activity", "On start - End");
    }



    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {

            if (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException) {
                //new AlertDialog.Builder(this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();
                Log.e(LOG_TAG, "Facebook session not opened.");

            } else {
                if (state.isOpened()) {
                    Log.d("MainActivity", "Facebook session opened.");

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        public void onCompleted(GraphUser user, Response response) {
                            if (response != null) {
                                // do something with <response> now
                                try {

                                    Log.d(LOG_TAG, "Trying to get FB info.");

                                    Log.d(LOG_TAG, user.getId());
                                    Log.d(LOG_TAG, user.getName());
                                    Log.d(LOG_TAG, (String) user.getProperty("gender"));
                                    Log.d(LOG_TAG, (String) user.getProperty("email"));
                                    Log.d(LOG_TAG, (String) user.getProperty("locale"));
                                    //Log.d(LOG_TAG, user.getBirthday());
                                    //Log.d(LOG_TAG, user.getLocation().toString());

                                Log.d(LOG_TAG, user.getInnerJSONObject().getJSONObject("location").getString("id"));

                                    get_id = user.getId();
                                    get_name = user.getName();
                                    get_gender = (String) user.getProperty("gender");
                                    get_email = (String) user.getProperty("email");
                                    get_birthday = user.getBirthday();
                                    get_locale = (String) user.getProperty("locale");
                                    get_location = user.getLocation().toString();

                                    Log.d(LOG_TAG, user.getId() + "; " +
                                            user.getName() + "; " +
                                            (String) user.getProperty("gender") + "; " +
                                            (String) user.getProperty("email") + "; " +
                                            user.getBirthday() + "; " +
                                            (String) user.getProperty("locale") + "; " +
                                            user.getLocation());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(LOG_TAG, "Exceception: "+ e.getMessage());
                                }

                            }
                        }
                    });
                } else if (state.isClosed()) {
                    Log.d("MainActivity", "Facebook session closed.");
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }


    public void btnSignupClick(View view) {

        MongoDB mongoDB = new MongoDB();
        Log.d(LOG_TAG,"User ID"+currentUser.getId());
        mongoDB.insertUser(currentUser.getId(), txtUserName.toString(), txtLocation.toString(), listAdapter.getSelectedChildren(),latitute,longitutde);


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from signup screen");
        intent.putExtra(FB_ID, currentUser.getName());
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
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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

    private void prepareListData() {


        expListGroupItems = new ArrayList<ExpListGroupItem>() ;
        expListChildItems = new HashMap<String, List<ExpListChildItem>>()  ;



        // Adding child data
        expListGroupItems.add(new ExpListGroupItem("Sports"));
        expListGroupItems.add(new ExpListGroupItem("Spiritual"));
        expListGroupItems.add(new ExpListGroupItem("Entertainment"));


        // Adding child data
        List<ExpListChildItem> Sports = new ArrayList<ExpListChildItem>();
        Sports.add(new ExpListChildItem("Cricket"));
        Sports.add(new ExpListChildItem("Football"));
        Sports.add(new ExpListChildItem("Kabadi"));
        Sports.add(new ExpListChildItem("Tennis"));
        Sports.add(new ExpListChildItem("Racket ball"));




        List<ExpListChildItem> spiritual = new ArrayList<ExpListChildItem>();
        spiritual.add(new ExpListChildItem("Prayer groups"));
        spiritual.add(new ExpListChildItem("Discussion"));




        List<ExpListChildItem> entertainment = new ArrayList<ExpListChildItem>();
        entertainment.add(new ExpListChildItem("Dance"));
        entertainment.add(new ExpListChildItem("Movie"));
        entertainment.add(new ExpListChildItem("picnic"));



        expListChildItems.put(expListGroupItems.get(0).Text, Sports); // Header, Child data
        expListChildItems.put(expListGroupItems.get(1).Text, spiritual);
        expListChildItems.put(expListGroupItems.get(2).Text, entertainment);
    }
}