package com.innovation.me2there;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;



public class LaunchActivity extends FragmentActivity {

    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_launch, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    public void launchSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from launch screen");
        startActivity(intent);
    }


    public void launchLogin(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from launch screen");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
