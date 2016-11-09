package com.app.library.net;

/**
 * RxJava Request Callbacks.
 * Created by hexiaohong on 16/11/8.
 */
public class RxRequestCallbacks {

    /**
     * 请求正常，onNext->onComplete；
     * 请求异常，onError
     *
     * @param subscriber subscriber
     * @param <T>        response
     * @return IRequestCallBack
     */
    public static <T> IRequestCallBack<T> simple(final rx.Subscriber<? super T> subscriber) {
        return new SimpleRequestCallback<>(subscriber);
    }

    /**
     * 请求正常，onNext(response不为空)->onComplete；
     * 请求异常，onError
     *
     * @param subscriber subscriber
     * @param <T>        response
     * @return IRequestCallBack
     */
    public static <T> IRequestCallBack<T> noEmpty(final rx.Subscriber<? super T> subscriber) {
        return new NoEmptyRequestCallback<>(subscriber);
    }

    /**
     * 请求正常，onNext->onComplete；
     * 请求异常，onComplete
     *
     * @param subscriber subscriber
     * @param <T>        response
     * @return IRequestCallBack
     */
    public static <T> IRequestCallBack<T> noError(final rx.Subscriber<? super T> subscriber) {
        return new NoErrorRequestCallback<>(subscriber);
    }

    /**
     * 请求正常，onNext(response不为空)->onComplete；
     * 请求异常，onComplete
     *
     * @param subscriber subscriber
     * @param <T>        response
     * @return IRequestCallBack
     */
    public static <T> IRequestCallBack<T> noErrorNoEmpty(final rx.Subscriber<? super T> subscriber) {
        return new NoErrorNoEmptyRequestCallback<>(subscriber);
    }

    private static abstract class BaseRequestCallback<T> implements IRequestCallBack<T> {

        final rx.Subscriber<? super T> subscriber;

        BaseRequestCallback(final rx.Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        boolean isUnsubscribed() {
            return subscriber.isUnsubscribed();
        }

        void onNext(T t) {
            subscriber.onNext(t);
        }

        void onCompleted() {
            subscriber.onCompleted();
        }

        void onError(Throwable throwable) {
            if (throwable != null) {
                subscriber.onError(throwable);
            } else {
                subscriber.onError(new Exception("throwable is null."));
            }
        }
    }

    public static class SimpleRequestCallback<T> extends BaseRequestCallback<T> {

        public SimpleRequestCallback(final rx.Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public void onResponse(BaseStatus status, T response) {
            if (!isUnsubscribed()) {
                if (status.isSuccessful()) {
                    onNext(response);
                    onCompleted();
                } else {
                    onError(status.getThrowable());
                }
            }
        }
    }

    public static class NoEmptyRequestCallback<T> extends BaseRequestCallback<T> {

        public NoEmptyRequestCallback(final rx.Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public void onResponse(BaseStatus status, T response) {
            if (!isUnsubscribed()) {
                if (status.isSuccessful() && response != null) {
                    onNext(response);
                    onCompleted();
                } else {
                    onError(status.getThrowable());
                }
            }
        }
    }

    public static class NoErrorRequestCallback<T> extends BaseRequestCallback<T> {

        public NoErrorRequestCallback(final rx.Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public void onResponse(BaseStatus status, T response) {
            if (!isUnsubscribed()) {
                if (status.isSuccessful()) {
                    onNext(response);
                }
                onCompleted();
            }
        }
    }

    public static class NoErrorNoEmptyRequestCallback<T> extends BaseRequestCallback<T> {

        public NoErrorNoEmptyRequestCallback(final rx.Subscriber<? super T> subscriber) {
            super(subscriber);
        }

        @Override
        public void onResponse(BaseStatus status, T response) {
            if (!isUnsubscribed()) {
                if (status.isSuccessful() && response != null) {
                    onNext(response);
                }
                onCompleted();
            }
        }
    }
}
