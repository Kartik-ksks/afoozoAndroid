package in.kpis.afoozo.custome;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import in.kpis.afoozo.AppInitialization;

public class TextViewFontMaker extends AppCompatTextView {

    public TextViewFontMaker(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setTypeface(AppInitialization.getFontMaker());
    }
    public TextViewFontMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TextViewFontMaker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
