package main;
import java.util.ArrayList;

public class Result {
	private ArrayList<String> results;
	private int idx_read;
	private int idx_parse;
	
	public Result() {
		results = new ArrayList<String>();
	}
	
	public void add(String result) {
		results.add(result);
		parse(idx_parse);
	}
	
	private void parse(int idx) {
		//parsing mechanism
		idx_parse++;
	}
	
	public String getRaw() {
		String ret = results.get(idx_read);
		idx_read++;
		return ret;
	}
}
