package com.app.library.net;

import android.text.TextUtils;

import com.app.library.net.annotation.RequestParameter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenlong on 16/10/18.
 */

public abstract class BaseRequest<T> {
    // METHOD
    protected static final String GET = "GET";
    protected static final String POST = "POST";

    protected Class<T> clazz;
    protected Map<String, String> mHeader;
    protected Map<String, String> mQueries;
    protected Map<String, String> mFields;
    protected IRequestCallBack<T> mCallBack;
    protected String mMethod;

    protected abstract void doExec();

    protected abstract void doCancel();

    protected void init() {
        mHeader = new HashMap<>();
        mQueries = new HashMap<>();
        mFields = new HashMap<>();
    }


    public BaseRequest(Class<T> clazz, IRequestCallBack<T> callBack) {
        this.clazz = clazz;
        this.mCallBack = callBack;
    }

    protected void buildHeaderAndParams() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(RequestParameter.class)) {
                RequestParameter parameter = f.getAnnotation(RequestParameter.class);
                if (parameter.method() == RequestParameter.METHOD.GET) {
                    try {
                        if (!TextUtils.isEmpty(parameter.name())) {
                            addQuery(parameter.name(), (String) f.get(this));
                        } else {
                            addQuery(f.getName(), (String) f.get(this));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (parameter.method() == RequestParameter.METHOD.POST) {
                    try {
                        if (!TextUtils.isEmpty(parameter.name())) {
                            addField(parameter.name(), (String) f.get(this));
                        } else {
                            addField(f.getName(), (String) f.get(this));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addHead(String key, String value) {
        if (key != null && value != null) {
            mHeader.put(key, value);
        }
    }

    public void addQuery(String key, String value) {
        if (key != null && value != null) {
            mQueries.put(key, value);
        }
    }

    public void addField(String key, String value) {
        if (key != null && value != null) {
            mFields.put(key, value);
        }
    }
}
