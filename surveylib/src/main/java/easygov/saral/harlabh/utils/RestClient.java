package easygov.saral.harlabh.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.models.responsemodels.translationmodel.TranslationModel;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by apoorv on 20-02-2017.
 */

public class RestClient {
    private static HarlabhApi REST_CLIENT;
    private static Retrofit retrofit;
    static {setupRestClient();
    }

    private static Prefs mPrefs;
    private RestClient() {}

    public static HarlabhApi get() {
        return REST_CLIENT;
    }


    public static Retrofit retrofit() {
        return retrofit;
    }

    private static void setupRestClient() {
        mPrefs=Prefs.with(SplashActivity.inst);
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request_object = chain.request().newBuilder()
                        .header("Authorization", mPrefs.getString(Constants.Auth,""))
                        .addHeader("lati",String.valueOf(MyApplication.latitude))
                        .addHeader("longi",String.valueOf(MyApplication.longitude))
                        .build();

                okhttp3.Response response = chain.proceed(request_object);
                if(response.code() == 200) {
                    try {
                        String jsonresponse = response.body().string();
                        MediaType contentType = response.body().contentType();
                        String language_code = mPrefs.getString(Constants.Language,"en");
                        if(!(language_code.equals("en"))) {
                            ResponseManager responseManager = new ResponseManager(true, language_code);
                            responseManager.raw_response = jsonresponse;
                            responseManager.response_with_keys_changed = responseManager.createHashMapFromJsonString(responseManager.raw_response, "");
                            if (responseManager.list_of_translation_required.size() > 0) {
                                String hash_values = new Gson().toJson(responseManager.list_of_translation_required);
                                MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                                String json = "{\"language\":\""  + responseManager.language + "\"," +
                                        "\"hashvalues\":" + hash_values + "}";

                                RequestBody request_body = RequestBody.create(MEDIA_TYPE_JSON, json);

                                Request request_object_for_translations = new Request.Builder()
                                        .url(Constants.BASE_URL + Constants.translation_url)
                                        .header("Authorization", mPrefs.getString(Constants.Auth, ""))
                                        .post(request_body).build();

                                okhttp3.Response response_object_translation = chain.proceed(request_object_for_translations);
                                if (response_object_translation.code() == 200) {
                                    try {
                                        String translated_text = response_object_translation.body().string();
                                        TranslationModel translation_model = new Gson().fromJson(translated_text, TranslationModel.class);
                                        responseManager.call_translation_api = false;
                                        responseManager.response_with_translation = translation_model.data;

                                        String response_with_keys_changed_json = new Gson().toJson(responseManager.response_with_keys_changed);
                                        HashMap<String, Object> translated_response = responseManager.createHashMapFromJsonString(response_with_keys_changed_json, "");

                                        jsonresponse =  new Gson().toJson(translated_response);

                                        translated_response = null;
                                        response_with_keys_changed_json = null;
                                        translated_text = null;
                                        responseManager = null;
                                        translation_model = null;
                                        hash_values = null;

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }

                        ResponseBody body = ResponseBody.create(contentType, jsonresponse);

                        return response.newBuilder().body(body).build();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(response.code() == 403) {

                }
                return response;

//                return chain.proceed(newRequest);
            }
        };




        OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).connectTimeout(600, TimeUnit.SECONDS);
        builder.interceptors().add(interceptor);

        // HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
       // builder.addInterceptor(logging);
// todo : add library compile 'com.squareup.okhttp3:logging-interceptor:3.8.0'


        OkHttpClient client = builder.build();



        /*retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .build();*/

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .build();


        REST_CLIENT = retrofit.create(HarlabhApi.class);
    }

}
