package in.kpis.afoozo.custome;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.kpis.afoozo.AppInitialization;


/**
 * Created by Android1 on 1/9/2016.
 */

public class TextViewBold extends AppCompatTextView {


    public TextViewBold(Context context) {
        super(context);
       init();
    }

    private void init() {
        this.setTypeface(AppInitialization.getFontBold());
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}