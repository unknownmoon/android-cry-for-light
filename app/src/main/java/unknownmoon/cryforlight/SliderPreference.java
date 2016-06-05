package unknownmoon.cryforlight;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
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
    private String mHeaderLabelText;
    private String mHeaderValueFormat;
    private int mSliderMax;
    private int mSliderValue;
    private TextView mHeaderValueView;

    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SliderPreference, defStyleAttr, defStyleRes);

        mDisplayHeaderLabel = a.getBoolean(R.styleable.SliderPreference_displayHeaderLabel, DISPLAY_HEADER_LABEL);
        mDisplayHeaderValue = a.getBoolean(R.styleable.SliderPreference_displayHeaderValue, DISPLAY_HEADER_VALUE);
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
        mHeaderValueView = (TextView) view.findViewById(R.id.pref_header_value);

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

        if (mHeaderValueView != null) {

            updateHeaderValueText(mSliderValue);

            if (mDisplayHeaderValue) {
                mHeaderValueView.setVisibility(View.VISIBLE);
            } else {
                mHeaderValueView.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);

        updateSlideValue(restorePersistedValue ? Integer.valueOf(getPersistedString(String.valueOf(mSliderValue)))
                : (int) defaultValue);

    }

    private void updateHeaderValueText(int value) {
        mHeaderValueView.setText(String.format(mHeaderValueFormat, value));
    }

    private void updateSlideValue(int newValue) {
        mSliderValue = newValue;
    }

    private void persistSlideValue() {
        persistString(String.valueOf(mSliderValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSlideValue(progress);

        updateHeaderValueText(progress);
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
