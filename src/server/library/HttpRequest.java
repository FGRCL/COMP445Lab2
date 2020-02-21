package server.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.InputMismatchException;

public class HttpRequest {
    private Method method;
    private String uri;
    private HttpVersion version;
    private final Hashtable<String, String> headers;
    private final String body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final String firstLine = reader.readLine();
        parseRequestLine(firstLine);

        headers = new Hashtable<>();
        String line = reader.readLine();
        while(!line.isEmpty()) {
            addHeader(line);
        }

        if(!headers.containsKey("Content-Length")) {
            body = "";
            return;
        }

        final StringBuilder body = new StringBuilder();

        line = reader.readLine();
        while(!line.isEmpty()) {
            body.append(line);
        }

        this.body = body.toString();
    }

    private void parseRequestLine(final String line) {
        final String[] requestLine = line.split(" ");
        if (requestLine.length != 3)
            throw new InputMismatchException("Line cannot be split into 3: <" + line + ">");

        final String method = requestLine[0];
        switch (method) {
            case "GET":
                this.method = Method.GET;
                break;
            case "POST":
                this.method = Method.POST;
                break;
            default:
                throw new InputMismatchException("Incorrect method: " + method);
        }

        uri = requestLine[1];

        final String version = requestLine[2];
        switch (version) {
            case "HTTP/1.0":
                this.version = HttpVersion.OnePointOh;
                break;
            case "HTTP/1.1":
                this.version = HttpVersion.OnePointOne;
                break;
            default:
                throw new InputMismatchException("Invalid HTTP version: " + version);
        }
    }

    private void addHeader(final String line) {
        final String[] headerLine = line.split(": ");
        if(headerLine.length != 2)
            throw new InputMismatchException("Could not split header key and value: " + line);
        headers.put(headerLine[0], headerLine[1]);
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}