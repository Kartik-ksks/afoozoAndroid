package in.kpis.afoozo.custome;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.SeekBar;

/**
 * Created by KHEMRAJ on 10/29/2018.
 */
public class ProgressBarAnimation extends Animation {

    private SeekBar progressBar;
    private float from;
    private float  to;

    public ProgressBarAnimation(SeekBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
}
