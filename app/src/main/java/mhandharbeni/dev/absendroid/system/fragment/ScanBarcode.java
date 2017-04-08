package mhandharbeni.dev.absendroid.system.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import mhandharbeni.dev.absendroid.R;
import mhandharbeni.dev.absendroid.system.Menu;

/**
 * Created by root on 22/02/17.
 */

public class ScanBarcode extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    Fragment fr = new Menu();
                    changeFragment(fr);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(getActivity(), "Contents = " + rawResult +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        Bundle bundle=new Bundle();
        bundle.putString("code", rawResult.getText());
        Fragment fr = new Menu();
        fr.setArguments(bundle);
        changeFragment(fr);
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frameContainer, fragment);
        fm.commit();
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
