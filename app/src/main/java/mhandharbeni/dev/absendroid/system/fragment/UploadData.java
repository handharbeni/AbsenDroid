package mhandharbeni.dev.absendroid.system.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.fanrunqi.waveprogress.WaveProgressView;
import io.realm.Realm;
import io.realm.RealmResults;
import mhandharbeni.dev.absendroid.R;
import mhandharbeni.dev.absendroid.system.Login;
import mhandharbeni.dev.absendroid.system.Menu;
import mhandharbeni.dev.absendroid.system.database.Presensi;
import mhandharbeni.dev.absendroid.system.database.PresensiHelper;
import mhandharbeni.dev.absendroid.system.holder.ImageTypeBig;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

/**
 * Created by root on 27/02/17.
 */

public class UploadData extends Fragment implements ConnectivityChangeListener {
    FloatingActionButton fabHome, fabLogout, fabCamera, fabUpload;
    EncryptedPreferences encryptedPreferences;
    String states;
    PresensiHelper pHelper;
    View v;
    private WaveProgressView waveProgressbar;
    Button btnSimpan;
    RelativeLayout rlMain, rlSecon;
    HttPizza client;
    String url;
    TextView totalData, totalDataUpload;
    private FluentSnackbar mFluentSnackbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            ConnectionBuddy.getInstance().clearNetworkCache(getActivity(), savedInstanceState);
        }
        ConnectionBuddyConfiguration networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(getActivity()).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        url = getActivity().getString(R.string.server)+"/server-absendroid/postAbsensi.php";
        client = new HttPizza();
        pHelper = new PresensiHelper(getActivity());

        v = inflater.inflate(R.layout.layout_upload, container, false);
        mFluentSnackbar = FluentSnackbar.create(getActivity());

        rlMain = (RelativeLayout) v.findViewById(R.id.mainLayout);
        rlSecon = (RelativeLayout) v.findViewById(R.id.secondLayout);
        btnSimpan = (Button) v.findViewById(R.id.btnUploadData);
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(String.valueOf(R.string.key)).build();
        waveProgressbar = (WaveProgressView) v.findViewById(R.id.waveProgressbar);
        fabLogout = (FloatingActionButton) v.findViewById(R.id.logout);
        fabCamera = (FloatingActionButton) v.findViewById(R.id.scanQRCode);
        fabUpload = (FloatingActionButton) v.findViewById(R.id.uploadPresensi);
        fabHome = (FloatingActionButton) v.findViewById(R.id.Home);

        fabHome.hide(false);
        fabLogout.hide(false);
        fabCamera.hide(false);
//        fabUpload.hide(false);

        fabHome.show(true);
        fabLogout.show(true);
        fabCamera.show(true);
//        fabUpload.show(true);

        checkData();
        totalData = (TextView) v.findViewById(R.id.totalData);
        totalDataUpload = (TextView) v.findViewById(R.id.totalDataUpload);

        changeView();
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                states = encryptedPreferences.getUtils().encryptStringValue("login");
                encryptedPreferences.edit()
                        .putString("STATE", states)
                        .apply();
                Fragment fr = new Login();
                changeFragment(fr);
            }
        });
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new ScanBarcode();
                changeFragment(fr);
            }
        });
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                states = encryptedPreferences.getUtils().encryptStringValue("menu");
                encryptedPreferences.edit()
                        .putString("STATE", states)
                        .apply();
                Fragment fr = new Menu();
                changeFragment(fr);

            }
        });

        return v;
    }
    public void changeView(){
        String countAll = String.valueOf(countData("All"));
        String countNotUpload = String.valueOf(countData("notUploaded"));

        totalData.setText(countAll);
        totalDataUpload.setText(countNotUpload);
    }
    public int countData(String type){
        RealmResults<Presensi> pResult = null;
        if(type == "All"){
            pResult = pHelper.getPresensi();
        }else if(type == "notUploaded"){
            pResult = pHelper.getNoneUploaded();
        }
        if (pResult.size() > 0) {
            return pResult.size();
        }
        return 0;
    }
    public void checkData(){
        int totalDataBelumUpload = countData("notUploaded");
        if(totalDataBelumUpload > 0){
            btnSimpan.setEnabled(true);
        }else{
            btnSimpan.setEnabled(false);
        }
    }
    public void syncData(){
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")) {

            checkData();
            rlMain.setVisibility(View.GONE);
            rlSecon.setVisibility(View.VISIBLE);
            waveProgressbar.setCurrent(0, "0/" + String.valueOf(countData("notUploaded")));
            waveProgressbar.setMaxProgress(countData("notUploaded"));
            waveProgressbar.setText("#FFFF00", 41);
            waveProgressbar.setWaveColor("#5b9ef4");

            final RealmResults<Presensi> pResult = pHelper.getNoneUploaded();
            if (pResult.size() > 0) {
                for (int i = 0; i < pResult.size(); i++) {
                    RequestBody formBody = new FormBody.Builder()
                            .add("perusahaan_id", pResult.get(i).getPerusahaan_id())
                            .add("random_code", pResult.get(i).getRandomCode())
                            .add("qr_state", pResult.get(i).getQr_state())
                            .add("pegawai_id", pResult.get(i).getPegawai_id())
                            .add("waktu_android", pResult.get(i).getTanggal_android() + " " + pResult.get(i).getWaktu_android())
                            .add("imei", pResult.get(i).getImei())
                            .add("latitude", pResult.get(i).getLatitude())
                            .add("longitude", pResult.get(i).getLongitude())
                            .add("akurasi", pResult.get(i).getAkurasi())
                            .build();
                    Request request = client.newRequest()
                            .url(url)
                            .post(formBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        String status = jsonObj.getString("status");
                        if (status.equalsIgnoreCase("4")) {
                            pHelper.UpdatePresensi(pResult.get(i).getId());
                        }
                        waveProgressbar.setCurrent(i, String.valueOf(i) + "/" + String.valueOf(countData("notUploaded")));
                    } catch (IOException e) {
                        e.printStackTrace();
                        rlMain.setVisibility(View.VISIBLE);
                        rlSecon.setVisibility(View.GONE);
                        showSnackBar("UPLOAD GAGAL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        rlMain.setVisibility(View.VISIBLE);
                        rlSecon.setVisibility(View.GONE);
                        showSnackBar("UPLOAD GAGAL");
                    }
                }
                changeView();
                rlMain.setVisibility(View.VISIBLE);
                rlSecon.setVisibility(View.GONE);
                showSnackBar("UPLOAD FINISH");
                checkData();
            }else{
                showSnackBar("DATA SUDAH TERUPLOAD!!");
            }
        }else{
            showSnackBar("TIDAK ADA KONEKSI!!");
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
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frameContainer, fragment);
        fm.commit();
    }


    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        Log.d("STATE NETWORK", String.valueOf(event.getState().getValue()));
        if(event.getState().getValue() == ConnectivityState.CONNECTED){
            encryptedPreferences.edit()
                    .putString("NETWORK", "1")
                    .apply();
        }
        else{
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
