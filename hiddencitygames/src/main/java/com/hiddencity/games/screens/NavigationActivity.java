package com.hiddencity.games.screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.hiddencity.games.map.HiddenGoogleMap;
import com.hiddencity.games.map.HiddenInfoAdapter;
import com.hiddencity.games.HiddenSharedPreferences;
import com.hiddencity.games.R;
import com.hiddencity.games.rest.BeaconizedMarker;
import com.hiddencity.games.rest.Places;
import com.hiddencity.games.rest.uri.ContentURL;
import com.hiddencity.newton.domain.BeaconEvent;
import com.hiddencity.newton.domain.ContentID;
import com.hiddencity.newton.eddystone.EddystoneBeaconManager;
import com.hiddencity.newton.rx.ObservableBeacon;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;


public class NavigationActivity extends AppCompatActivity {

    private String TAG = "NavigationActivity";

    @OnClick(R.id.simulate_beacon_1)
    public void sim1(View v){
        ContentID contentID = new ContentID();
        contentID.setBeaconName("Beacon");
        BeaconEvent beaconEvent = new BeaconEvent(contentID);
        onNext.call(beaconEvent);
    }

    @OnClick(R.id.simulate_beacon_2)
    public void sim2(View v){
        ContentID contentID = new ContentID();
        contentID.setBeaconName("Beacon");
        BeaconEvent beaconEvent = new BeaconEvent(contentID);
        onNext.call(beaconEvent);
    }

    @OnClick(R.id.simulate_beacon_3)
    public void sim3(View v){
        places.places(new Callback<List<BeaconizedMarker>>() {

            @Override
            public void success(List<BeaconizedMarker> places, Response response) {
                hiddenGoogleMap.addMarkers(places);
                ContentID contentID = new ContentID();
                contentID.setBeaconName("Beacon");
                BeaconEvent beaconEvent = new BeaconEvent(contentID);
                onNext.call(beaconEvent);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getResponse().getReason());
            }
        });
    }

    EddystoneBeaconManager eddystoneBeaconManager;
    HiddenSharedPreferences hiddenSharedPreferences;
    HiddenGoogleMap hiddenGoogleMap;
    Places places;

    public static void goThere(Context context){
        Intent intent = new Intent(context, NavigationActivity.class);
        context.startActivity(intent);
    }

    public static void goThere(Context context, boolean resync){
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.putExtra("resync", resync);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);


        String backendEndpoint = getResources().getString(R.string.backend_endpoint);

        places = new RestAdapter.Builder()
                .setEndpoint(backendEndpoint)
                .setLog(new AndroidLog(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(Places.class);

        TranslucantStatusBar();
        GoogleMap();

        hiddenSharedPreferences = new HiddenSharedPreferences(NavigationActivity.this);
        eddystoneBeaconManager = new EddystoneBeaconManager(this);

        beaconNavigation();

        places.places(new Callback<List<BeaconizedMarker>>() {

            @Override
            public void success(List<BeaconizedMarker> places, Response response) {
                hiddenGoogleMap.addMarkers(places);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getResponse().getReason());
            }
        });
    }

    private void beaconNavigation() {

        eddystoneBeaconManager.startMonitoring(new ObservableBeacon() {
            @Override
            public void onBeaconInitialized(Observable<BeaconEvent> observable) {
                observable.subscribe(onNext);
            }
        });
    }


    Action1<BeaconEvent> onNext = new Action1<BeaconEvent>() {
        @Override
        public void call(final BeaconEvent beaconEvent) {
            String contentId = hiddenGoogleMap.contentIdByBeaconId(beaconEvent.getContentID().getBeaconName());

            if(contentId == null){
                Log.e(HiddenSharedPreferences.TAG, "Beacon " + beaconEvent.getContentID().getBeaconName() + " not in backend");
            } else {
                WebViewActivity.goThere(NavigationActivity.this, new ContentURL(contentId).getUrl());
            }
        }
    };


    private void GoogleMap() {
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        hiddenGoogleMap = new HiddenGoogleMap(this, map);
        hiddenGoogleMap.setInfoWindowAdapter(new HiddenInfoAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void TranslucantStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); w.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#10000000"));
    }


    @Override
    public void onBackPressed() {
    }
}