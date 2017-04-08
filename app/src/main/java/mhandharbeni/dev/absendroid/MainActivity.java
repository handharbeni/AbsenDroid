package mhandharbeni.dev.absendroid;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zookey.universalpreferences.UniversalPreferences;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import mhandharbeni.dev.absendroid.system.Login;
import mhandharbeni.dev.absendroid.system.Menu;
import mhandharbeni.dev.absendroid.system.fragment.UploadData;

public class MainActivity extends AppCompatActivity{
    EncryptedPreferences encryptedPreferences;
    String states;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(String.valueOf(R.string.key)).build();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS Service are Disabled");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            LocationTracker tracker = new LocationTracker(getApplicationContext()) {
                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff
                    encryptedPreferences.edit()
                            .putString("Accuracy", String.valueOf(location.getAccuracy()))
                            .putString("Longitude", String.valueOf(location.getLongitude()))
                            .putString("Latitude", String.valueOf(location.getLatitude()))
                            .putString("Altitude", String.valueOf(location.getAltitude()))
                            .apply();
                }

                @Override
                public void onTimeout() {

                }
            };
            tracker.startListening();
        }
        UniversalPreferences.initialize(this);
//        states = encryptedPreferences.getUtils().encryptStringValue("menu");
        states = encryptedPreferences.getString("STATE",encryptedPreferences.getUtils().encryptStringValue("login"));
        encryptedPreferences.edit()
                .putString("STATE", states)
                .apply();
        setContentView(R.layout.activity_main);
        checkState();
    }
    public void checkState(){
        Fragment fr = new Menu();
        String state = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString("STATE","login"));
        if(state.equalsIgnoreCase("login")){
            fr = new Login();
            changeFragment(fr);
        }else if(state.equalsIgnoreCase("menu")){
            fr = new Menu();
            changeFragment(fr);
        }else if(state.equalsIgnoreCase("upload")){
            fr = new UploadData();
            changeFragment(fr);
        }
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frameContainer, fragment);
        fm.commit();
    }
    private void utilsExample() {
        String encryptedApiKey = encryptedPreferences.getUtils().encryptStringValue("123123123");
        Log.d("MainActivity", "encryptedApiKey => " + encryptedApiKey);
        String decryptedApiKey = encryptedPreferences.getUtils().decryptStringValue(encryptedApiKey);
        Log.d("MainActivity", "decryptedApiKey => " + decryptedApiKey);
    }
}
