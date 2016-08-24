package com.wfzcx.ieos.data.service;

import com.jude.utils.JUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/3/2.
 */
public class ErrorTransform<T> implements Observable.Transformer<T, T> {

    private Action1<Throwable> handler;

    public ErrorTransform() {
        this.handler = new ServerErrorHandler();
    }

    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable
                .doOnError(handler)
                .onErrorResumeNext(Observable.empty());
    }


    public static class ServerErrorHandler implements Action1<Throwable> {

        @Override
        public void call(Throwable throwable) {

            JUtils.Log("Error:" + throwable.getClass().getName() + ":" + throwable.getMessage());
            String errorString;
            if (throwable instanceof HttpException) {
                HttpException err = (HttpException) throwable;
                if (err.code() >= 400 && err.code() < 500) {
                    try {
                        JSONObject jsonObject = new JSONObject(err.response().errorBody().string());
                        errorString = jsonObject.getString("error");
                    } catch (JSONException | IOException e) {
                        errorString = "未知错误:" + err.code() + err.message();
                    }
                } else if (err.code() >= 500) {
                    errorString = "服务器错误";
                } else {
                    errorString = "请求错误:" + err.code();
                }
            } else {
                errorString = "网络错误";
            }
            JUtils.Toast(errorString);
        }

    }

}