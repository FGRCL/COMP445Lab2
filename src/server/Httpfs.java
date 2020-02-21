package server;

import java.io.File;

import cliparser.OptionsParser;

public class Httpfs {

	public static void main(String[] args) {
		OptionsParser parser = new OptionsParser(0, new File("help.txt"));
		parser.addOption("-v", 0, "-v Prints debugging messages.", (String[] arguments)->{
			return;
		});
		parser.addOption("-p", 1, "-p Specifies the port number that the server will listen and serve at. Default is 8080.", (String[] arguments)->{
			return;
		});
		parser.addOption("-d", 1, "-d Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application.", (String[] arguments)->{
			return;
		});
	}

}
