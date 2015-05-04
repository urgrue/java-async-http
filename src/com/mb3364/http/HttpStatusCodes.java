package com.mb3364.http;

/**
 * HTTP Status Code constants.
 */
public final class HttpStatusCodes {

    public static final int ACCEPTED = 202;
    public static final int BAD_GATEWAY = 502;
    public static final int BAD_METHOD = 405;
    public static final int BAD_REQUEST = 400;
    public static final int CLIENT_TIMEOUT = 408;
    public static final int CONFLICT = 409;
    public static final int CREATED = 201;
    public static final int ENTITY_TOO_LARGE = 413;
    public static final int FORBIDDEN = 403;
    public static final int GATEWAY_TIMEOUT = 504;
    public static final int GONE = 410;
    public static final int INTERNAL_ERROR = 500;
    public static final int LENGTH_REQUIRED = 411;
    public static final int MOVED_PERM = 301;
    public static final int MOVED_TEMP = 302;
    public static final int MULT_CHOICE = 300;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int NOT_AUTHORITATIVE = 203;
    public static final int NOT_FOUND = 404;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int NOT_MODIFIED = 304;
    public static final int NO_CONTENT = 204;
    public static final int OK = 200;
    public static final int PARTIAL = 206;
    public static final int PAYMENT_REQUIRED = 402;
    public static final int PRECON_FAILED = 412;
    public static final int PROXY_AUTH = 407;
    public static final int REQ_TOO_LONG = 414;
    public static final int RESET = 205;
    public static final int SEE_OTHER = 303;
    public static final int UNAUTHORIZED = 401;
    public static final int UNAVAILABLE = 503;
    public static final int UNSUPPORTED_TYPE = 415;
    public static final int USE_PROXY = 305;
    public static final int VERSION = 505;

    private HttpStatusCodes() {
    }
}
