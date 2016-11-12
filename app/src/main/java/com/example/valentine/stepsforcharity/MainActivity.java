package com.example.valentine.stepsforcharity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    Double longtitude;
    Double latitude;
    String current_location;
    ImageView imageview2,imageView3;
    TextView t1,t2,t3,t4,t5,t6;
    EditText money;
    String registerurl="";

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//declarations of variable to be used;
        t1=(TextView)findViewById(R.id.textView3);//distance
        t2=(TextView)findViewById(R.id.textView5);//steps
        t3=(TextView)findViewById(R.id.textView7);//money
        t4=(TextView)findViewById(R.id.textView9);//current location


        imageview2=(ImageView)findViewById(R.id.imageView2);//start/stop
        imageView3=(ImageView)findViewById(R.id.imageView3);//donate

        money=(EditText)findViewById(R.id.editText);
imageview2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {

        ImageView imageView = (ImageView) view;
        assert(R.id.imageView2 == imageView.getId());
        Integer integer = (Integer) imageView.getTag();
        integer = integer == null ? 0 : integer;

        switch(integer) {
            case R.drawable.start:
                imageView3.setImageResource(R.drawable.stop);
                Toast.makeText(MainActivity.this, "Start pressed", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("longtitude", Double.toString(latitude));
                editor.putString("latitude", Double.toString(longtitude));
                editor.commit();


                break;
            case R.drawable.stop:
                imageView3.setImageResource(R.drawable.start);

                String la= sharedpreferences.getString("longtitude", "null");
                String lon=sharedpreferences.getString("latitude","null");
                Toast.makeText(MainActivity.this, "Stop pressed", Toast.LENGTH_SHORT).show();
              float dist=  getDistance(Double.parseDouble(la),Double.parseDouble(lon),latitude,longtitude);
                Toast.makeText(MainActivity.this, Float.toString(dist), Toast.LENGTH_SHORT).show();
                t1.setText(Float.toString(dist));
                t2.setText(Float.toString(dist/2));
                break;


        }

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(money.getText().toString().isEmpty())
                {
                    money.setError("Enter ammount!!!");
                }
                else
                {
                    sendmoney(money.getText().toString());
                }

            }
        });









    }
});





    }
   public void sendmoney(final String ammount)
    {

        class AddEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(MainActivity.this,null,"Please wait...",false,false);
            }

            @Override
            protected String doInBackground(Void... v)
            {
                HashMap<String,String> params = new HashMap<>();
                params.put("sname",ammount);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(registerurl, params);
                return res;

            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                loading.dismiss();

            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();




    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude= (location.getLatitude());
        longtitude= location.getLongitude();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longtitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                current_location = listAddresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
t4.setText(current_location);

        locationsetter( longtitude,  latitude);

        //distance
        String la= sharedpreferences.getString("longtitude", "null");
        String lon=sharedpreferences.getString("latitude","null");
        Toast.makeText(MainActivity.this, "Stop pressed", Toast.LENGTH_SHORT).show();
        float dist=  getDistance(Double.parseDouble(la),Double.parseDouble(lon),latitude,longtitude);
        Toast.makeText(MainActivity.this, Float.toString(dist), Toast.LENGTH_SHORT).show();
        t1.setText(Float.toString(dist));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    public float getDistance(double lat1, double lon1, double lat2, double lon2) {
        android.location.Location homeLocation = new android.location.Location("");
        homeLocation .setLatitude(lat1);
        homeLocation .setLongitude(lon1);

        android.location.Location targetLocation = new android.location.Location("");
        targetLocation .setLatitude(lat2);
        targetLocation .setLongitude(lon2);
        float distanceInMeters =  targetLocation.distanceTo(homeLocation);
        return distanceInMeters ;
    }

public  void locationsetter( Double longtitude, Double latitude)
{

}


}
