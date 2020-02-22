package cliparser;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class OptionsParser {
	private HashMap<String, Option> options;
	private HashMap<String, String> helps;
	private int nbArguments;
	private String programHelpMessage;
	
	public OptionsParser(int nbArguments, String helpMessage) {
		options = new HashMap<String, Option>();
		helps = new HashMap<String, String>();
		this.nbArguments = nbArguments;
		this.programHelpMessage = helpMessage;
	}
	
	public OptionsParser(int nbArguments, File fileHelpMessage) {
		options = new HashMap<String, Option>();
		helps = new HashMap<String, String>();
		this.nbArguments = nbArguments;
		this.programHelpMessage = FileReader.getFileContent(fileHelpMessage);
	}
	
	public void addOption(String optionName, int nbArguments, String helpMessage, OptionDelegate delegate) {
		Option option = new Option(optionName, nbArguments, delegate);
		options.put(optionName, option);
		helps.put(optionName, helpMessage);
	}
	
	public void addOption(String optionName, int nbArguments, File helpMessageFile, OptionDelegate delegate) {
		addOption(optionName, nbArguments, FileReader.getFileContent(helpMessageFile), delegate);
	}
	
	public String[] parse(String[] arguments) throws OptionDoesNotExistException {
		String[] programArguments = getProgramArguments(arguments); 
		if(arguments.length > 0 && arguments[0].equals("help")){
			programArguments = arguments;
			if(arguments.length>1) {
				printOptionHelp(arguments[1]);
			}else {
				System.out.println(programHelpMessage);
			}
		}else {
			programArguments = getProgramArguments(arguments); 
			String[] programOptions = Arrays.copyOfRange(arguments, 0, arguments.length-nbArguments);
			for(int i=0; i<programOptions.length; i++) {
				Option currentOption = options.get(programOptions[i]);
				if(currentOption == null) throw new OptionDoesNotExistException("Program option \""+programOptions[i]+"\" does not exist");
				String[] optionArgs = new String[currentOption.getNbArguments()];
				for(int j=0; j<currentOption.getNbArguments(); j++) {
					optionArgs[j] = programOptions[i+j+1];
				}
				currentOption.getDelegate().optionCallback(optionArgs);
				i = i + currentOption.getNbArguments();
			}
		}
		return programArguments;
	}
	
	private String[] getProgramArguments(String[] arguments) {
		String[] programArguments = new String[nbArguments];
		for(int i=0; i<nbArguments; i++) {
			programArguments[i] = arguments[arguments.length-(i+1)];
		}
		return programArguments;
	}
	
	private void printOptionHelp(String optionName) throws OptionDoesNotExistException {
		String helpMessage = helps.get(optionName);
		if(helpMessage == null) {
			throw new OptionDoesNotExistException("Program option \""+optionName+"\" does not exist");
		}else {
			System.out.println(helpMessage);
		}
	}
}
