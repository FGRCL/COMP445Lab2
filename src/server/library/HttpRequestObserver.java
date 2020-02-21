package server.library;

public interface HttpRequestObserver {
    public HttpResponse onRequest(HttpRequest request);
}