package com.kswy.property.server.retrofit;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.kswy.property.bean.User;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    public static String URL_BASE = "http://39.106.118.220:8088/";

    private RetrofitService retrofitService;
    public static ApiManager instance;

    public static ApiManager getInstance() {
        if (instance == null) {
            synchronized (ApiManager.class) {
                if (instance == null) {
                    instance = new ApiManager();
                }
            }
        }

        return instance;
    }

    private ApiManager() {
        initRetrofitService();
    }

    private void initRetrofitService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new MyOkhttpInterceptor())
                .addNetworkInterceptor(new MyOkhttpInterceptor())
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
//                .authenticator(new Authenticator() {
//                    @Nullable
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {
//                        if (responseCount(response) > 2) {
//                            return null;
//                        }
//                        return response.request().newBuilder().header("Authorization", User.getUser().getToken()).build();
//                    }
//                })
                .retryOnConnectionFailure(false)
                .build();
        if (retrofitService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(client)
                    .addConverterFactory(NullOrEmptyConvertFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            retrofitService = retrofit.create(RetrofitService.class);
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    public RetrofitService getRetrofitService() {

        return retrofitService;
    }

    private class MyOkhttpInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.e("apiManager", " request:"+ request);
            Response response = chain.proceed(request);

            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();

            if (!bodyEncoded(response.headers())) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    return response;
                }

                if (contentLength != 0) {
                    String result = buffer.clone().readString(charset);
                    Log.e("apiManager", " response.url():"+ response.request().url());
                    Log.e("apiManager", " response.body():"+ result);
                    //得到所需的string，开始判断是否异常
                    //***********************do something*****************************

                }

            }

            return response;
        }
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
