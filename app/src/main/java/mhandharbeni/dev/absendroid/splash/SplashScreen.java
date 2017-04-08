package mhandharbeni.dev.absendroid.splash;

import android.Manifest;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;
import mhandharbeni.dev.absendroid.MainActivity;
import mhandharbeni.dev.absendroid.R;

/**
 * Created by root on 14/02/17.
 */

public class SplashScreen extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = new String[11];
        permissions[0] = Manifest.permission.CAMERA;
        permissions[1] = Manifest.permission.INTERNET;
        permissions[2] = Manifest.permission.WAKE_LOCK;
        permissions[3] = Manifest.permission.LOCATION_HARDWARE;
        permissions[4] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions[5] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[6] = Manifest.permission.READ_PHONE_STATE;
        permissions[7] = Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[8] = Manifest.permission.ACCESS_WIFI_STATE;
        permissions[9] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[10] = Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(
                this,
                permissions,
                5
        );

        View easySplashScreenView = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(4000)
                .withBackgroundResource(R.color.colorPrimary)
                .withFooterText("Copyright 2016")
                .withBeforeLogoText("AbsenDroid")
                .withLogo(R.drawable.ic_fingerprint)
                .create();

        setContentView(easySplashScreenView);
    }
}
