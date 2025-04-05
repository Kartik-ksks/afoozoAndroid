package in.kpis.afoozo.okhttpServcice;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kpis.afoozo.R;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.ImageCompression;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetworkManager<T> {
    private static OkHttpClient client;
    private static MediaType JSON;
    private static Context context;

    private static RequestBody body;
    private static Request request;
    private static MultipartBody.Builder requestBody;
    public static final String MEDIA_TYPE_JPG = "image/jpg";
    public static final String MEDIA_TYPE_PNG = "image/png";

    private OnCallback<T> callback;
    private Class<T> dataClass;

    public NetworkManager(Class<T> dataClass, OnCallback<T> callBack) {
        this.dataClass = dataClass;
        this.callback = callBack;
    }

    public interface OnCallback<T> {
        void onSuccess(boolean success, ResponseClass<T> responseClass, int which);

        void onFailure(boolean success, String response, int which);
    }

    public void callAPIJson(final Context context, String callType, String url, String params, String title, final boolean isShowLoader, final int which) throws UnsupportedEncodingException {
        Dialog progressDialog;
        Log.e("URL:", url);
        if (!Utils.isDeviceOnline(context)) {
            Toast.makeText(context, context.getString(R.string.no_internet_connection_available), Toast.LENGTH_LONG).show();
        } else {
            Log.e("URL --- ", url);
            if (isShowLoader) {
                Utils.progressDialog(context, "loading");
            }

            if (params != null && params.length() > 0) {
                body = RequestBody.create(JSON, params);
            }

            if (callType.equals(Constants.VAL_POST)) {
                request = new Request.Builder()
                        .header(Constants.FLD_CONTENT_TYPE, Constants.VAL_CONTENT_TYPE)
                        .header("Authorization", SharedPref.getBasicAuth(context))
                        .url(url)
                        .post(body)
                        .build();

            } else if (callType.equals(Constants.VAL_GET)) {
                request = new Request.Builder()
                        .header(Constants.FLD_CONTENT_TYPE, Constants.VAL_CONTENT_TYPE)
                        .header("Authorization", SharedPref.getBasicAuth(context))
                        .url(url)
                        .get()
                        .build();
            } else if (callType.equals(Constants.VAL_DELETE)) {
                request = new Request.Builder()
                        .header(Constants.FLD_CONTENT_TYPE, Constants.VAL_CONTENT_TYPE)
                        .header("Authorization", SharedPref.getBasicAuth(context))
                        .url(url)
                        .delete()
                        .build();
            }

//        OkHttpClient httpClient = new OkHttpClient();

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();

            httpClient.newCall(request).enqueue(new Callback() {
                Handler mainHandler = new Handler(context.getMainLooper());

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Responce", " onFailure ");
                    if (isShowLoader) {
                        Utils.dismissProgressDialog();
                    }
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onFailure(false, "", which);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();

                    Log.e("Responce", " Success " + res);
//                    callback.onSuccess(true, res, which);
                    if (isShowLoader) {
                        Utils.dismissProgressDialog();
                    }

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            successResponse(context, res, which);
                        }
                    });
                }
            });
        }
    }


    /**
     * Common function for make request to server as post
     *
     * @param url
     * @param imagePath
     */
    public void uploadFileToServer(Context mContext, String url, String imagePath, boolean isShowLoader, int which) {

        if (isShowLoader) {
            Utils.progressDialog(mContext, "loading");
        }

        ImageCompression imageCompression = new ImageCompression(mContext);

        getRequestObj();

        addFileForUploading("file", imagePath, null);

        makeRequestToServerPost(mContext, AppUrls.UPLOAD_FILE, isShowLoader, which);
    }


    /**
     * Common function for make request to server as post
     *
     * @param url
     */
    private void makeRequestToServerPost(final Context mContext, String url, final boolean isShowLoader, final int which) {
        try {

            MultipartBody multipartBody = requestBody.build();
            request = new Request.Builder()
                    .header(Constants.FLD_CONTENT_TYPE, Constants.VAL_CONTENT_TYPE)
                    .url(url)
                    .post(multipartBody)
                    .build();
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
//        OkHttpClient httpClient=new OkHttpClient();
//        httpClient.newCall(request).enqueue(callback);

            httpClient.newCall(request).enqueue(new Callback() {
                Handler mainHandler = new Handler(mContext.getMainLooper());

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Responce", " onFailure ");
                    if (isShowLoader) {
                        Utils.dismissProgressDialog();
                    }
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onFailure(false, "", which);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();

                    Log.e("Responce", " Success " + res);
//                    callback.onSuccess(true, res, which);
                    if (isShowLoader) {
                        Utils.dismissProgressDialog();
                    }

                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            successResponse(mContext, res, which);
                        }
                    });
                }
            });
        } catch (Exception e) {

        }
    }


    /**
     * This is the common function for add file to request to server
     *
     * @param key
     * @param filPath
     * @return
     */

    private static MultipartBody.Builder addFileForUploading(String key, String filPath, String mediaType) {
        File f = new File(filPath);
        if (mediaType == null) {
            mediaType = MEDIA_TYPE_JPG;
        }
        if (f.exists()) {
            // mediaType=ApiConnection.MEDIA_TYPE_UNKNOWN;
            requestBody.addFormDataPart(key, f.getName(), RequestBody.create(MediaType.parse(mediaType), f));
        }
        return requestBody;
    }

    private static MultipartBody.Builder getRequestObj() {
        requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        return requestBody;
    }

    /**
     * add key value data for request to Server
     *
     * @param key
     * @param value
     * @return
     */
    public static MultipartBody.Builder addDataToRequest(String key, String value) {
        requestBody.addFormDataPart(key, value);
        return requestBody;
    }


    private void successResponse(Context context, String s, int which) {

        ResponseClass response = new ResponseClass<T>(dataClass);
        ResponseClass responseClass = new Gson().fromJson(s, response);
        responseClass.setCompletePacket(s);

        if (responseClass.getErrorCode() == 409) {
            Utils.retryAlertDialog(context, "", context.getString(R.string.you_are_not_authorised_to_access_please_login_again),
                    "", context.getString(R.string.Ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.dismissRetryAlert();
                            Utils.logout(context);
                        }
                    });
        } else
            successResponse(responseClass, which);

    }

    private void successResponse(ResponseClass<T> responseClass, int which) {
        if (callback != null)
            callback.onSuccess(true, responseClass, which);

    }
}
