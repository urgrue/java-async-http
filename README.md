# Java Async HTTP Client
A simple, lightweight, asynchronous HTTP client built on top of Java's `HttpURLConnection`.

This project is actively being improved and major changes may break backwards compatibility.

Please feel free to report any issues.

## Basics

Simply create either an `AsyncHttpClient` (asynchronous) or `SyncHttpClient` (synchronous) instance and make requests through it with the `get()`, `post()`, `put()`, `delete()`, or `head()` methods.
Responses are handled by callbacks through `HttpResponseHandler` usually created as an anonymous inner class of the function call.


#### Example

```java
String url = "https://api.twitch.tv/kraken/games/top";

// Set the GET parameters
RequestParams params = new RequestParams();
params.put("limit", 1);
params.put("offset", 0);

HttpClient client = new AsyncHttpClient();
client.setHeader("Accept", "application/vnd.twitchtv.v3+json"); // Optional: send custom headers
client.setUserAgent("my-java-application"); // Optional: set a custom user-agent

client.get(url, params, new HttpResponseHandler() {
    @Override
    public void onSuccess(HttpResponse response) {
        // Successful response from the server
        System.out.println(response.getStatusCode());
        System.out.println(response.getStatusMessage());
        System.out.println(response.getContent());
    }

    @Override
    public void onFailure(HttpResponse response) {
        // The server returned an error (4xx/5xx status code)
        System.out.println(response.getUrl());
        System.out.println(response.getStatusCode());
        System.out.println(response.getStatusMessage());
    }

    @Override
    public void onFailure(Throwable throwable) {
        // Something went wrong with the request and an exception was thrown
        throwable.printStackTrace();
    }
});
```

#### RequestParams

The `RequestParams` object is used to specify the HTTP request parameters such as for GET or POST. GET parameters are automatically appended to the URL and POST parameters will be x-www-form-urlencoded and sent in the content body.

## Download
* [v1.0.0 jar](https://github.com/mb3364/java-async-http/releases/tag/v1.0.0)

## Roadmap

* Allow file uploads with form data.
* More control over setting Content-Type.
* Handle cookies.