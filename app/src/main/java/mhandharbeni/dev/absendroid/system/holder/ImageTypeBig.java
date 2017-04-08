package mhandharbeni.dev.absendroid.system.holder;

import android.content.Context;
import android.widget.TextView;

import com.mindorks.placeholderview.Animation;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.LongClick;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import mhandharbeni.dev.absendroid.R;

/**
 * Created by root on 19/02/17.
 */

@Animate(Animation.ENTER_LEFT_DESC)
@NonReusable
@Layout(R.layout.item_presensi)
public class ImageTypeBig {

    @View(R.id.txtCaption)
    private TextView txtCaption;

    @View(R.id.txtDate)
    private TextView txtDate;

    @View(R.id.txtStatus)
    private TextView txtStatus;

    private String mUlr;
    private String date;
    private String caption;
    private String status;
    private Context mContext;
    private PlaceHolderView mPlaceHolderView;

    public ImageTypeBig(Context context, PlaceHolderView placeHolderView, String date, String caption, String status) {
        mContext = context;
        mPlaceHolderView = placeHolderView;
//        mUlr = ulr;
        this.date = date;
        this.caption = caption;
        this.status = status;
    }

    @Resolve
    private void onResolved() {
//        Glide.with(mContext).load(mUlr).into(imageView);
        txtCaption.setText(this.caption);
        txtDate.setText(this.date);
        txtStatus.setText(this.status);
    }

    @LongClick(R.id.txtCaption)
    private void onLongClick(){
        mPlaceHolderView.removeView(this);
    }

}