package server;

public interface HttpRequestObserver {
    public HttpResponse onRequest(HttpRequest request);
}