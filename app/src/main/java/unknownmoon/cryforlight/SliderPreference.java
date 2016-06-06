package unknownmoon.cryforlight;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by jing on 05/06/2016.
 */
public class SliderPreference extends Preference implements SeekBar.OnSeekBarChangeListener {

    public static Boolean DISPLAY_HEADER_LABEL = true;
    public static Boolean DISPLAY_HEADER_VALUE = true;
    public static int SLIDER_MAX = 100;

    private Boolean mDisplayHeaderLabel;
    private Boolean mDisplayHeaderValue;
    private String mSummary;
    private String mHeaderLabelText;
    private String mHeaderValueFormat;
    private int mSliderMax;
    private int mSliderValue;

    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SliderPreference, defStyleAttr, defStyleRes);

        mDisplayHeaderLabel = a.getBoolean(R.styleable.SliderPreference_displayHeaderLabel, DISPLAY_HEADER_LABEL);
        mDisplayHeaderValue = a.getBoolean(R.styleable.SliderPreference_displayHeaderValue, DISPLAY_HEADER_VALUE);
        mSummary = a.getString(R.styleable.SliderPreference_summary);
        mHeaderLabelText = a.getString(R.styleable.SliderPreference_headerLabelText);
        mHeaderValueFormat = a.getString(R.styleable.SliderPreference_headerValueFormat);
        mSliderMax = a.getInt(R.styleable.SliderPreference_sliderMaxValue, SLIDER_MAX);

        a.recycle();
    }

    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SliderPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sliderPreferenceStyle);
    }

    public SliderPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        SeekBar seekBarView = (SeekBar) view.findViewById(R.id.pref_seek_bar);
        TextView headerLabelView = (TextView) view.findViewById(R.id.pref_header_label);
        TextView summaryView = (TextView) view.findViewById(R.id.pref_summary);
        TextView headerValueView = (TextView) view.findViewById(R.id.pref_header_value);

        if (seekBarView != null) {

            seekBarView.setMax(mSliderMax);
            seekBarView.setProgress(mSliderValue);

            seekBarView.setOnSeekBarChangeListener(this);
        }

        if (headerLabelView != null) {

            headerLabelView.setText(mHeaderLabelText);

            if (mDisplayHeaderLabel) {
                headerLabelView.setVisibility(TextView.VISIBLE);
            } else {
                headerLabelView.setVisibility(TextView.INVISIBLE);
            }
        }

        if (headerValueView != null) {

            updateHeaderValueText(headerValueView, mSliderValue);

            if (mDisplayHeaderValue) {
                headerValueView.setVisibility(View.VISIBLE);
            } else {
                headerValueView.setVisibility(View.INVISIBLE);
            }
        }

        if (summaryView != null) {
            if (mSummary.length() != 0) {
                summaryView.setText(mSummary);
            } else {
                summaryView.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);

        updateSlideValue(restorePersistedValue ? getPersistedInt(mSliderValue)
                : (int) defaultValue);

    }

    private void updateHeaderValueText(TextView v, int value) {
        if (v != null) {
            v.setText(String.format(mHeaderValueFormat, value));
        }
    }

    private void updateSlideValue(int newValue) {
        mSliderValue = newValue;
    }

    private void persistSlideValue() {
        persistInt(mSliderValue);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView headerValueView = (TextView) getView(null, (ViewGroup) seekBar.getParent()).findViewById(R.id.pref_header_value);
        updateSlideValue(progress);
        updateHeaderValueText(headerValueView, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        persistSlideValue();
        notifyChanged();
    }
}
