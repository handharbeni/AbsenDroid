package mhandharbeni.dev.absendroid.system.database;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by root on 02/03/17.
 */

public class PresensiHelper {
    private static final String TAG = "PresensiHelper";

    private Realm realm;
    private RealmResults<Presensi> realmResult;
    public Context context;

    public PresensiHelper(Context context) {
        this.context = context;
        Realm.init(context);
        try{
            realm = Realm.getDefaultInstance();

        }catch (Exception e){

            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }
    }
    public void AddPresensi(Presensi pr){
        Presensi presens = new Presensi();
        presens.setId(String.valueOf((int) (System.currentTimeMillis() / 1000)));
        presens.setPerusahaan_id(pr.getPerusahaan_id());
        presens.setPegawai_id(pr.getPegawai_id());
        presens.setTanggal_android(pr.getTanggal_android());
        presens.setWaktu_android(pr.getWaktu_android());
        presens.setQr_state(pr.getQr_state());
        presens.setImei(pr.getImei());
        presens.setLatitude(pr.getLatitude());
        presens.setLongitude(pr.getLongitude());
        presens.setAkurasi(pr.getAkurasi());
        presens.setStatus(pr.getStatus());
        presens.setRandomCode(pr.getRandomCode());

        realm.beginTransaction();
        realm.copyToRealm(presens);
        realm.commitTransaction();
    }
    public RealmResults<Presensi> getPresensi(){
        realmResult = realm.where(Presensi.class).findAll();
        return realmResult;
    }
    public RealmResults<Presensi> getNoneUploaded(){
        realmResult = realm.where(Presensi.class).equalTo("status", 0).findAll();
        return realmResult;
    }

    public void UpdatePresensi(String id){
        realm.beginTransaction();
        Presensi presens = realm.where(Presensi.class).equalTo("id", id).findFirst();
        presens.setStatus(1);
        realm.commitTransaction();
    }
    public boolean checkDuplicate(String tanggalSekarang, String randomCode, String qrState){
        realmResult = realm.where(Presensi.class)
                .equalTo("tanggal_android", tanggalSekarang)
                .equalTo("randomCode",randomCode)
                .equalTo("qr_state",qrState)
                .findAll();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
    public void deleteData(int id) {
//        RealmResults<Article> dataDesults = realm.where(Article.class).equalTo("id", id).findAll();
//        realm.beginTransaction();
//        dataDesults.remove(0);
//        dataDesults.removeLast();
//        dataDesults.clear();
//        realm.commitTransaction();
//
//        showToast("Hapus data berhasil.");
    }

}
