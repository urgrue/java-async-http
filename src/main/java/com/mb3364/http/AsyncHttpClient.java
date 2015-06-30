package com.mb3364.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client for making asynchronous HTTP requests.
 *
 * @author Matthew Bell
 * @see SyncHttpClient
 */
public class AsyncHttpClient extends HttpClient {

    private final ExecutorService threadPool;

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * Make a asynchronous HTTP DELETE request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #delete(String, RequestParams, HttpResponseHandler)
     */
    @Override
    public void delete(final String url, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.delete(url, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP DELETE request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #delete(String, HttpResponseHandler)
     */
    @Override
    public void delete(final String url, final RequestParams params, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.delete(url, params, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP GET request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #get(String, RequestParams, HttpResponseHandler)
     */
    @Override
    public void get(final String url, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.get(url, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP GET request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #get(String, HttpResponseHandler)
     */
    @Override
    public void get(final String url, final RequestParams params, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.get(url, params, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP HEAD request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #head(String, RequestParams, HttpResponseHandler)
     */
    @Override
    public void head(final String url, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.head(url, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP HEAD request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #head(String, HttpResponseHandler)
     */
    @Override
    public void head(final String url, final RequestParams params, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.head(url, params, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP POST request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #post(String, RequestParams, HttpResponseHandler)
     */
    @Override
    public void post(final String url, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.post(url, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP POST request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #post(String, HttpResponseHandler)
     */
    @Override
    public void post(final String url, final RequestParams params, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.post(url, params, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP PUT request.
     *
     * @param url     the URL of the resource to request
     * @param handler the response handler
     * @see #put(String, RequestParams, HttpResponseHandler)
     */
    @Override
    public void put(final String url, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.put(url, handler);
            }
        });
    }

    /**
     * Make a asynchronous HTTP PUT request with parameters.
     *
     * @param url     the URL of the resource to request
     * @param params  the parameters to send with the request
     * @param handler the response handler
     * @see #put(String, HttpResponseHandler)
     */
    @Override
    public void put(final String url, final RequestParams params, final HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.put(url, params, handler);
            }
        });
    }
}
