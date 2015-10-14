package com.hiddencity.games.screens.interceptors;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.hiddencity.games.HiddenSharedPreferences;
import com.hiddencity.games.R;
import com.hiddencity.games.adapters.PlaceEntityAdapter;
import com.hiddencity.games.rest.AnswerResponse;
import com.hiddencity.games.rest.calls.AnswerCall;
import com.hiddencity.games.screens.MainMenuActivity;
import com.hiddencity.games.screens.NavigationActivity;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

/**
 * Created by arturskowronski on 20/09/15.
 */
public class ResultInterceptor {

    private String TAG = "ResultInterceptor";

    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    public ResultInterceptor(Context c) {

        mContext = c;
    }

    @JavascriptInterface
    public void backToGame() {
        logout();
    }

    public void logout(){
        new HiddenSharedPreferences(mContext).clearAllProperties();
        final PlaceEntityAdapter placeEntityAdapter = new PlaceEntityAdapter(mContext);
        placeEntityAdapter.clearDB();
        Intent intent = new Intent(mContext, MainMenuActivity.class);
        mContext.startActivity(intent);
        Toast.makeText(mContext, "Dziękujemy za rozgrywkę.", Toast.LENGTH_LONG).show();

    }
}
