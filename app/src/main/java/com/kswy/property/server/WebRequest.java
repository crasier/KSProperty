package com.kswy.property.server;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kswy.property.bean.User;
import com.kswy.property.server.retrofit.ApiManager;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.HttpException;

public class WebRequest {
    private static WebRequest instance;

    public enum RType {
        JSONObject,
        JSONArray,
        String,
        Boolean,
        NoBody,
        Void
    }

    public static WebRequest getInstance() {
        if (instance == null) {
            synchronized (WebRequest.class) {
                if (instance == null) {
                    instance = new WebRequest();
                }
            }
        }

        return instance;
    }

    /**
     * 尝试使用本地保存的token，如果失败，并且是认证错误，重新去获取token
     * @param shortUrl 获取数据的短链接
     * @param json 数据
     * @param observer 回调监听
     * @param type 回调数据数据类型
     * */
    private void withToken(final String shortUrl, final String json, final Observer observer, final RType type) {
        final String dataUrl = ApiManager.URL_BASE + shortUrl;
        Observable observable;
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        switch (type) {
            case JSONObject:
                observable = ApiManager.getInstance().getRetrofitService().requestObj(User.getUser().getToken(), dataUrl, body);
                break;
            case JSONArray:
                observable =  ApiManager.getInstance().getRetrofitService().requestArr(User.getUser().getToken(), dataUrl, body);
                break;
            case String:
                observable =  ApiManager.getInstance().getRetrofitService().requestStr(User.getUser().getToken(), dataUrl, body);
                break;
            case Boolean:
                observable =  ApiManager.getInstance().getRetrofitService().requestBool(User.getUser().getToken(), dataUrl, body);
                break;
            case NoBody:
                observable =  ApiManager.getInstance().getRetrofitService().requestNoBody(User.getUser().getToken(), dataUrl, body);
                break;
            case Void:
                observable =  ApiManager.getInstance().getRetrofitService().requestNoBody(User.getUser().getToken(), dataUrl, body);
                break;
            default:
                observable =  ApiManager.getInstance().getRetrofitService().requestStr(User.getUser().getToken(), dataUrl, body);
        }


        if (type == RType.Void) {
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<retrofit2.Response<Void>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    observer.onSubscribe(d);
                }

                @Override
                public void onNext(retrofit2.Response<Void> entity) {
                    observer.onNext(null);
                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof HttpException) {
                        if (((HttpException) e).code() == 401) {
                            withNewToken(shortUrl, json, observer, type);
                            return;
                        }
                    }
                    observer.onError(e);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        }else {
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            observer.onSubscribe(d);
                        }

                        @Override
                        public void onNext(Object o) {
                            observer.onNext(o);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof HttpException) {
                                if (((HttpException) e).code() == 401) {
                                    withNewToken(shortUrl, json, observer, type);
                                    return;
                                }
                            }
                            observer.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            observer.onComplete();
                        }
                    });
        }

    }

    /**
     * 重新去获取token,然后执行请求
     * @param shortUrl 获取数据的短链接
     * @param json 数据
     * @param observer 回调监听
     * @param type 回调数据数据类型
     * */
    private void withNewToken(final String shortUrl, final String json, final Observer observer, final RType type) {
        final String dataUrl = ApiManager.URL_BASE + shortUrl;
        final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        ApiManager.getInstance().getRetrofitService()
                .login(User.getUser().getAccount(), User.getUser().getPassword())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<JSONObject, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(JSONObject object) throws Exception {
                        Log.e("webRequest", "apply: "+object);
                        String token = object.getString("token");
                        User.getUser().setToken(token);
                        switch (type) {
                            case JSONObject:
                                return ApiManager.getInstance().getRetrofitService().requestObj(token, dataUrl, body);
                            case JSONArray:
                                return ApiManager.getInstance().getRetrofitService().requestArr(token, dataUrl, body);
                            case String:
                                return ApiManager.getInstance().getRetrofitService().requestStr(token, dataUrl, body);
                            case Boolean:
                                return ApiManager.getInstance().getRetrofitService().requestBool(token, dataUrl, body);
                            case NoBody:
                                return ApiManager.getInstance().getRetrofitService().requestNoBody(token, dataUrl, body);
                            default:
                                return ApiManager.getInstance().getRetrofitService().requestStr(token, dataUrl, body);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void login(String usr, String pwd, Observer<JSONObject> listener) {

        ApiManager.getInstance().getRetrofitService().
                login(usr, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    /**
     * 注册用户
     * @param userId 用户名（手机号)
     * */
    public void addUser(String userId, String pwd, String nick, Observer listener) {
        String shortUrl = "RegisterUser";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("userName", nick);
        jsonObject.put("password", pwd);
        jsonObject.put("status", "Y");
        jsonObject.put("roleId", 3);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toJSONString());

        ApiManager.getInstance().getRetrofitService()
                .requestNoBody("", ApiManager.URL_BASE + shortUrl, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
    }

    /**
     * 获取用户绑定的楼宇信息
     *
     * 此接口需要先获取token
     * @param userId  用户ID
     * @param listener 请求回调监听
     * */
    public void getUserInhabitant(String userId, Observer listener) {
        String shortUrl = "Base/GetUserInhabitant";

        JSONObject json = new JSONObject();
        json.put("userId", userId);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 根据房屋门牌号获取该房屋对应的费用
     *
     * 此接口需要先获取token
     * @param inNo  房屋ID （门牌号）
     * @param listener 请求回调监听
     * */
    public void GetFeeByInhabi(String inNo, Observer listener) {
        String shortUrl = "Base/GetFeeByInhabi";
        JSONObject json = new JSONObject();
        json.put("InNo", inNo);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 更新用户绑定住房
     * @param userId 用户名
     * @param inNo 住房id(增加、删除均使用这个接口)
     * @param listener 回调监听
     * */
    public void saveUserInhabitant(String userId, String inNo[], Observer listener) {
        String shortUrl = "Base/SaveUserInhabitant";
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        json.put("userId", userId);

        for (int i = 0; i < inNo.length; i++) {
            JSONObject obj = new JSONObject();
            obj.put("InNo", inNo[i]);
            arr.add(obj);
        }
        json.put("InNos", arr);

        Log.e("webRequest", "saveUserInhabitant: body = "+ json.toJSONString());

        withToken(shortUrl, json.toJSONString(), listener, RType.NoBody);
    }

    /**
     * 获取所有小区楼房信息
     */
    public void getBuild(Observer listener) {
        String shortUrl = "Base/GetBuild";
        withToken(shortUrl, new JSONObject().toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 根据楼宇信息获取楼上住户信息
     *
     * @param id 楼宇的id
     */
    public void getInhabitantByBuild(int id, Observer listener) {
        String shortUrl = "Base/GetInhabitantByBuild";
        JSONObject json = new JSONObject();
        json.put("id", id);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 根据用户门牌号获取应缴费项目（按量缴费 比如水费、电费）
     * @param inNo 用户门牌id
     * @param listener 回调监听
     * */
    public void getChargeList(String inNo, Observer listener) {
        String shortUrl = "Bill/GetChargeList";
        JSONObject json = new JSONObject();
        json.put("InNo", inNo);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 根据用户门牌ID获取临时缴费账单（按月按户缴费，比如物业费、垃圾清理费）
     * @param inNo 用户门牌id
     * @param listener 回调监听
     */
    public void getNormalPay(String inNo, Observer listener) {
        String shortUrl = "Bill/GetNormalPay";
        JSONObject json = new JSONObject();
        json.put("InNo", inNo);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }

    /**
     * 根据用户门牌ID获取上次预交费信息（按月）
     * @param inNo 用户门牌id
     * @param listener 回调监听
     */
    public void getLastPrePay(String inNo, Observer listener) {
        String shortUrl = "Bill/GetLastPrePay";
        JSONObject json = new JSONObject();
        json.put("InNo", inNo);
        withToken(shortUrl, json.toJSONString(), listener, RType.JSONArray);
    }
}
