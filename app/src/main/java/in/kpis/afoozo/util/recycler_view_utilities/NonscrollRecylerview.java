package in.kpis.afoozo.util.recycler_view_utilities;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by wingstud on 15-06-2017.
 */
public class NonscrollRecylerview extends RecyclerView {

    public NonscrollRecylerview(Context context) {
        super(context);
    }

    public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonscrollRecylerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}