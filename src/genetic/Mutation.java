package genetic;

public class Mutation {
	final String phrase;
	final int score;
	final int occurances;
	
	public Mutation(String phrase, int score, int occurances){
		this.phrase=phrase;
		this.score=score;
		this.occurances=occurances;
	}
	
	public boolean equals(Object o){
		if (o.getClass()==this.getClass()){
			Mutation m = (Mutation) o;
			return m.phrase.equals(this.phrase);
		}
		return false;
	}
}
