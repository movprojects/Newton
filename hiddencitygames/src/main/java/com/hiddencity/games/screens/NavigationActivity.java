package com.hiddencity.games.screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.hiddencity.games.map.HiddenGoogleMap;
import com.hiddencity.games.map.HiddenInfoAdapter;
import com.hiddencity.games.HiddenSharedPreferences;
import com.hiddencity.games.R;
import com.hiddencity.games.rest.PlaceReq;
import com.hiddencity.games.rest.PlaceResponse;
import com.hiddencity.games.rest.Places;
import com.hiddencity.newton.domain.BeaconEvent;
import com.hiddencity.newton.eddystone.EddystoneBeaconManager;
import com.hiddencity.newton.rx.ObservableBeacon;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;


public class NavigationActivity extends AppCompatActivity {

    private String TAG = "NavigationActivity";

    EddystoneBeaconManager eddystoneBeaconManager;
    HiddenSharedPreferences hiddenSharedPreferences;
    HiddenGoogleMap hiddenGoogleMap;
    Places places;

    public static void goThere(Context context){
        Intent intent = new Intent(context, NavigationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

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

        String playerId = hiddenSharedPreferences.getPlayerId();
        playerId = "fQ5ChP3csTU:APA91bErS9e5vpcH22hFhKTQsu-A7Zz9PEgWT_2kQwMjiebZOcTwhHD39THceSjlGowch4BNodmrGSpySpdvl-bfXkLWE5vpZRwarl5NpyCuFYNw0IsduPp3sV8FcqWVpOaslNBU_Hni";

        places.places(playerId, new Callback<List<PlaceReq>>() {

            @Override
            public void success(List<PlaceReq> places, Response response) {
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
            places.placeByBeacon(beaconEvent.getContentID().getBeaconName(),
                                 hiddenSharedPreferences.getPlayerId(), new Callback<PlaceResponse>() {
                @Override
                public void success(PlaceResponse placeResponse, Response response) {
                    WebViewActivity.goThere(NavigationActivity.this, "/content/" + placeResponse.getId());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(HiddenSharedPreferences.TAG, "Beacon " + beaconEvent.getContentID().getBeaconName() + " not in backend");
                }
            });
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
}
