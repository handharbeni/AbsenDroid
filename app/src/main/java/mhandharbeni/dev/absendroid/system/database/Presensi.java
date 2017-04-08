package mhandharbeni.dev.absendroid.system.database;

import io.realm.RealmObject;

/**
 * Created by root on 18/02/17.
 */

public class Presensi extends RealmObject {
    private String id;

    private String perusahaan_id;
    private String pegawai_id;
    private String tanggal_android;
    private String waktu_android;
    private String qr_state;
    private String imei;
    private String latitude;
    private String longitude;
    private String akurasi;
    private Integer status;
    private String randomCode;


    public String getTanggal_android() { return tanggal_android; }

    public void setTanggal_android(String tanggal_android) { this.tanggal_android = tanggal_android; }

    public String getRandomCode() { return randomCode; }

    public void setRandomCode(String randomCode) { this.randomCode = randomCode; }

    public Integer getStatus() { return status; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void setStatus(Integer status) { this.status = status; }

    public String getPerusahaan_id() { return perusahaan_id; }

    public void setPerusahaan_id(String perusahaan_id) {
        this.perusahaan_id = perusahaan_id;
    }

    public String getPegawai_id() {
        return pegawai_id;
    }

    public void setPegawai_id(String pegawai_id) {
        this.pegawai_id = pegawai_id;
    }

    public String getWaktu_android() {
        return waktu_android;
    }

    public void setWaktu_android(String waktu_android) {
        this.waktu_android = waktu_android;
    }

    public String getQr_state() {
        return qr_state;
    }

    public void setQr_state(String qr_state) {
        this.qr_state = qr_state;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAkurasi() {
        return akurasi;
    }

    public void setAkurasi(String akurasi) {
        this.akurasi = akurasi;
    }
}
