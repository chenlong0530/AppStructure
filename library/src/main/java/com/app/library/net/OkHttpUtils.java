package com.app.library.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * OkHttp常用封装
 * Created by hexiaohong on 16/7/28.
 */
public class OkHttpUtils {

    private static final int CONNECT_TIMEOUT = 30 * 1000;
    private static final int SOCKET_TIMEOUT = 30 * 1000;

    private volatile static OkHttpClient mOkHttpClient;

    /**
     * Gets Client.
     *
     * @return the client
     */
    public static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                mOkHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .writeTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build();
            }
        }
        return mOkHttpClient;
    }

    /**
     * Cancel all.
     */
    public static void cancelAll() {
        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().cancelAll();
        }
    }

    /**
     * Cancel.
     *
     * @param tag the tag
     */
    public static void cancel(String tag) {
        if (tag == null) return;

        if (mOkHttpClient != null) {
            for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }

            for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

    /**
     * Gets Adapter Client
     *
     * @param proxySocketAddress the proxy socket address, option
     * @param connectTimeout     the connect timeout, milliseconds, -1 ignore
     * @param socketTimeout      the socket timeout, milliseconds, -1 ignore
     * @return the adapter client
     */
    public static OkHttpClient adapterClient(SocketAddress proxySocketAddress,
                                             int connectTimeout,
                                             int socketTimeout) {

        OkHttpClient client = OkHttpUtils.getClient();

        // client clone, if need
        if (proxySocketAddress != null
                || (connectTimeout != -1 && connectTimeout != CONNECT_TIMEOUT)
                || (socketTimeout != -1 && socketTimeout != SOCKET_TIMEOUT)) {

            OkHttpClient.Builder builder = client.newBuilder();

            if (proxySocketAddress != null) {
                builder.proxy(new Proxy(Proxy.Type.HTTP, proxySocketAddress));
            }

            if (connectTimeout != -1 && connectTimeout != CONNECT_TIMEOUT) {
                builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            }

            if (socketTimeout != -1 && socketTimeout != SOCKET_TIMEOUT) {
                builder.readTimeout(socketTimeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(socketTimeout, TimeUnit.MILLISECONDS);
            }

            client = builder.build();
        }

        return client;
    }

    /**
     * Synchronous Get.
     *
     * @param url the url
     * @return the response string
     */
    public static String getSync(String url) {
        return getSync(url, null, null, null, -1, -1);
    }

    /**
     * Synchronous Get.
     *
     * @param url     the url
     * @param headers the headers, option
     * @param tag     the tag, option
     * @return the response string
     */
    public static String getSync(String url,
                                 Map<String, String> headers,
                                 String tag) {

        return getSync(url, headers, tag, null, -1, -1);
    }

    /**
     * Synchronous Get.
     *
     * @param url                the url
     * @param headers            the headers, option
     * @param tag                the tag, option
     * @param proxySocketAddress the proxy socket address, option
     * @return the response string
     */
    public static String getSync(String url,
                                 Map<String, String> headers,
                                 String tag,
                                 SocketAddress proxySocketAddress) {

        return getSync(url, headers, tag, proxySocketAddress, -1, -1);
    }

    /**
     * Synchronous Get.
     *
     * @param url                the url
     * @param headers            the headers, option
     * @param tag                the tag, option
     * @param proxySocketAddress the proxy socket address, option
     * @param connectTimeout     the connect timeout, milliseconds, -1 ignore
     * @param socketTimeout      the socket timeout, milliseconds, -1 ignore
     * @return the response string
     */
    public static String getSync(String url,
                                 Map<String, String> headers,
                                 String tag,
                                 SocketAddress proxySocketAddress,
                                 int connectTimeout,
                                 int socketTimeout) {

        Request.Builder builder = new Request.Builder();

        // url
        builder.url(url);

        // tag
        builder.tag(tag);

        // headers
        if (headers != null && headers.size() > 0) {
            builder.headers(Headers.of(headers));
        }

        // call
        try {
            Request request = builder.build();
            OkHttpClient client = adapterClient(proxySocketAddress,
                    connectTimeout,
                    socketTimeout);

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Synchronous Post.
     *
     * @param url     the url
     * @param headers the headers, option
     * @param fields  the fields
     * @param tag     the tag, option
     * @return the response string
     */
    public static String postSync(String url,
                                  Map<String, String> headers,
                                  Map<String, String> fields,
                                  String tag) {

        return postSync(url, headers, fields, tag, null, -1, -1);
    }

    /**
     * Synchronous Post.
     *
     * @param url                the url
     * @param headers            the headers, option
     * @param fields             the fields
     * @param tag                the tag, option
     * @param proxySocketAddress the proxy socket address, option
     * @return the response string
     */
    public static String postSync(String url,
                                  Map<String, String> headers,
                                  Map<String, String> fields,
                                  String tag,
                                  SocketAddress proxySocketAddress) {

        return postSync(url, headers, fields, tag, proxySocketAddress, -1, -1);
    }

    /**
     * Synchronous Post.
     *
     * @param url                the url
     * @param headers            the headers, option
     * @param fields             the fields
     * @param tag                the tag, option
     * @param proxySocketAddress the proxy socket address, option
     * @param connectTimeout     the connect timeout, milliseconds, -1 ignore
     * @param socketTimeout      the socket timeout, milliseconds, -1 ignore
     * @return the response string
     */
    public static String postSync(String url,
                                  Map<String, String> headers,
                                  Map<String, String> fields,
                                  String tag,
                                  SocketAddress proxySocketAddress,
                                  int connectTimeout,
                                  int socketTimeout) {

        Request.Builder builder = new Request.Builder();

        // url
        builder.url(url);

        // tag
        builder.tag(tag);

        // headers
        if (headers != null && headers.size() > 0) {
            builder.headers(Headers.of(headers));
        }

        // fields
        if (fields != null && fields.size() > 0) {
            FormBody.Builder formBuilder = new FormBody.Builder();

            Iterator<Map.Entry<String, String>> iterator = fields.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                formBuilder.add(entry.getKey(), entry.getValue());
            }

            builder.post(formBuilder.build());
        }

        // call
        try {
            Request request = builder.build();
            OkHttpClient client = adapterClient(proxySocketAddress,
                    connectTimeout,
                    socketTimeout);

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Synchronous Post Multipart.
     *
     * @param url                the url
     * @param headers            the headers, option
     * @param parts              the parts
     * @param mime               the mime
     * @param file               the file
     * @param tag                the tag, option
     * @param proxySocketAddress the proxy socket address, option
     * @param connectTimeout     the connect timeout, milliseconds, -1 ignore
     * @param socketTimeout      the socket timeout, milliseconds, -1 ignore
     * @return the response string
     */
    public static String multiSync(String url,
                                   Map<String, String> headers,
                                   Map<String, String> parts,
                                   String mime,
                                   File file,
                                   String tag,
                                   SocketAddress proxySocketAddress,
                                   int connectTimeout,
                                   int socketTimeout) {

        Request.Builder builder = new Request.Builder();

        // url
        builder.url(url);

        // tag
        builder.tag(tag);

        // headers
        if (headers != null && headers.size() > 0) {
            builder.headers(Headers.of(headers));
        }

        // parts
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);

        if (parts != null && parts.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = parts.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                multiBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        // file
        if (file != null) {
            String fileName = file.getName();
            String name = fileName;
            int index = name.lastIndexOf(".");
            if (index != -1) {
                name = name.substring(0, index);
            }
            multiBuilder.addFormDataPart(name, fileName,
                    RequestBody.create(MediaType.parse(mime), file));
        }

        builder.post(multiBuilder.build());

        // call
        try {
            Request request = builder.build();
            OkHttpClient client = adapterClient(proxySocketAddress,
                    connectTimeout,
                    socketTimeout);

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Asynchronous Download.
     *
     * @param url              the url
     * @param headers          the headers, option
     * @param file             the file
     * @param progressListener the progress listener, option
     * @param tag              the tag, option
     */
    public static void download(final String url,
                                final Map<String, String> headers,
                                final File file,
                                final ProgressListener progressListener,
                                final String tag) {

        Request.Builder builder = new Request.Builder();

        // url
        builder.url(url);

        // tag
        builder.tag(tag);

        // headers
        if (headers != null && headers.size() > 0) {
            builder.headers(Headers.of(headers));
        }

        // call
        try {
            Request request = builder.build();

            OkHttpClient client = adapterClient(null, -1, -1);

            // client clone, if need
            if (progressListener != null) {
                client = client.newBuilder()
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                        .build();
                            }
                        }).build();
            }

            // must async, support cancel
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // save
                    try {
                        copyStream(response.body().byteStream(), new FileOutputStream(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy Stream
     *
     * @param is the input stream
     * @param os the output stream
     */
    private static void copyStream(InputStream is, OutputStream os) {
        int len;
        byte[] buf = new byte[2048];
        try {
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The Custom Progress Response Body
     */
    public static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        /**
         * Instantiates a new Progress response body.
         *
         * @param responseBody     the response body
         * @param progressListener the progress listener
         */
        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    /**
     * The interface Progress listener.
     */
    public interface ProgressListener {
        /**
         * Update.
         *
         * @param bytesRead     the bytes read
         * @param contentLength the content length
         * @param done          the done
         */
        void update(long bytesRead, long contentLength, boolean done);
    }

    public static class ProgressRequestBody extends RequestBody {
        private final RequestBody requestBody;
        private final ProgressListener progressListener;
        private BufferedSink bufferedSink;

        public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                bufferedSink = Okio.buffer(sink(sink));
            }
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        }

        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                long bytesWrite = 0L;
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        contentLength = contentLength();
                    }
                    bytesWrite += byteCount;
                    progressListener.update(bytesWrite, contentLength, bytesWrite == contentLength);
                }
            };
        }
    }
}
