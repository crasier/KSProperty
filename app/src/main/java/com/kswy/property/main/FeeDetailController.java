package com.kswy.property.main;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kswy.property.R;
import com.kswy.property.bean.Fee;
import com.kswy.property.bean.House;
import com.kswy.property.server.WebRequest;
import com.kswy.property.utils.ViewHolder;
import com.kswy.property.widgets.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 费用管理
 * */
public class FeeDetailController {

    private static final String TAG = "FeeDetailController";

    private Context mContext;
    private View rootView;
    private House mHouse;
    private LayoutInflater mInflator;
    private FinishedFill fillListener;
    private boolean isRequestFinished = true;

    private ArrayList<Fee> payCharge;//应缴，按量
    private ArrayList<Fee> payNormal;//应缴，按月/用户
    private ArrayList<Fee> preCharge;//预交费，按量
    private ArrayList<Fee> preNormal;//预交费，按月

    private ListView payChargeListView;
    private ListView payNormalListView;
    private ListView preChargeListView;
    private ListView preNormalListView;

    private PayChargeAdapter payChargeAdapter;
    private PayNormalAdapter payNormalAdapter;
    private PreChargeAdapter preChargeAdapter;
    private PreNormalAdapter preNormalAdapter;

    public FeeDetailController(Context context, View rootView, House house) {
        this.mContext = context;
        this.rootView = rootView;
        this.mHouse = house;
        mInflator = LayoutInflater.from(context);
    }

    public void fill() {

        isRequestFinished = false;

        payCharge = new ArrayList<>();
        payNormal = new ArrayList<>();
        preCharge = new ArrayList<>();
        preNormal = new ArrayList<>();

        payChargeListView = rootView.findViewById(R.id.fee_pay_charge);
        payNormalListView = rootView.findViewById(R.id.fee_pay_normal);
        preChargeListView = rootView.findViewById(R.id.fee_pre_charge);
        preNormalListView = rootView.findViewById(R.id.fee_pre_normal);

        payChargeAdapter = new PayChargeAdapter();
        payNormalAdapter = new PayNormalAdapter();
        preChargeAdapter = new PreChargeAdapter();
        preNormalAdapter = new PreNormalAdapter();


        payChargeListView.setAdapter(payChargeAdapter);
        payNormalListView.setAdapter(payNormalAdapter);
        preChargeListView.setAdapter(preChargeAdapter);
        preNormalListView.setAdapter(preNormalAdapter);

        getFees();
    }

    public void refresh() {
        getFees();
    }

    private void getFees() {

        final SparseBooleanArray tagArray = new SparseBooleanArray();

        final String inNo = mHouse.getInNo();

        WebRequest.getInstance().getChargeList(inNo, new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
                tagArray.put(0, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onNext(JSONArray array) {
                Log.e(TAG, "getChargeList "+inNo+"; onNext: array = "+array);
                tagArray.put(0, true);
                ArrayList<Fee> fees = (ArrayList<Fee>) JSON.parseArray(array.toJSONString(), Fee.class);
                Log.e(TAG, "getChargeList "+inNo+"; onNext: fee = "+fees);
                payCharge.addAll(fees);
                payChargeAdapter.notifyDataSetChanged();
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getChargeList "+inNo+"; onError: "+e);
                tagArray.put(0, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "getChargeList "+inNo+"; onComplete: ");
                tagArray.put(0, true);
                isRequestFinished = isRequestFinish(tagArray);
            }
        });

        WebRequest.getInstance().getNormalPay(inNo, new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
                tagArray.put(1, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onNext(JSONArray array) {
                Log.e(TAG, "getNormalPay: "+inNo+"; onNext: array = "+array);
                tagArray.put(1, true);
                ArrayList<Fee> fees = (ArrayList<Fee>) JSON.parseArray(array.toJSONString(), Fee.class);
                payNormal.addAll(fees);
                Log.e(TAG, "getNormalPay: "+inNo+"; onNext: fees = "+payNormal);
                payNormalAdapter.notifyDataSetChanged();
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getNormalPay: "+inNo+"; onError: "+e);
                tagArray.put(1, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "getNormalPay: "+inNo+"; onComplete: ");
                tagArray.put(1, true);
                isRequestFinished = isRequestFinish(tagArray);
            }
        });

        WebRequest.getInstance().getLastPrePay(inNo, new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
                tagArray.put(2, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onNext(JSONArray array) {
                Log.e(TAG, "getLastPrePay "+inNo+"; onNext: array = "+array);
                tagArray.put(2, true);
                ArrayList<Fee> fees = (ArrayList<Fee>) JSON.parseArray(array.toJSONString(), Fee.class);
                preNormal.addAll(fees);
                Log.e(TAG, "getLastPrePay "+inNo+"; onNext: fees = "+fees);
                preNormalAdapter.notifyDataSetChanged();
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getLastPrePay "+inNo+"; onError: "+e);
                tagArray.put(2, true);
                isRequestFinished = isRequestFinish(tagArray);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "getLastPrePay "+inNo+"; onComplete: ");
                tagArray.put(2, true);
                isRequestFinished = isRequestFinish(tagArray);
            }
        });
    }

    public synchronized boolean isRequestFinish(SparseBooleanArray array) {
        for (int i = 0; i < array.size(); i++) {
            if (!array.get(array.keyAt(i))) {
                return false;
            }
        }

        if (fillListener != null) {
            fillListener.onFinished(rootView);
        }

        return true;
    }


    public boolean isRequestFinished() {
        return isRequestFinished;
    }


    public void setOnFinishListener(FinishedFill onFinishListener) {
        fillListener = onFinishListener;
    }

    public interface FinishedFill {
        public void onFinished(View view);
    }

    /**
     * 费用标题
     * */
    private synchronized Fee getFeeTitle() {
        Fee fee = new Fee();
        fee.setInNo(mContext.getString(R.string.fee_title_inno));
        fee.setInName(mContext.getString(R.string.fee_title_inname));
        fee.setFeeName(mContext.getString(R.string.fee_title_name));
        fee.setBuildId(mContext.getString(R.string.fee_title_buildid));
        fee.setTotal(mContext.getString(R.string.fee_title_total));
        fee.setPrice(mContext.getString(R.string.fee_title_price));
        fee.setCounts(mContext.getString(R.string.fee_title_counts));
        fee.setLastCount(mContext.getString(R.string.fee_title_lastCount));
        fee.setThisCount(mContext.getString(R.string.fee_title_thisCount));
        fee.setLastTime(mContext.getString(R.string.fee_title_lasttime));
        fee.setThisTime(mContext.getString(R.string.fee_title_thistime));
        fee.setPayMonth(mContext.getString(R.string.fee_title_paymonth));
        fee.setIsPay(mContext.getString(R.string.fee_title_payed));
        fee.setCreator(mContext.getString(R.string.fee_title_creator));
        fee.setBillFile(mContext.getString(R.string.fee_title_billfile));
        fee.setPayFile(mContext.getString(R.string.fee_title_payfile));
        fee.setRemark(mContext.getString(R.string.fee_title_remark));

        fee.setHasTitle(true);
        return fee;
    }

    private class PayChargeAdapter extends BaseAdapter {

        private View footer;

        public PayChargeAdapter() {
            footer = mInflator.inflate(R.layout.view_text, null);
            initData();
        }

        @Override
        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        private void initData() {
            if (payCharge == null || payCharge.size() == 0
                    || (payCharge.size() == 1 && payCharge.get(0).isHasTitle())) {
                if (payChargeListView.getFooterViewsCount() == 0) {
                    payChargeListView.addFooterView(footer);
                }
                if (payCharge == null) {
                    payCharge = new ArrayList<>();
                }
                if (payCharge.size() == 0) {
                    payCharge.add(getFeeTitle());
                }
            }else {
                if (payChargeListView.getFooterViewsCount() > 0) {
                    payChargeListView.removeFooterView(footer);
                }
            }
            if (!payCharge.get(0).isHasTitle()) {
                payCharge.add(0, getFeeTitle());
            }
        }

        @Override
        public int getCount() {

            return payCharge.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflator.inflate(R.layout.item_pay_charge, null);
            }
            ViewHolder.<TextView> get(view, R.id.home_fee_inno)
                    .setText(payCharge.get(i).getInNo());
            ViewHolder.<TextView> get(view, R.id.home_fee_inname)
                    .setText(payCharge.get(i).getInName());
            ViewHolder.<TextView> get(view, R.id.home_fee_name)
                    .setText(payCharge.get(i).getFeeName());
            ViewHolder.<TextView> get(view, R.id.home_fee_last)
                    .setText(payCharge.get(i).getLastCount());
            ViewHolder.<TextView> get(view, R.id.home_fee_this)
                    .setText(payCharge.get(i).getThisCount());
            ViewHolder.<TextView> get(view, R.id.home_fee_count)
                    .setText(payCharge.get(i).getCounts());
            ViewHolder.<TextView> get(view, R.id.home_fee_price)
                    .setText(payCharge.get(i).getPrice());
            ViewHolder.<TextView> get(view, R.id.home_fee_total)
                    .setText(payCharge.get(i).getTotal());
            return view;
        }
    }

    private class PayNormalAdapter extends BaseAdapter {

        private View footer;

        public PayNormalAdapter() {
            footer = mInflator.inflate(R.layout.view_text, null);
            initData();
        }

        @Override
        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        private void initData() {
            if (payNormal == null || payNormal.size() == 0
                    ||(payNormal.size() == 1 && payNormal.get(0).isHasTitle())) {
                if (payNormalListView.getFooterViewsCount() == 0) {
                    payNormalListView.addFooterView(footer);
                }
                if (payNormal == null) {
                    payNormal = new ArrayList<>();
                }
                if (payNormal.size() == 0) {
                    payNormal.add(getFeeTitle());
                }
            }else {
                if (payNormalListView.getFooterViewsCount() > 0) {
                    payNormalListView.removeFooterView(footer);
                }
            }
            if (!payNormal.get(0).isHasTitle()) {
                payNormal.add(0, getFeeTitle());
            }
        }

        @Override
        public int getCount() {
            return payNormal.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflator.inflate(R.layout.item_pay_normal, null);
            }
            ViewHolder.<TextView> get(view, R.id.home_fee_inno)
                    .setText(payNormal.get(i).getInNo());
            ViewHolder.<TextView> get(view, R.id.home_fee_inname)
                    .setText(payNormal.get(i).getInName());
            ViewHolder.<TextView> get(view, R.id.home_fee_name)
                    .setText(payNormal.get(i).getFeeName());
            ViewHolder.<TextView> get(view, R.id.home_fee_paymonth)
                    .setText(payNormal.get(i).getPayMonth());
            ViewHolder.<TextView> get(view, R.id.home_fee_price)
                    .setText(payNormal.get(i).getPrice());
            ViewHolder.<TextView> get(view, R.id.home_fee_total)
                    .setText(payNormal.get(i).getTotal());
            ViewHolder.<TextView> get(view, R.id.home_fee_remark)
                    .setText(payNormal.get(i).getRemark());
            return view;
        }
    }

    private class PreChargeAdapter extends BaseAdapter {

        private View footer;

        public PreChargeAdapter() {
            footer = mInflator.inflate(R.layout.view_text, null);
            initData();
        }

        @Override
        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        private void initData() {
            if (preCharge == null || preCharge.size() == 0
                    || (preCharge.size() == 1 && preCharge.get(0).isHasTitle())) {
                if (preChargeListView.getFooterViewsCount() == 0) {
                    preChargeListView.addFooterView(footer);
                }
                if (preCharge == null) {
                    preCharge = new ArrayList<>();
                }
                if (preCharge.size() == 0) {
                    preCharge.add(getFeeTitle());
                }
            }else {
                if (preChargeListView.getFooterViewsCount() > 0) {
                    preChargeListView.removeFooterView(footer);
                }
            }
            if (!preCharge.get(0).isHasTitle()) {
                preCharge.add(0, getFeeTitle());
            }
        }

        @Override
        public int getCount() {
            return preCharge.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflator.inflate(R.layout.item_pre_charge, null);
            }

            ViewHolder.<TextView> get(view, R.id.home_fee_inno)
                    .setText(preCharge.get(i).getInNo());
            ViewHolder.<TextView> get(view, R.id.home_fee_inname)
                    .setText(preCharge.get(i).getInName());
            ViewHolder.<TextView> get(view, R.id.home_fee_name)
                    .setText(preCharge.get(i).getFeeName());
            ViewHolder.<TextView> get(view, R.id.home_fee_last)
                    .setText(preCharge.get(i).getLastCount());
            ViewHolder.<TextView> get(view, R.id.home_fee_this)
                    .setText(preCharge.get(i).getThisCount());
            ViewHolder.<TextView> get(view, R.id.home_fee_price)
                    .setText(preCharge.get(i).getPrice());

            return view;
        }
    }

    private class PreNormalAdapter extends BaseAdapter {

        private View footer;

        public PreNormalAdapter() {
            footer = mInflator.inflate(R.layout.view_text, null);
            initData();
        }

        @Override
        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        private void initData() {
            if (preNormal == null || preNormal.size() == 0
                    || (preNormal.size() == 1 && preNormal.get(0).isHasTitle())) {
                if (preNormalListView.getFooterViewsCount() == 0) {
                    preNormalListView.addFooterView(footer);
                }
                if (preNormal == null) {
                    preNormal = new ArrayList<>();
                }
                if (preNormal.size() == 0) {
                    preNormal.add(getFeeTitle());
                }
            }else {
                if (preNormalListView.getFooterViewsCount() > 0) {
                    preNormalListView.removeFooterView(footer);
                }
            }
            if (!preNormal.get(0).isHasTitle()) {
                preNormal.add(0, getFeeTitle());
            }
        }

        @Override
        public int getCount() {
            return preNormal.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflator.inflate(R.layout.item_pre_normal, null);
            }

            ViewHolder.<TextView> get(view, R.id.home_fee_inno)
                    .setText(preNormal.get(i).getInNo());
            ViewHolder.<TextView> get(view, R.id.home_fee_inname)
                    .setText(preNormal.get(i).getInName());
            ViewHolder.<TextView> get(view, R.id.home_fee_name)
                    .setText(preNormal.get(i).getFeeName());
            ViewHolder.<TextView> get(view, R.id.home_fee_last)
                    .setText(preNormal.get(i).getLastTime());
            setPayedMonth(view, i);
            ViewHolder.<TextView> get(view, R.id.home_fee_price)
                    .setText(preNormal.get(i).getPrice());
            ViewHolder.<TextView> get(view, R.id.home_fee_total)
                    .setText(preNormal.get(i).getTotal());

            return view;
        }

        private void setPayedMonth(View convertView, final int position) {
            TextView textView = ViewHolder.get(convertView, R.id.home_fee_this);
            if (position == 0) {
                textView.setText(R.string.fee_title_thistime);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }else {
                textView.setText(preNormal.get(position).getThisTime());
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_calendar, 0);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectDate(position);
                    }
                });
            }
        }
    }

    private void selectDate(final int position) {
        Calendar c = Calendar.getInstance();
        final Fee fee = preNormal.get(position);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        try {
            if (!TextUtils.isEmpty(fee.getLastTime())) {
                String date[] = fee.getLastTime().split("-");
                year = Integer.parseInt(date[0]);
                month = Integer.parseInt(date[1]) - 1;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        new DatePickerDialog(mContext, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth) {
                String textString = String.format("选择年月：%d-%d\n", startYear,
                        startMonthOfYear + 1);
                Log.e(TAG, "onDateSet: selectedDate = "+textString);
                fee.setThisTime(String.format(Locale.CHINA, "%d-%d", startYear, startMonthOfYear + 1));
                preNormal.set(position, fee);
                preNormalAdapter.notifyDataSetChanged();
            }
        }, year, month, c.get(Calendar.DATE)).show();
    }
}
