package mhandharbeni.dev.absendroid.system;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mhandharbeni.dev.absendroid.R;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

/**
 * Created by root on 14/02/17.
 */

public class Login extends Fragment implements ConnectivityChangeListener {
    Button btnLogin;
    EncryptedPreferences encryptedPreferences;
    String states;
    View v;
    HttPizza client;
    String urlLogin;

    private FluentSnackbar mFluentSnackbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle("ABSEN DROID");
        if(savedInstanceState != null){
            ConnectionBuddy.getInstance().clearNetworkCache(getActivity(), savedInstanceState);
        }
        ConnectionBuddyConfiguration networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(getActivity()).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
        urlLogin = getActivity().getString(R.string.server)+"/server-absendroid/login.php";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        v = inflater.inflate(R.layout.login, container, false);
        mFluentSnackbar = FluentSnackbar.create(getActivity());
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(String.valueOf(R.string.key)).build();
        states = encryptedPreferences.getUtils().encryptStringValue("menu");
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        return v;
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frameContainer, fragment);
        fm.commit();
    }
    public void doLogin(){
        btnLogin.setText("AUTHENTICATING...");
        btnLogin.setEnabled(Boolean.FALSE);
//        Log.d("PREFERENCES NETWORK", encryptedPreferences.getString("NETWORK", "0"));
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            MaterialEditText mEmail = (MaterialEditText) v.findViewById(R.id.txtUsername);
            String sEmail = mEmail.getText().toString();
            RequestBody formBody = new FormBody.Builder()
//                    .add("email", sEmail)
                    .add("imei", getIMEI(getActivity()))
                    .build();

            Request request = client.newRequest()
                    .url(urlLogin)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        String responses = response.body().string();
                        Log.d("ERROR JSON", responses);
                        JSONObject jsonObj = new JSONObject(responses);
                        String status = jsonObj.getString("status");
                        if(status.equalsIgnoreCase("2")){
                            btnLogin.setText("VERIFIKASI DEVICE");
                            btnLogin.setEnabled(Boolean.TRUE);
                            String nama = jsonObj.getString("nama");
                            String email = jsonObj.getString("email");
                            String key = jsonObj.getString("key");
                            String id = jsonObj.getString("id");
                            encryptedPreferences.edit()
                                    .putString("STATE", states)
                                    .putString("ID", id)
                                    .putString("NAMA", nama)
                                    .putString("EMAIL", email)
                                    .putString("KEY", key)
                                    .apply();
                            Fragment fr = new Menu();
                            changeFragment(fr);
                        }else{
                            showSnackBar("VERIFIKASI GAGAL!");
                            btnLogin.setText("VERIFIKASI DEVICE");
                            btnLogin.setEnabled(Boolean.TRUE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSnackBar("JSON UNKNOWN");
                        btnLogin.setText("VERIFIKASI DEVICE");
                        btnLogin.setEnabled(Boolean.TRUE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar("DEVICE NOT READY");
                        btnLogin.setText("VERIFIKASI DEVICE");
                        btnLogin.setEnabled(Boolean.TRUE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Throwabel", t.getMessage());
                    showSnackBar("VERIFIKASI GAGAL!");
                    btnLogin.setText("VERIFIKASI DEVICE");
                    btnLogin.setEnabled(Boolean.TRUE);
                }
            });

        }else{
            showSnackBar("NO CONNECTION, PLEASE USE DATA OR WIFI");
        }

    }
    public void showSnackBar(String message){
        mFluentSnackbar.create(message)
                .maxLines(2) // default is 1 line
                .backgroundColorRes(R.color.colorPrimary) // default is #323232
                .textColorRes(R.color.color_orange) // default is Color.WHITE
                .duration(Snackbar.LENGTH_SHORT) // default is Snackbar.LENGTH_LONG
                .actionText("DISMISS") // default is "Action"
                .actionTextColorRes(R.color.colorAccent)
                .important()
                .action(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    public String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
    public String getDeviceUniqueID(Activity activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        Log.d("STATE NETWORK", String.valueOf(event.getState().getValue()));
        if(event.getState().getValue() == ConnectivityState.CONNECTED){
            // device has active internet connection
            encryptedPreferences.edit()
                    .putString("NETWORK", "1")
                    .apply();
        }
        else{
            // there is no active internet connection on this device
            encryptedPreferences.edit()
                    .putString("NETWORK", "0")
                    .apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionBuddy.getInstance().registerForConnectivityEvents(getActivity(), this);
    }

    @Override
    public void onStop() {
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(getActivity());
        super.onStop();
    }
}
