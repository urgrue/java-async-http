# Java Async HTTP Client
A simple, lightweight, asynchronous HTTP client built on top of Java's `HttpURLConnection`.

This project is actively being improved and major changes may break backwards compatibility.

Please feel free to report any issues.

## Basics

Simply create either an `AsyncHttpClient` (asynchronous) or `SyncHttpClient` (synchronous) instance and make requests through it with the `get()`, `post()`, `put()`, `delete()`, or `head()` methods.
Responses are handled by callbacks through `HttpResponseHandler` usually created as an anonymous inner class of the function call.


#### Examples

```java
String url = "https://api.twitch.tv/kraken/games/top";

// Set the GET parameters
RequestParams params = new RequestParams();
params.put("limit", 1);
params.put("offset", 0);

HttpClient client = new AsyncHttpClient();
client.setHeader("Accept", "application/vnd.twitchtv.v3+json"); // Optional: send custom headers; sent with all future requests
client.setUserAgent("my-java-application"); // Optional: set a custom user-agent

client.get(url, params, new StringHttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
        /* Request was successful */
    }

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
        /* Server responded with a status code 4xx or 5xx error */
    }

    @Override
    public void onFailure(Throwable throwable) {
        /* An exception occurred during the request. Usually unable to connect or there was an error reading the response */
    }
});
```

The above example reads String responses using `StringHttpResponseHandler`. It will automatically read the response encoding and encode the String for you.

For raw data in an array of bytes, you may use `HttpResponseHandler`,

```java
client.get(url, params, new HttpResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content) {
        /* Request was successful */
    }

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content) {
        /* Server responded with a status code 4xx or 5xx error */
    }

    @Override
    public void onFailure(Throwable throwable) {
        /* An exception occurred during the request. Usually unable to connect or there was an error reading the response */
    }
});
```

Downloading files is as easy as using a `FileHttpResponseHandler`:

```java
String url = "https://example.org/cool-file.zip";
File file = new File("C:\\cool-file.zip"); // Save path
HttpClient client = new SyncHttpClient();

client.get(url, new FileHttpResponseHandler(file) {
    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, File content) {
        /* Request was successful */
    }

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, File content) {
        /* Server responded with a status code 4xx or 5xx error */
    }

    @Override
    public void onFailure(Throwable throwable) {
        /* An exception occurred during the request. Usually unable to connect or there was an error reading the response */
    }

    @Override
    public void onProgressChanged(long bytesReceived, long totalBytes) {
        /* Optional: Track download progress. Will be called several times during file download */
        System.out.println("Downloaded: " + bytesReceived + " / " + totalBytes);
    }
});
```

#### RequestParams

The `RequestParams` object is used to specify the HTTP request parameters such as for GET or POST. GET parameters are automatically appended to the URL and POST parameters will be x-www-form-urlencoded and sent in the content body.

## Limitations

* Currently only able to send basic form data in UTF-8. Uploading files is not yet supported.

## Download

* [v1.2.0 jar](https://github.com/mb3364/java-async-http/releases/tag/v1.2.0)

## Thanks

Outward API heavily inspired by [android-async-http](https://github.com/loopj/android-async-http)

## Roadmap

* Full JavaDocs
* Allow file uploads with form data.
* More control over setting Content-Type.
* Handle cookies.