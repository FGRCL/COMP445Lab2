package server.library;

import java.util.LinkedList;
import java.util.List;

public class HttpResponse{
    private Status status;
    private HttpVersion version;
    private List<String> headers;
    private String body;

    public HttpResponse(HttpVersion version, Status status) {
        this.status = status;
        this.version = version;
        this.headers = new LinkedList<>();
    }
    
    public HttpResponse(HttpVersion version, Status status, String body) {
        this.status = status;
        this.version = version;
        this.headers = new LinkedList<>();
        this.body = body;//dad bods are hot
    }

    public void addHeader(String key, String value) {
        headers.add(key + ": " + value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        switch(version) {
            case OnePointOh:
                builder.append("HTTP/1.0 ");
                break;
            case OnePointOne:
                builder.append("HTTP/1.1 ");
                break;
        }

        switch(status) {
            case OK:
                builder.append("200 OK");
                break;
            case BAD_REQUEST:
                builder.append("400 BAD REQUEST");
                break;
            case NOT_FOUND:
                builder.append("404 NOT FOUND");
                break;
            case INTERNAL_SERVER_ERROR:
                builder.append("500 INTERNAL SERVER ERROR");
                break;
            case FORBIDDEN:
            	builder.append("403 FORBIDDEN");
            	break;
            case CREATED:
            	builder.append("201 CREATED");
            	break;
        }

        builder.append("\r\n");
        for(String s: headers) {
            builder.append(s);
            builder.append("\r\n");
        }

        builder.append("\r\n");
        if(body != null) {
            builder.append(body);
        }

        return builder.toString();
    }
}