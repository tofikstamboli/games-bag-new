package com.example.nativeadinrecyclerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameViewController extends Activity {

    private AdView mAdView;
    private Button back,winCoins;
    private InterstitialAd interstitial;
    private RewardedAd rewardedVideoAd;

    private Integer coinWIn = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);
        back = (Button)findViewById(R.id.back);
        winCoins = (Button)findViewById(R.id.winNow);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        winCoins.setEnabled(false);
        loadRewardedAds();
        // Initialize the Mobile Ads SDK
//        MobileAds.initialize(this, getString(R.string.admob_app_id));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdRequest adIRequest = new AdRequest.Builder().build();

        // Prepare the Interstitial Ad Activity
        interstitial = new InterstitialAd(GameViewController.this);

        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        // Interstitial Ad load Request
        interstitial.loadAd(adIRequest);

        mAdView.loadAd(adRequest);

        winCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),AdViews.class);
//                startActivity(intent);
                showRewardedAd();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayInterstitial();
            }
        });
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(getApplicationContext(),"onAdLoaded",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(),"onAdFailedToLoad",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(getApplicationContext(),"onAdOpened",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Toast.makeText(getApplicationContext(),"onAdLeftApplication",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(getApplicationContext(),"onAdClosed",Toast.LENGTH_LONG).show();
            }
        });


        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener()
        {
            public void onAdLoaded()
            {
                // Call displayInterstitial() function when the Ad loads

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
                startActivity(intent);
            }


        });
    }
    public void displayInterstitial()
    {
        // If Interstitial Ads are loaded then show else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }else{
            Intent i = new Intent(getApplicationContext(),LauncherActivity.class);
            startActivity(i);
        }
    }

    private void loadRewardedAds(){
        this.rewardedVideoAd = new RewardedAd(this,"ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                Log.d("ReawrdedAd","Rewarded Ad loaded");
                winCoins.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int i) {
                super.onRewardedAdFailedToLoad(i);
            }
        };
        this.rewardedVideoAd.loadAd(new AdRequest.Builder().build(),adLoadCallback);
    }

    private void showRewardedAd(){

        if (rewardedVideoAd.isLoaded()){
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    coinWIn = coinWIn + 5;
                    Toast.makeText(getApplicationContext(),"Congrats You have "+coinWIn+" now !",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                    Log.d("ReawrdedAd","Rewarded Ad open");
                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    Log.d("ReawrdedAd","Rewarded Ad close");
                    winCoins.setEnabled(false);
                    loadRewardedAds();
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    super.onRewardedAdFailedToShow(i);
                    Log.d("ReawrdedAd","Rewarded Ad fail to show");
                    Toast.makeText(getApplicationContext(),"Fail to show",Toast.LENGTH_LONG).show();
                    loadRewardedAds();
                }
            };

            rewardedVideoAd.show(this,rewardedAdCallback);
        }
    }
}
