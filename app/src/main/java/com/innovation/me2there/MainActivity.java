package com.innovation.me2there;


import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.SearchView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;
import android.location.Geocoder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

//import android.support.v7.app.ActionBar;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActionBar.TabListener {

    public static final int ACTIVITY_QUERY_LOADER = 0;

    public static final String QUERY_KEY = "ActivityQuery";
    private static DataStore mee2ThereDataStore;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation ;
    private Double latitute;
    private Double longitutde;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    public static float densityMultiplier;
    @Override
    protected void onStart() {
        Log.i("Main Activity", "On start - start");

        if (!isLocationEnabled(this)) {

            Log.e("Main Activity", "Location services are not enabled. Please enable");
        }
        else {

            Log.i("Main Activity", "Location services are  enabled.");
        }
        super.onStart();
        mGoogleApiClient.connect();

        Log.i("Main Activity", "On start - End");
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow


            // We need to create a bundle containing the query string to send along to the
            // LoaderManager, which will be handling querying the database and returning results.
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);

            HomeLoaderCallback loaderCallbacks = new HomeLoaderCallback(this);

            // Start the loader with the new query, and an object that will handle all callbacks.
            getLoaderManager().restartLoader(ACTIVITY_QUERY_LOADER, bundle, loaderCallbacks);
        }
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        buildGoogleApiClient();
		setContentView(R.layout.activity_main);

        densityMultiplier = getApplicationContext().getResources().getDisplayMetrics().density;
        Intent intent = getIntent();
        handleIntent(intent);
        String message = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(StartActivity.FB_ID);

        // Set up the action bar.
        //final ActionBar actionBar = getSupportActionBar();
        final ActionBar actionBar = getActionBar();

   //     actionBar.setDisplayShowTitleEnabled(false);
     //   actionBar.setHomeButtonEnabled(false);
      //  actionBar.setDisplayShowHomeEnabled(false);

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        // actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        TextView createButton = (TextView) findViewById(R.id.btnOrganize);
        createButton.setKeyListener(null);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = new LatLng(latitute,longitutde);
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                intent.putExtra("currentLocation",currentLocation);
                startActivity(intent);
            }
        });

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
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        String locationDetails = LocationProvider.getAddressFromLocation(latitute,longitutde,this);

        Log.i("Main Activity","Last Known location based on coordinates \n"+locationDetails);
        fillCards();
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
        Log.i("Main Activity","Location Connection Failed "+result.toString());
        Log.i("Main Activity","Location Connection Failed Resolution "+result.getResolution());
        Log.i("Main Activity","Location Connection Failed Error Code "+result.getErrorCode());
        Log.i("Main Activity","Location Connection Failed Error Code "+result.hasResolution());
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

    private void fillCards(){
        if(mee2ThereDataStore == null) {
            mee2ThereDataStore = new DataStore(latitute, longitutde);
        }
        //Need to retrieve the activity list and image name from DB
        List<EventDetailVO> events = DataStore.getActivities();

        ArrayList<Card> cards = new ArrayList<Card>();

        for(EventDetailVO anEvent:events){
            // Create a Card
            Card cardItem = new ActivityCard(this,R.layout.row_card,anEvent);
            cards.add(cardItem);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this, cards);

        CardListView listView = (CardListView) this.findViewById(R.id.activityList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
//            case R.id.action_login:
//                intent = new Intent(this,FirstActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_logout:
//                loggedInUser = null;
//                invalidateOptionsMenu();
//                return true;
//            case R.id.action_profile:
//                intent = new Intent(this,EditProfile.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_settings:
//                intent = new Intent(this,Settings.class);
//                startActivity(intent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
      //  inflater.inflate(R.menu.actions_bar, menu);
        inflater.inflate(R.menu.menu_main, menu);


//       MenuItem searchItem = menu.add(0, R.id.search_bar, 0, "searching");
  //      searchItem.setIcon(R.drawable.search);

     //    Need to use MenuItemCompat methods to call any action item related methods
//        MenuItemCompat.setShowAsAction(searchItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);


        // Associate searchable configuration with the SearchView
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        MenuItem loginItem = menu.findItem(R.id.action_login);
  //      MenuItem logoutItem = menu.findItem(R.id.action_logout);
    //    MenuItem profileItem = menu.findItem(R.id.action_profile);
/*
        if(loggedInUser != null && ! loggedInUser.isEmpty()){
            Log.i("Main Activity","Login item"+loginItem.toString()+"onPrepareOptionsMenu : "+loggedInUser);
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            profileItem.setVisible(true);
        }else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
        }
*/
        return true;

    }
}
