package mhandharbeni.dev.absendroid.system.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by root on 27/02/17.
 */

public class CapturingCode extends AppCompatActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.d("QRCODE", "contents: " + contents);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("QRCODE", "RESULT_CANCELED");
            }
        }
    }
}
