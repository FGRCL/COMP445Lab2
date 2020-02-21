package server;

import java.io.File;

import cliparser.OptionDoesNotExistException;
import cliparser.OptionsParser;

public class HttpfsOptions {
    int port = 8080;
    boolean verbose = false;
    String directory = "files";

    public HttpfsOptions(String[] args) {
        OptionsParser parser = new OptionsParser(0, new File("help.txt"));

		parser.addOption("-v", 0, "-v Prints debugging messages.", (String[] arguments)->{
			verbose = true;
		});
		parser.addOption("-p", 1, "-p Specifies the port number that the server will listen and serve at. Default is 8080.", (String[] arguments)->{
            port = Integer.parseInt(arguments[0]);
		});
		parser.addOption("-d", 1, "-d Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application.", (String[] arguments)->{
			directory = arguments[0];
        });
        try {
            parser.parse(args);
        } catch(OptionDoesNotExistException e) {
            //noop
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getFileDirectory() {
        return directory;
    }
}