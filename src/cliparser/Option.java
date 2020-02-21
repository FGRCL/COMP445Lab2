package cliparser;
public class Option {
	private String optionName;
	private int nbArguments;
	private OptionDelegate delegate;
	
	public Option(String optionName, int nbArguments, OptionDelegate delegate) {
		this.optionName = optionName;
		this.nbArguments = nbArguments;
		this.delegate = delegate;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Option) {
			Option option = (Option) o;
			return option.getOptionName().equals(optionName);
		}
		return false;
	}
	
	public String getOptionName() {
		return optionName;
	}
	
	public int getNbArguments() {
		return nbArguments;
	}
	
	public OptionDelegate getDelegate() {
		return delegate;
	}
}
