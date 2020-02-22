package client;

public class HelpMessages {
    public static String GenerateHelpMessage(String[] args) {
        if(args[0].equals("help")) {
            if(args.length == 1) {
                return HelpMessages.BasicHelpMessage;
            } else if(args[1].equals("get")) {
                return HelpMessages.GetUsageHelpMessage;
            } else if(args[1].equals("post")) {
                return HelpMessages.PostUsageHelpMessage;
            }
        }
        return "Invalid format for help message";
    }
    
    public static String BasicHelpMessage = "httpc is a curl-like application but supports HTTP protocol only.\r\n"
            + "Usage:\r\n"
            + "\thttpc command [arguments]\r\n"
            + "The commands are:\r\n"
            + "\tget\texecutes a HTTP GET request and prints the response.\r\n"
            + "\tpost\texecutes a HTTP POST request and prints the response.\r\n"
            + "\thelp\tprints this screen.\r\n"
            + "\r\n"
            + "Use \"httpc help [command]\" for more information about a command.\r\n";
    
    public static String GetUsageHelpMessage = "usage: httpc get [-v] [-h key:value] URL\r\n"
            + "\r\n"
            + "Get executes a HTTP GET request for a given URL\r\n"
            + "\r\n"
            + "\t-v\t\tPrint the detail of the response such as protocol, status, and headers.\r\n"
            + "\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.\r\n"
            + "\r\n";
    
    public static String PostUsageHelpMessage = "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\r\n"
            + "\r\n"
            + "Post executes a HTTP POST request for a given URL with inline data or from file.\r\n"
            + "\r\n"
            + "\t-v\t\tPrints the detail of the response such as protocol, status, and headers.\r\n"
            + "\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'\r\n"
            + "\t-d string\tAssociates an inline data to the body HTTP POST request.\r\n"
            + "\t-f file\tAssociates the content of a file to the body HTTP POST request.\r\n"
            + "\r\n"
            + "Either [-d] or [-f] can be used but not both.\r\n"
            + "\r\n";
}
