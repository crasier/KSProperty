package com.kswy.property.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.kswy.property.R;

import java.lang.reflect.Field;
import java.math.MathContext;

public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {

    private static final String START_YEAR = "start_year";
    private static final String START_MONTH = "start_month";
    private static final String START_DAY = "start_day";

    private final DatePicker mDatePickerStart;
    private final OnDateSetListener mCallBack;

    private String[] mDisplayMonths = {"1", "2", "3","4", "5", "6","7", "8", "9","10", "11", "12"};

    public interface OnDateSetListener {
        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
    }

    public DatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }


    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, context.getString(R.string.sure), this);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_datepicker, null);
        setView(view);
        mDatePickerStart = view.findViewById(R.id.datePickerStart);
        mDatePickerStart.init(year, monthOfYear, dayOfMonth, this);

        hideDay(mDatePickerStart);
    }

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 处理android5.0以上的特殊情况 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
                for (Field datePickerField : datePickerfFields) {
                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        try {
                            dayPicker = datePickerField.get(mDatePicker);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE)
            tryNotifyDateSet();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        ((NumberPicker)((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(0)).getChildAt(1)).setDisplayedValues(mDisplayMonths);
        if (view.getId() == R.id.datePickerStart)
            mDatePickerStart.init(year, month, day, this);
    }

    public DatePicker getDatePickerStart() {
        return mDatePickerStart;
    }


    public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePickerStart.updateDate(year, monthOfYear, dayOfMonth);
    }


    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mDatePickerStart.clearFocus();
            mCallBack.onDateSet(mDatePickerStart, mDatePickerStart.getYear(), mDatePickerStart.getMonth(),
                    mDatePickerStart.getDayOfMonth());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void show() {
        super.show();
        if (mDatePickerStart != null) {
            ((NumberPicker) ((ViewGroup) ((ViewGroup) mDatePickerStart.getChildAt(0)).getChildAt(0)).getChildAt(1)).setDisplayedValues(mDisplayMonths);
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePickerStart.getYear());
        state.putInt(START_MONTH, mDatePickerStart.getMonth());
        state.putInt(START_DAY, mDatePickerStart.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePickerStart.init(start_year, start_month, start_day, this);

    }
}
