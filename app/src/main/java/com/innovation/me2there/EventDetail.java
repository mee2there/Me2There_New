package com.innovation.me2there;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class EventDetail extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_event_detail);

        Bundle inBundle = getIntent().getExtras();

        Fragment detailFragment = new PlaceholderFragment();

        Bundle newBundle = new Bundle();
        //newBundle.putLong("selectedID",inBundle.getLong("selectedID"));
        newBundle.putParcelable("event_detail", inBundle.getParcelable("event_detail"));
        detailFragment.setArguments(newBundle);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, detailFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            //Long index = args.getLong("selectedID");
            EventDetailVO event = args.getParcelable("event_detail");

            View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
            TextView nameTextView = ((TextView) rootView.findViewById(R.id.event_name));
            nameTextView.setText(event.getEventName());

            TextView descTextView = ((TextView) rootView.findViewById(R.id.event_desc));
            descTextView.setText(event.getEventDesc());

            TextView dateTextView = ((TextView) rootView.findViewById(R.id.event_time));
            dateTextView.setText(event.getEventTime().toString());
            ImageView activityImage = (ImageView) rootView.findViewById(R.id.activity_image);
            activityImage.setImageBitmap(event.getEventImage());
            return rootView;
        }
    }
}
