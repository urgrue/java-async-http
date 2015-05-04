package http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient extends HttpClient {

    private final ExecutorService threadPool;

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void delete(String url, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.delete(url, handler);
            }
        });
    }

    @Override
    public void delete(String url, RequestParams params, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.delete(url, params, handler);
            }
        });
    }

    @Override
    public void get(String url, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.get(url, handler);
            }
        });
    }

    @Override
    public void get(String url, RequestParams params, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.get(url, params, handler);
            }
        });
    }

    @Override
    public void head(String url, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.head(url, handler);
            }
        });
    }

    @Override
    public void head(String url, RequestParams params, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.head(url, params, handler);
            }
        });
    }

    @Override
    public void post(String url, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.post(url, handler);
            }
        });
    }

    @Override
    public void post(String url, RequestParams params, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.post(url, params, handler);
            }
        });
    }

    @Override
    public void put(String url, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.put(url, handler);
            }
        });
    }

    @Override
    public void put(String url, RequestParams params, HttpResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient.super.put(url, params, handler);
            }
        });
    }
}
