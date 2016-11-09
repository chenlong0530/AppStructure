package com.app.library.net;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Base OKHttp Request.
 * @param <T>
 */
public abstract class BaseOkHttpRequest<T> extends BaseRequest<T> {

    private okhttp3.Call mCall;

    public BaseOkHttpRequest(Class<T> clazz, IRequestCallBack<T> callback) {
        super(clazz, callback);
    }

    private Request buildGetRequest() {
        return new Request.Builder()
                .headers(Headers.of(mHeaders))
                .url(mUrl)
                .tag(mTag)
                .build();
    }

    private Request buildFormRequest() {
        FormBody.Builder formBuilder = new FormBody.Builder();

        Iterator<Map.Entry<String, String>> iterator = mFields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        return new Request.Builder()
                .headers(Headers.of(mHeaders))
                .url(mUrl)
                .post(formBuilder.build())
                .tag(mTag)
                .build();
    }

    private Request buildMultiRequest() {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();

        multipartBuilder.setType(MultipartBody.FORM);

        Iterator<Part> iteratorPart = mParts.iterator();
        while (iteratorPart.hasNext()) {
            Part part = iteratorPart.next();
            multipartBuilder.addFormDataPart(part.name, part.value);
        }

        Iterator<FilePart> iteratorFilePart = mFileParts.iterator();
        while (iteratorFilePart.hasNext()) {
            FilePart filePart = iteratorFilePart.next();
            multipartBuilder.addFormDataPart(filePart.name, filePart.filename,
                    RequestBody.create(MediaType.parse(filePart.mime), filePart.file));
        }

        return new Request.Builder()
                .headers(Headers.of(mHeaders))
                .url(mUrl)
                .post(multipartBuilder.build())
                .tag(mTag)
                .build();
    }

    @Override
    protected void doExec() {
        try {
            Request request;

            if (mMethod.equalsIgnoreCase(GET)) {
                request = buildGetRequest();
            } else if (mMethod.equalsIgnoreCase(POST)) {
                if (isMultipart()) {
                    request = buildMultiRequest();
                } else {
                    request = buildFormRequest();
                }
            } else {
                throw new IllegalArgumentException("req method not support");
            }

            mCall = OkHttpUtils
                    .adapterClient(mProxyAddress, mConnectTimeout, mSocketTimeout)
                    .newCall(request);

            Response response = mCall.execute();

            if (response.isSuccessful()) {
                if (DEBUG) {
                    Headers responseHeaders = response.headers();
                    logDebug(TAG, TAG_CHILD + " response sequence[" + mSequence + "], "
                            + "tag:" + mTag + ", "
                            + "headers:" + responseHeaders.toString());
                }

                if (mCallBack != null) {
                    String body = response.body().string();

                    if (DEBUG) {
                        try {
                            printJson(new JSONObject(body));
                        } catch (Exception e) {
                            logWarn(TAG, TAG_CHILD + " log response data exception occurs");
                        }
                    }

                    T t = new Gson().fromJson(body, mClazz);
                    setHiddenBody(t, body);
                    mCallBack.onResponse(new BaseStatus(response.code(), response.message(), null), t);
                }
            } else {
                if (DEBUG) {
                    logWarn(TAG, TAG_CHILD + " exec exception occurs, " + response.code());
                }

                if (mCallBack != null) {
                    mCallBack.onResponse(new BaseStatus(response.code(), response.message(), new ErrorCodeException(response.code())), null);
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                logWarn(TAG, TAG_CHILD + " exec exception occurs, " + e.getMessage());
            }

            if (mCallBack != null) {
                mCallBack.onResponse(new BaseStatus(-1, e.getMessage(), e), null);
            }
        }
    }

    private static final int CHUNK_SIZE = 3000;

    private void printJson(JSONObject jsonObject) {
        String message = null;
        try {
            message = jsonObject.toString(2);
        } catch (Exception ignored) {
        }

        if (message == null || message.length() == 0) return;
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logDebug(TAG, TAG_CHILD + " response sequence[" + mSequence + "], "
                    + "tag:" + mTag + ", "
                    + "data:" + message);
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            logDebug(TAG, TAG_CHILD + " response sequence[" + mSequence + "], "
                    + "tag:" + mTag + ", "
                    + "data:" + new String(bytes, i, count));
        }
    }

    @Override
    public void doCancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private void setHiddenBody(T t, String body) {
        Field field;
        try {
            // 暂定方案：仅在需要原始body数据时，才通过反射成员变量赋值
            field = t.getClass().getField("hiddenBody");
            field.setAccessible(true);
            field.set(t, body);
        } catch (Exception ignored) {
        }
    }
}
