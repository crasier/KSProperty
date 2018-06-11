package com.kswy.property.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kswy.property.BaseActivity;
import com.kswy.property.R;
import com.kswy.property.bean.Fee;
import com.kswy.property.utils.MyToast;
import com.kswy.property.utils.ViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 支付界面
 * */
public class PayActivity extends BaseActivity {

    private static final String TAG = "PayActivity";
    
    private Unbinder unbinder;
    private LayoutInflater mInflator;
    private AlertDialog exitDialog;
    private PopupWindow selectPayPopWindow;
    private DialogFragment selectPayDialog;
    private float mFeeTotal;

    private ArrayList<Fee> payCharge;//应缴，按量
    private ArrayList<Fee> payNormal;//应缴，按月/用户
    private ArrayList<Fee> preCharge;//预交费，按量
    private ArrayList<Fee> preNormal;//预交费，按月

    @BindView(R.id.fee_pay_charge)
    protected ListView payChargeListView;
    @BindView(R.id.fee_pay_normal)
    protected ListView payNormalListView;
    @BindView(R.id.fee_pre_charge)
    protected ListView preChargeListView;
    @BindView(R.id.fee_pre_normal)
    protected ListView preNormalListView;
    @BindView(R.id.tab_pay_charge)
    protected LinearLayout tabPayCharge;
    @BindView(R.id.tab_pay_normal)
    protected LinearLayout tabPayNormal;
    @BindView(R.id.tab_pre_charge)
    protected LinearLayout tabPreCharge;
    @BindView(R.id.tab_pre_normal)
    protected LinearLayout tabPreNormal;
    @BindView(R.id.title_pay)
    protected TextView mTitlePay;
    @BindView(R.id.title_pre)
    protected TextView mTitlePre;
    @BindView(R.id.go_pay)
    protected TextView mGoPay;
    @BindView(R.id.charge_total)
    protected TextView mTotal;

    private PayChargeAdapter payChargeAdapter;
    private PayNormalAdapter payNormalAdapter;
    private PreChargeAdapter preChargeAdapter;
    private PreNormalAdapter preNormalAdapter;
    
    @Override
    protected void initUI(Bundle savedInstanceState) {
        addContentView(R.layout.layout_pay_activity);

        setTopBar(R.drawable.icon_back, R.string.fee_order_commit, 0);
        unbinder = ButterKnife.bind(this);

        payCharge = (ArrayList<Fee>) getIntent().getSerializableExtra("payCharge");
        payNormal = (ArrayList<Fee>) getIntent().getSerializableExtra("payNormal");
        preCharge = (ArrayList<Fee>) getIntent().getSerializableExtra("preCharge");
        preNormal = (ArrayList<Fee>) getIntent().getSerializableExtra("preNormal");
        mFeeTotal = getIntent().getFloatExtra("feeTotal", 0);
        mTotal.setText(getString(R.string.fee_charge_total, String.valueOf(mFeeTotal)));
        mGoPay.setOnClickListener(this);

        mInflator = LayoutInflater.from(this);

        boolean showPayTitle = false;
        boolean showPreTitle = false;

        if (payCharge == null || payCharge.size() <= 1) {
            tabPayCharge.setVisibility(View.GONE);
        }else {
            tabPayCharge.setVisibility(View.VISIBLE);
            payChargeAdapter = new PayChargeAdapter();
            payChargeListView.setAdapter(payChargeAdapter);
            showPayTitle = true;
        }

        if (payNormal == null || payNormal.size() <= 1) {
            tabPayNormal.setVisibility(View.GONE);
        }else {
            showPayTitle = true;
            tabPayNormal.setVisibility(View.VISIBLE);
            payNormalAdapter = new PayNormalAdapter();
            payNormalListView.setAdapter(payNormalAdapter);
        }

        if (preCharge == null || preCharge.size() <= 1) {
            tabPreCharge.setVisibility(View.GONE);
        }else {
            showPreTitle = true;
            tabPreCharge.setVisibility(View.VISIBLE);
            preChargeAdapter = new PreChargeAdapter();
            preChargeListView.setAdapter(preChargeAdapter);
        }

        if (preNormal == null || preNormal.size() <= 1) {
            tabPreNormal.setVisibility(View.GONE);
        }else {
            showPreTitle = true;
            tabPreNormal.setVisibility(View.VISIBLE);
            preNormalAdapter = new PreNormalAdapter();
            preNormalListView.setAdapter(preNormalAdapter);
        }

        mTitlePay.setVisibility(showPayTitle ? View.VISIBLE : View.GONE);
        mTitlePre.setVisibility(showPreTitle ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void freeMe() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_top_left:
                onBackPressed();
                break;
            case R.id.go_pay:
                showPayPlat();
                break;
            case R.id.pay_plat_zfb:
                MyToast.show(PayActivity.this, "走支付宝支付流程");
                break;
            case R.id.pay_plat_wx:
                MyToast.show(PayActivity.this, "走微信支付流程");
                break;
            case R.id.pay_outside:
            case R.id.pay_cancel:
                dismissSelectPayPopWindow();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (dismissSelectPayPopWindow()) {
            return;
        }
        sureToExit();
    }

    private void sureToExit() {
        if (exitDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.fee_pay_cancel)
                    .setPositiveButton(R.string.exit_anymore, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.exit_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            exitDialog = builder.create();
            exitDialog.setCancelable(true);
            exitDialog.setCanceledOnTouchOutside(false);
        }

        exitDialog.show();
    }

    private void showPayPlat() {
        if (selectPayPopWindow == null) {
            View rootView = mInflator.inflate(R.layout.layout_pop_pay, null);
            selectPayPopWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            selectPayPopWindow.setBackgroundDrawable(null);
            selectPayPopWindow.setClippingEnabled(false);
            rootView.findViewById(R.id.pay_outside).setOnClickListener(this);
            rootView.findViewById(R.id.pay_plat_zfb).setOnClickListener(this);
            rootView.findViewById(R.id.pay_plat_wx).setOnClickListener(this);
            rootView.findViewById(R.id.pay_cancel).setOnClickListener(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            selectPayPopWindow.showAsDropDown(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        }else {
            selectPayPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        }
    }

    private boolean dismissSelectPayPopWindow() {
        if (selectPayPopWindow != null && selectPayPopWindow.isShowing()) {
            selectPayPopWindow.dismiss();
            return true;
        }

        return false;
    }

    /**
     * 费用标题
     * */
    private Fee getFeeTitle() {
        Fee fee = new Fee();
        fee.setInNo(getString(R.string.fee_title_inno));
        fee.setInName(getString(R.string.fee_title_inname));
        fee.setFeeName(getString(R.string.fee_title_name));
        fee.setBuildId(getString(R.string.fee_title_buildid));
        fee.setTotal(getString(R.string.fee_title_total));
        fee.setPrice(getString(R.string.fee_title_price));
        fee.setCounts(getString(R.string.fee_title_counts));
        fee.setLastCount(getString(R.string.fee_title_lastCount));
        fee.setThisCount(getString(R.string.fee_title_thisCount));
        fee.setLastTime(getString(R.string.fee_title_lasttime));
        fee.setThisTime(getString(R.string.fee_title_thistime));
        fee.setPayMonth(getString(R.string.fee_title_paymonth));
        fee.setPayMonthTo(getString(R.string.fee_title_paymonth_to));
        fee.setIsPay(getString(R.string.fee_title_payed));
        fee.setCreator(getString(R.string.fee_title_creator));
        fee.setBillFile(getString(R.string.fee_title_billfile));
        fee.setPayFile(getString(R.string.fee_title_payfile));
        fee.setRemark(getString(R.string.fee_title_remark));

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
                    .setText(preNormal.get(i).getPayMonth());
            setPayedMonth(view, i);
            ViewHolder.<TextView> get(view, R.id.home_fee_price)
                    .setText(preNormal.get(i).getPrice());
            ViewHolder.<TextView> get(view, R.id.home_fee_total)
                    .setText(i == 0 ? preNormal.get(i).getTotal() : String.valueOf(getTotal(preNormal.get(i))));
            return view;
        }

        private void setPayedMonth(View convertView, final int position) {
            TextView textView = ViewHolder.get(convertView, R.id.home_fee_this);
            if (position == 0) {
                textView.setText(R.string.fee_title_thistime);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }else {
                textView.setText(preNormal.get(position).getPayMonthTo());
            }
        }
    }

    private float getTotal(Fee fee) {

        float total = 0;
        try {
            String startDate[] = fee.getPayMonth().trim().split("-");
            String endDate[] = fee.getPayMonthTo().trim().split("-");

            int year = Integer.parseInt(endDate[0]) - Integer.parseInt(startDate[0]);
            int month = Integer.parseInt(endDate[1]) - Integer.parseInt(startDate[1]);
            float price = Float.parseFloat(fee.getTotal());
            total = (year * 12 + month) * price;
        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setTotal: "+e);
        }

        return total;
    }
}
