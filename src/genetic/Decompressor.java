package genetic;

public class Decompressor {
	String text;
	
	public Decompressor(String text){
		this.text=text;
	}
	
	public String decompress(){
		String[] rules = text.substring(text.indexOf("=")+1).split("\\|");
		String data = text.substring(0,text.indexOf("="));
		String rule;
		for (int i = rules.length-1;i>=0;i--){
			rule = rules[i];
			data = data.replace(rule.substring(0,1), rule.substring(2));;
		}
		return new String(data);
	}
	
	public long gettime(long start){
		return (long) ((System.nanoTime()-start)/1000000.0);
	}
}
