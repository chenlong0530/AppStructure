package com.app.library.net;

import android.text.TextUtils;

import com.app.library.lg.Lg;
import com.app.library.net.annotation.RequestParameter;

import java.io.File;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base Request.
 * @param <T>
 */
public abstract class BaseRequest<T> {

    // METHOD
    protected static final String GET = "GET";
    protected static final String POST = "POST";

    protected static AtomicInteger mAtomicInteger = new AtomicInteger();

    protected static boolean DEBUG = true;
    protected static final String TAG = "pa_http";
    protected final String TAG_CHILD = this.getClass().getSimpleName();

    protected Class<T> mClazz;
    protected IRequestCallBack<T> mCallBack;

    protected int mSequence;
    protected String mMethod;
    protected String mUrl;
    protected String mTag;

    protected Map<String, String> mHeaders;
    protected Map<String, String> mQueries;
    protected Map<String, String> mFields;

    protected List<Part> mParts;
    protected List<FilePart> mFileParts;

    protected SocketAddress mProxyAddress;
    protected int mConnectTimeout;
    protected int mSocketTimeout;

    protected abstract String getReqMethod();

    protected abstract String getReqUrl();

    protected SocketAddress getProxyAddress() {
        return null;
    }

    protected int getConnectTimeout() {
        return 30 * 1000;
    }

    protected int getSocketTimeout() {
        return 30 * 1000;
    }

    /**
     * build request header and params
     *
     * @see #addHead(String, String) addHead
     * @see #addHeads(Map)  addHeads
     * @see #addQuery(String, String) addQuery
     * @see #addField(String, String) addField
     * @see #addPart(String, String) addPart
     * @see #addFilePart(String, String, String, File) addFilePart
     */
    protected void buildReqHeaderAndParams() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(RequestParameter.class)) {
                RequestParameter rp = f.getAnnotation(RequestParameter.class);
                if (rp.method() == RequestParameter.METHOD.GET) {
                    try {
                        if (!TextUtils.isEmpty(rp.name())) {
                            addQuery(rp.name(), (String) f.get(this));
                        } else {
                            addQuery(f.getName(), (String) f.get(this));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (rp.method() == RequestParameter.METHOD.POST) {
                    try {
                        if (!TextUtils.isEmpty(rp.name())) {
                            addField(rp.name(), (String) f.get(this));
                        } else {
                            addField(f.getName(), (String) f.get(this));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public BaseRequest(Class<T> clazz, IRequestCallBack<T> callback) {
        mClazz = clazz;
        mCallBack = callback;

        init();
    }

    private void init() {
        mSequence = mAtomicInteger.incrementAndGet();
        mMethod = "GET";
        mUrl = "";
        mHeaders = new HashMap<String, String>();
        mQueries = new HashMap<String, String>();
        mFields = new HashMap<String, String>();
        mParts = new ArrayList<Part>();
        mFileParts = new ArrayList<FilePart>();
    }

    public void exec() {
        preExec();

        if (DEBUG) {
            logDebug(TAG, TAG_CHILD + " request sequence:[" + mSequence + "], "
                    + "tag:" + mTag + ", "
                    + mMethod + ":" + mUrl + ", "
                    + "headers:" + mHeaders.toString() + ", "
                    + "proxy:" + (mProxyAddress == null ? "no" : mProxyAddress.toString()) + ", "
                    + "connect timeout:" + mConnectTimeout + ", "
                    + "socket timeout:" + mSocketTimeout + ", "
                    + "fields:" + mFields.toString());
        }

        doExec();
    }

    private void preExec() {
        mMethod = getReqMethod();

        if (TextUtils.isEmpty(mMethod)) {
            throw new IllegalArgumentException("req method is illegal");
        }

        setDefaultReqHeader();
        buildReqHeaderAndParams();

        String url = getReqUrl();

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("req url is illegal");
        }

        if (url.toLowerCase().startsWith("http")) {
            mUrl = url;
        } else {
            String baseUrl = getReqBaseUrl();

            if (TextUtils.isEmpty(baseUrl)) {
                throw new IllegalArgumentException("req url is illegal");
            }

            mUrl = baseUrl + url;
        }

        // add queries
        UrlParams urlParams = new UrlParams(mQueries);
        mUrl = mUrl + urlParams.toString();

        mProxyAddress = getProxyAddress();
        mConnectTimeout = getConnectTimeout();
        mSocketTimeout = getSocketTimeout();
    }

    protected abstract void doExec();

    public void cancel() {
        doCancel();
    }

    protected abstract void doCancel();

    protected String getReqBaseUrl() {
        return "";
    }

    protected void setDefaultReqHeader() {

    }

    public void addHead(String key, String value) {
        if (key != null && value != null) {
            mHeaders.put(key, value);
        }
    }

    public void addHeads(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            addHead(entry.getKey(), entry.getValue());
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

    public void tag(String tag) {
        mTag = tag;
    }

    protected boolean isMultipart() {
        return mParts.size() > 0 || mFileParts.size() > 0;
    }

    protected class Part {

        public String name;
        public String value;

        public Part(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    protected class FilePart {

        public String name;
        public String filename;
        public String mime;
        public File file;

        public FilePart(String name, String filename, String mime, File file) {
            this.name = name;
            this.filename = filename;
            this.mime = mime;
            this.file = file;
        }
    }

    public void addPart(String name, String value) {
        if (name != null && value != null) {
            mParts.add(new Part(name, value));
        }
    }

    public void addFilePart(String name, String filename, String mime, File file) {
        if (file != null) {
            mFileParts.add(new FilePart(name, filename, mime, file));
        }
    }

    protected void logDebug(String tag, String message) {
        Lg.d(tag + ":" + message);
    }

    protected void logWarn(String tag, String message) {
        Lg.w(tag + ":" + message, null);
    }
}
