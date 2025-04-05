package in.kpis.afoozo.custome;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import in.kpis.afoozo.AppInitialization;


/**
 * Created by Android1 on 1/9/2016.
 */
public class ButtonViewRegular extends AppCompatButton {
    public ButtonViewRegular(Context context) {
        super(context);
        init(context);
    }
    private void init(Context context) {
        this.setTypeface(AppInitialization.getFontRegular());
//        this.setBackground(context.getResources().getDrawable(R.drawable.btn_bg));
    }
    public ButtonViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public ButtonViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}