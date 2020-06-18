package com.example.nativeadinrecyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.io.File;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameViewController extends Activity {

    private AdView mAdView;
    private Button back,winCoins;
    private InterstitialAd interstitial;
    private RewardedAd rewardedVideoAd;
    private WebView webView;
    String MyPrefrences = "MyPrefrences";
    private Integer coinWIn = 0;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);
        back = (Button)findViewById(R.id.back);
        winCoins = (Button)findViewById(R.id.winNow);
        webView = (WebView)findViewById(R.id.webView);
        mAdView = findViewById(R.id.adView);
        sharedpreferences = getSharedPreferences(MyPrefrences, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        AdRequest adRequest = new AdRequest.Builder().build();
        winCoins.setEnabled(false);

        loadRewardedAds();

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String path = getIntent().getStringExtra("fileUrl");
        File folder = new File(path);

        if (folder.exists()) {
            String fpath = folder.getPath();
            webView.loadUrl("file://" + path);
        }
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

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.

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
                finish();
            }


        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        displayInterstitial();
    }

    public void displayInterstitial()
    {
        // If Interstitial Ads are loaded then show else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }else{
            finish();
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
                    coinWIn = coinWIn + 50;
                    int coin = sharedpreferences.getInt("coinsNow",0);
                    editor.putInt("coinsNow", coin+coinWIn);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Congrats You have "+sharedpreferences.getInt("coinsNow",0)+" now !",Toast.LENGTH_LONG).show();

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

                    loadRewardedAds();
                }
            };

            rewardedVideoAd.show(this,rewardedAdCallback);
        }
    }
}
