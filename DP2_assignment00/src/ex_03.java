import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ex_03 {

	public static void main(String[] args) {
		
		ArrayList<String> filesLines = new ArrayList<String>();
		ex_01.StoreFileIntoStringArrayList("file1.txt", filesLines);
		ex_01.StoreFileIntoStringArrayList("file2.txt", filesLines);
		
		Comparator<? super String> c = (string1, string2) -> {
			string1 = string1.toLowerCase();
			string2 = string2.toLowerCase();
			return string1.compareTo(string2);
		};
		
		filesLines.sort(c);
		
		//store into a file
		
		StoreStringArrayListIntoFile("file3.txt", filesLines);
		
		return;
		
	}
	
	public static void StoreStringArrayListIntoFile(String filename, ArrayList<String> fileLines) {
		
		FileWriter fw;
		
		try {
			fw = new FileWriter(filename);
		} catch (IOException e) {
			System.out.println("Error " + e.toString());
			return;
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (String string : fileLines) {
			try {
				bw.write(string);
				bw.newLine();
				bw.flush();
			} catch (IOException e) {
				System.out.println("Error " + e.toString());
				return;
			}
		}
		
	}
	
}