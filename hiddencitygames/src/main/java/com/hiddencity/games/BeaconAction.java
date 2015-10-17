package com.hiddencity.games;

import android.content.Context;
import android.util.Log;

import com.hiddencity.games.adapters.BeaconEntityAdapter;
import com.hiddencity.games.adapters.PlaceEntityAdapter;
import com.hiddencity.games.db.table.BeaconEntity;
import com.hiddencity.games.db.table.PlacesEntity;
import com.hiddencity.newton.domain.BeaconEvent;

import rx.functions.Action1;

/**
 * Created by arturskowronski on 15/10/15.
 */
public class BeaconAction {
    public BeaconAction(Context context) {
        this.context = context;
    }

    Context context;

    public Action1<BeaconEvent> get(){
        return new Action1<BeaconEvent>() {
            @Override
            public void call(final BeaconEvent beaconEvent) {
                final BeaconEntityAdapter beaconEntityAdapter = new BeaconEntityAdapter(context);
                final PlaceEntityAdapter placeEntityAdapter = new PlaceEntityAdapter(context);

                BeaconEntity beaconEntity = beaconEntityAdapter.findByName(beaconEvent.getContentID().getBeaconName());

                if(beaconEntity == null) {
                    Log.e(HiddenSharedPreferences.TAG, "Beacon " + beaconEvent.getContentID().getBeaconName() + " not in backend");
                    return;
                }

                PlacesEntity placesEntity = placeEntityAdapter.findByName(beaconEntity.getPlaceId());

                if(!placesEntity.isActive()) {
                    Log.e(HiddenSharedPreferences.TAG, "Beacon " + beaconEvent.getContentID().getBeaconName() + " not active");
                    return;
                }

                String contentId = beaconEntity.getContent();

                if(contentId == null) {
                    Log.e(HiddenSharedPreferences.TAG, "Beacon Content" + beaconEvent.getContentID().getBeaconName() + " not in backend");
                    return;
                }
                Log.e(HiddenSharedPreferences.TAG, "Beacon Content GO!");

                HiddenNotification hiddenNotification = new HiddenNotification();
                hiddenNotification.sendBeaconNotification(context, contentId, new HiddenSharedPreferences(context).getPlayerId(), beaconEntity.getTitle());
            }
        };
    }
}