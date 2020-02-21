package server;

import java.io.IOException;

public class Httpfs {

    public static void main(String[] args) {
        try {
            new PrintServer().start();
        } catch(IOException e) {
            System.err.println("Could not open the server: " + e);
            System.exit(1);
        }
    }

}
