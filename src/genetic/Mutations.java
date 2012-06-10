package genetic;

import java.util.ArrayList;

public class Mutations {
	private ArrayList<Mutation> mutations = new ArrayList<Mutation>();
	
	public Boolean add_mutation(String phrase, Integer score, Integer occurances){
		if (mutations.contains(phrase)){ return false; }
		
		for (int i=0;i<mutations.size();i++){
			if (score>=mutations.get(i).score){
				mutations.add(i, new Mutation(phrase, score, occurances));
				return true;
			}
		}
		
		mutations.add(new Mutation(phrase, score, occurances));
		return true;
	}
	
	public Mutation get_best_mutation(){
		Mutation best = mutations.get(0);
		mutations.remove(0);
		return best;
	}
	
	public Boolean has_mutations(){
		return mutations.size()!=0;
	}
	
	public String toString(){
		StringBuilder report = new StringBuilder();
		for (int i=0;i<mutations.size();i++){
			Mutation mutation = mutations.get(i);
			report.append(mutation.phrase + " " + mutation.score + " " + mutation.occurances + " | " );
		}
		return report.toString();
	}
}
