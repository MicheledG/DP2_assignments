import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ex_01 {

	public static void main(String[] args) {
		
		if(args.length != 1){
			System.out.println("Wrong number of parameters! usage: java ex_01 file_name");
			return;
		}
		
		//read and store the two file lines
		ArrayList<String> file1Lines = new ArrayList<String>();
		ArrayList<String> file2Lines = new ArrayList<String>();
		StoreFileIntoStringArrayList("file1.txt", file1Lines);
		StoreFileIntoStringArrayList(args[0], file2Lines);
		
		//print out lines of the second file not contained into first file
		for (String file2Line : file2Lines) {
			boolean missing = true;
			for (String file1Line : file1Lines) {
				if(file1Line.equals(file2Line)){
					missing = false;
					break;
				}
			}
			if(missing)
				System.out.println(file2Line);
		}
		
		return;
		
	}
	
	
	public static void StoreFileIntoStringArrayList(String filename, ArrayList<String> fileLines) {
		
		FileReader fr;
		
		try {
			fr = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("Error " + e.toString());
			return;
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		String fileLine;
		try {
			while((fileLine = br.readLine()) != null) {
				fileLines.add(fileLine);
			}
		} catch (IOException e) {
			System.out.println("Error " + e.toString());
			return;
		}
		
	}
	
}
