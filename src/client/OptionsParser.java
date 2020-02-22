package client;

import java.util.InputMismatchException;

public class OptionsParser {
    public static Options parse(String[] args) {
        Options options = new Options();
        int i = 0;
        while(i < args.length) {
            String current = args[i];
            switch(current) {
                case "get":
                    validateGet(options);
                    options.method = "get";
                    break;
                case "post":
                    validatePost(options);
                    options.method = "post";
                    break;
                case "-v":
                    options.verbose = true;
                    break;
                case "-h":
                    i++;
                    String header = GetNextArgument(args, i);
                    options.addHeader(header);
                    break;
                case "-d":
                    validateInlineData(options);
                    i++;
                    String data = GetNextArgument(args, i);
                    options.inlineData = formatInlineData(data);
                    break;
                case "-f":
                    validateFilename(options);
                    i++;
                    String filename = GetNextArgument(args, i);
                    options.fileName = filename;
                    break;
                case "-o":
                    i++;
                    String outFile = GetNextArgument(args, i);
                    options.outFile = outFile;
                    break;
                default:
                    validateUrl(options);
                    options.url = args[i];
                    break;
            }
            i++;
        }
        
        return options;
    }
    
    private static String GetNextArgument(String[] args, int i) {
        if(i != args.length) {
            return args[i];
        } else {
            throw new InputMismatchException("Error: Flag should be followed by a value, not used as the last argument.");
        }
    }

    private static void validateGet(Options options) {
        if(null == options.method) {
        } else switch (options.method) {
            case "post":
                throw new InputMismatchException("Method was defined as both POST and GET");
            case "get":
                throw new InputMismatchException("Method was defined as GET twice");
            default:
                break;
        }
    }

    private static void validatePost(Options options) {
        if(null == options.method) {
        } else switch (options.method) {
            case "post":
                throw new InputMismatchException("Method was defined as POST twice");
            case "get":
                throw new InputMismatchException("Method was defined as both GET and POST");
            default:
                break;
        }
    }

    private static void validateInlineData(Options options) {
        if(options.method.equals("get"))
            throw new InputMismatchException("Inline data (-d) can be added to POST requests, not GET requests.");
    }
    
    private static String formatInlineData(String data) {
        
        return data;
    }

    private static void validateFilename(Options options) {
        if(options.method.equals("get"))
            throw new InputMismatchException("File data (-f) can be added to POST requests, not GET requests.");
    }

    private static void validateUrl(Options options) {
        if(options.url != null)
            throw new InputMismatchException("Only one URL can be entered");
    }
}
