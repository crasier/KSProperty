package com.kswy.property.server.retrofit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitService {

    /**
     * 登录，获取到token认为登录成功，否则登录失败
     * */
    @FormUrlEncoded
    @POST("GetToken")
    public Observable<JSONObject> login(@Field("userId") String userId, @Field("password") String password);

//    @FormUrlEncoded
    @POST
    @Headers({"Content-Type: application/json","Accept: application/json"})
    /**
     * 请求的返回结果是jsonobject
     * */
    public Observable<JSONObject> requestObj(@Header("Authorization") String token, @Url String url, @Body RequestBody body);

//    @FormUrlEncoded
    @POST
    @Headers({"Content-Type: application/json","Accept: application/json"})
    /**
     * 请求的返回结果是jsonarray
     * */
    public Observable<JSONArray> requestArr(@Header("Authorization") String token, @Url String url, @Body RequestBody body);


//    @FormUrlEncoded
    @POST
    @Headers({"Content-Type: application/json","Accept: application/json"})
    /**
     * 请求的返回结果是String
     * */
    public Observable<String> requestStr(@Header("Authorization") String token, @Url String url, @Body RequestBody body);
//    @FormUrlEncoded
    @POST
    @Headers({"Content-Type: application/json","Accept: application/json"})
    /**
     * 请求的返回结果是String
     * */
    public Observable<Boolean> requestBool(@Header("Authorization") String token, @Url String url, @Body RequestBody body);

    @POST
    @Headers({"Content-Type: application/json","Accept: application/json"})
    /**
     * 请求的返回结果没有响应body，只有header
     * */
    public Observable<Response<Void>> requestNoBody(@Header("Authorization") String token, @Url String url, @Body RequestBody body);
}
