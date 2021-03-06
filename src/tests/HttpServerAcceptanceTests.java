package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import client.Client;
import client.Options;

import server.library.*;

public class HttpServerAcceptanceTests {
    class MockHttpRequestObserver implements HttpRequestObserver {
        private HttpResponse fakeResponse;
        public MockHttpRequestObserver(HttpResponse fakeResponse) {
            this.fakeResponse = fakeResponse;
        }

        @Override
        public HttpResponse onRequest(HttpRequest request) {
            return fakeResponse;
        }

    }
    class TestServer implements Runnable {
        private HttpRequestObserver observer;
        public TestServer(HttpRequestObserver observer) {
            this.observer = observer;
        }

        @Override
        public void run() {
            try {
                new HttpServer(80, observer).start();
            } catch(IOException e) {
                System.out.println("Failed to start test server");
            }
        }
    }

    @Test
    public void test() {
        HttpResponse fakeResponse = new HttpResponse(HttpVersion.OnePointOh, Status.OK);
        MockHttpRequestObserver observer = new MockHttpRequestObserver(fakeResponse);
        TestServer server = new TestServer(observer);
        Thread t = new Thread(server);
        t.start();

        Options options = new Options();
        options.method = "get";
        options.url = "http://127.0.0.1/";
        options.verbose = true;
        String response = new Client(options).sendRequest();

        assertTrue(response.startsWith("HTTP/1.0 200 OK"));
    }
}