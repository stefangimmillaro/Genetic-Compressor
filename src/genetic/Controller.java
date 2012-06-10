package genetic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Controller {
	public static void main(String[] args) throws IOException{
		long start = System.nanoTime();
		compress();
		long end = System.nanoTime() - start;
		System.out.println("Compression speed "+ end/1000000.0 +" ms");
		
		start = System.nanoTime();
		decompress();
		end = System.nanoTime() - start;
		System.out.println("Decompression speed "+ end/1000000.0 +" ms");
	}
	
	public static void compress() throws IOException{
		final int smallestCompressionSegment = 3;
		final int largestCompressionSegment = 6;
		final int mutationsPerGeneration = 50;
		final int generations = 150;
		final Compressor compressor = new Compressor(smallestCompressionSegment, largestCompressionSegment, mutationsPerGeneration, generations);
		
		String input_text = read_file("input/const.txt");
		String compressed_output_with_rules = compressor.prepare_output_data(input_text);
		write_file(compressed_output_with_rules, "output/out.sggc");
		
		float compression_percentage = (float)compressed_output_with_rules.getBytes().length/input_text.getBytes().length;
		String compression_percentage_output =  String.valueOf(1-compression_percentage).substring(2,4) + "." + String.valueOf(compression_percentage).substring(4,6) + "% compression";
		
		System.out.println(input_text.length() + " to " + compressed_output_with_rules.length() + " characters");
		System.out.println(input_text.getBytes().length + " to " + compressed_output_with_rules.getBytes().length + " bytes | " + compression_percentage_output);
	}
	
	public static void decompress() throws IOException{
		String text = read_file("output/out.sggc");
		Decompressor decompressor = new Decompressor(text);
		String data = decompressor.decompress();
		write_file(data, "reconstructed/original.txt");
	}
	
	public static String read_file(String file_name) throws IOException{
		final File file = new File(file_name);
		if (file.isFile() && file.exists()) {
			FileInputStream in = new FileInputStream(file);
			int read = 0, totalRead = 0;
			final int fileLength = (int) file.length();
			final byte[] fileBytes = new byte[fileLength];

			while ((read != -1) && (totalRead < fileLength)) {
				read = in.read(fileBytes, totalRead, fileLength - totalRead);
				if (read != -1) {
					totalRead += read;
				}
			}

			return new String(fileBytes);

		}
		return "INVALIDFILE";
	}

	public static void write_file(String text, String file_name) throws IOException{
		File file = new File(file_name);
		Writer output = new BufferedWriter(new FileWriter(file));
		output.write(text);
		output.close();
	}
}