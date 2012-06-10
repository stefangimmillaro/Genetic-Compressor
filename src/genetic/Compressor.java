package genetic;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;

public class Compressor {
	final int smallestCompressionSegment;
	final int largestCompressionSegment;
	final int mutationsPerGeneration;
	final int generations;
	final Random generator = new Random();

	LinkedHashMap<String, String> rules = new LinkedHashMap<String, String>();
	ArrayList<String> replacement_characters = new ArrayList<String>();
	int generations_count = 0;

	public Compressor(int smallestCompressionSegment, int largestCompressionSegment, int mutationsPerGeneration, int generations){
		this.smallestCompressionSegment=smallestCompressionSegment;
		this.largestCompressionSegment=largestCompressionSegment;
		this.mutationsPerGeneration=mutationsPerGeneration;
		this.generations=generations;
	}

	public String compress(String data){
		if (replacement_characters.size()==0){
			generate_replacement_characters(data, 3*generations);
			data.replace("=", replacement_characters.get(replacement_characters.size()-1));
			data.replace("|", replacement_characters.get(replacement_characters.size()-2));
		}
		if (data.length()<=smallestCompressionSegment*2){return data;}

		return do_compression(data);
	}

	private String do_compression(String data){
		System.out.println("GENERATION: " + generations_count + " (" + data.length() + ")");
		if (generations_count>=generations){return data;}
		generations_count++;
		int start,mutation_length,score,occurances;
		String mutation_phrase;
		int mutations_counter=0;
		Mutations mutations = new Mutations();

		while (++mutations_counter<=mutationsPerGeneration){
			start = generator.nextInt(data.length()-smallestCompressionSegment);
			mutation_length = smallestCompressionSegment+generator.nextInt(largestCompressionSegment-smallestCompressionSegment+1);
			mutation_phrase = data.substring(start, start+Math.min(mutation_length, data.length()-start));//if start is close to end of data, stay in bounds
			occurances = count_substring(data, mutation_phrase);
			score = (mutation_phrase.length()-1)*(occurances-1)-3;//c*s-c-s-2
			if (score>0){
				mutations.add_mutation(mutation_phrase, score, occurances);
			}
		}
		
		System.out.println(" "+mutations.toString());
		
		Mutation[] best_mutations = new Mutation[2];
		int best_count=0;
		for (int i=0;i<2;i++){
			if (mutations.has_mutations()){
				best_mutations[i] = mutations.get_best_mutation();
				best_count++;
			}
		}

		String rule_character, rule_character2, rule_character3;
		switch(best_count){
		case 1:
			rule_character = String.valueOf(replacement_characters.get(rules.size()));
			Mutation m = best_mutations[0];
			if (m.score - ( (character_bytes(rule_character)-1) * (m.occurances+1) ) > 0){
				rules.put(rule_character, m.phrase);
				String compressed_data = data.replace(m.phrase, rule_character);
				return do_compression(compressed_data);
			}
			break;
		case 2:
			rule_character = String.valueOf(replacement_characters.get(rules.size()));
			rule_character2 = String.valueOf(replacement_characters.get(1+rules.size()));
			String[] compressed_data = new String[2];
			
			if (best_mutations[0].score - ( (character_bytes(rule_character)-1) * (best_mutations[0].occurances+1) ) > 0){
				rules.put(rule_character, best_mutations[0].phrase);
				compressed_data[0] = data.replace(best_mutations[0].phrase, rule_character);
				
				if (best_mutations[1].score - ( (character_bytes(rule_character2)-1) * (best_mutations[1].occurances+1) ) > 0){
					compressed_data[1] = compressed_data[0].replace(best_mutations[1].phrase, rule_character2);
					if (compressed_data[1].length()<compressed_data[0].length()){
						rules.put(rule_character2, best_mutations[1].phrase);
						return do_compression(compressed_data[1]);
					}
				}
				
				return do_compression(compressed_data[0]);
			}
			break;
		case 3:
			break;
		}

		return data;
	}

	public String prepare_output_data(String text){
		String compressed_output = compress(text);
		StringBuilder out = new StringBuilder();
		out.append(compressed_output);
		out.append("=");

		for (String rule : rules.keySet()){
			out.append(rule + "=" + rules.get(rule) + "|");
		}

		return out.toString().substring(0,out.length()-1);
	}

	private int character_bytes(String s){
		return s.getBytes(Charset.forName("UTF-8")).length;
	}

	private void generate_replacement_characters(String data, int characters_needed){
		HashSet<String> used_characters = new HashSet<String>();
		characters_needed+=2;//for the following two tokens
		used_characters.add("=");//Going to use this as a token in output, id: 90
		used_characters.add("|");//Going to use this as a token in output, id: 248

		for (Character c : data.toCharArray()){
			used_characters.add(c.toString());
		}

		int index=0;
		int used=0;
		while (used<characters_needed){
			if (index==61 || index==124){ index++; }
			//dont use = or | as replacement characters
			String s = Character.toString((char) index);

			if (!used_characters.contains(s)){
				replacement_characters.add(s);
				used++;
			}
			index++;
		}
	}

	private int count_substring(String big, String phrase){
		int count = 0;
		int idx = 0;
		while ((idx = big.indexOf(phrase, idx)) != -1) {
			count++;
			idx += phrase.length();
		}
		return count;
	}
}
