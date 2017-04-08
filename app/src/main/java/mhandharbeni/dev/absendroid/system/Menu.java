package mhandharbeni.dev.absendroid.system;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.mindorks.placeholderview.PlaceHolderView;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmResults;
import mhandharbeni.dev.absendroid.R;
import mhandharbeni.dev.absendroid.system.database.Presensi;
import mhandharbeni.dev.absendroid.system.database.PresensiHelper;
import mhandharbeni.dev.absendroid.system.fragment.ScanBarcode;
import mhandharbeni.dev.absendroid.system.fragment.UploadData;
import mhandharbeni.dev.absendroid.system.holder.ImageTypeBig;

/**
 * Created by root on 14/02/17.
 */

public class Menu extends Fragment {
    FloatingActionButton fabLogout, fabCamera, fabUpload;
    EncryptedPreferences encryptedPreferences;
    String states;
    PresensiHelper pHelper;
    View v;
    private FluentSnackbar mFluentSnackbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pHelper = new PresensiHelper(getActivity());

        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(String.valueOf(R.string.key)).build();

        getActivity().setTitle(encryptedPreferences.getString("EMAIL", "ABSEN DROID"));

        v = inflater.inflate(R.layout.main_menu, container, false);
        mFluentSnackbar = FluentSnackbar.create(getActivity());



        fabLogout = (FloatingActionButton) v.findViewById(R.id.logout);
        fabCamera = (FloatingActionButton) v.findViewById(R.id.scanQRCode);
        fabUpload = (FloatingActionButton) v.findViewById(R.id.uploadPresensi);

        fabLogout.hide(false);
        fabCamera.hide(false);
        fabUpload.hide(false);

        fabLogout.show(true);
        fabCamera.show(true);
        fabUpload.show(true);

//        fabLogout.hide(false);
//        fabCamera.hide(false);
//        fabUpload.hide(false);

//        fabLogout.show(true);
//        fabCamera.show(true);
//        fabUpload.show(true);
//        FloatingActionMenu fabMenu = (FloatingActionMenu) v.findViewById(R.id.menu);
//        fabMenu.showMenu(true);

        PlaceHolderView mGalleryView = (PlaceHolderView)v.findViewById(R.id.presensiView);
        mGalleryView.getBuilder()
        .setHasFixedSize(false)
        .setItemViewCacheSize(10)
        .setLayoutManager(new GridLayoutManager(getActivity(), 1));
        RealmResults<Presensi> pResult = pHelper.getPresensi();
        if (pResult.size() > 0){
         for (int i=0; i<pResult.size();i++){
             mGalleryView.addView(
                     new ImageTypeBig(
                             getActivity().getApplicationContext(),
                             mGalleryView,
                             pResult.get(i).getTanggal_android()+" "+pResult.get(i).getWaktu_android(),
                             pResult.get(i).getQr_state().equalsIgnoreCase("1")?"DATANG":"PULANG",
                             pResult.get(i).getStatus()==1?"SUDAH DIUPLOAD":"BELUM DIUPLOAD"
                     )
             );

         }
            mGalleryView.refresh();
        }
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
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                states = encryptedPreferences.getUtils().encryptStringValue("upload");
                encryptedPreferences.edit()
                        .putString("STATE", states)
                        .apply();
                Fragment fr = new UploadData();
                changeFragment(fr);
            }
        });

        return v;
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frameContainer, fragment);
        fm.commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            String qr = args.getString("code");
            String[] splitQr = qr.split("#");
            String idPerusahaan = splitQr[0];
            String randomCode   = splitQr[1];
            String qrState      = splitQr[2];
            SimpleDateFormat waktu      = new SimpleDateFormat("HH:mm:ss");
            String currentTime          = waktu.format(new Date());
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Boolean checkDuplicate = pHelper.checkDuplicate(currentDate, randomCode, qrState);
            if (checkDuplicate == false){
                Presensi pr = new Presensi();
                pr.setPerusahaan_id(idPerusahaan);
                pr.setPegawai_id(encryptedPreferences.getString("ID", "0"));
                pr.setTanggal_android(currentDate);
                pr.setWaktu_android(currentTime);
                pr.setQr_state(qrState);
                pr.setImei(getIMEI(getActivity()));
                pr.setLatitude(encryptedPreferences.getString("Latitude", "0"));
                pr.setLongitude(encryptedPreferences.getString("Longitude", "0"));
                pr.setAkurasi(encryptedPreferences.getString("Akurasi", "0"));
                pr.setStatus(0);
                pr.setRandomCode(randomCode);
                pHelper.AddPresensi(pr);
            }else{
                showSnackBar("MAAF, ANDA TELAH MELAKUKAN ABSENSI SEBELUMNYA");
            }
        }
    }
    public void showSnackBar(String message){
        mFluentSnackbar.create(message)
                .maxLines(2)
                .backgroundColorRes(R.color.colorPrimary)
                .textColorRes(R.color.color_orange)
                .duration(Snackbar.LENGTH_SHORT)
                .actionText("DISMISS")
                .actionTextColorRes(R.color.colorAccent)
                .important()
                .action(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
}
