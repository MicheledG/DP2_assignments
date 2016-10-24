import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class ex_02 {

	public static void main(String[] args) {
		
		String fileName = System.getProperty("it.polito.dp2.file");
		
		ArrayList<String> fileLines = new ArrayList<String>();
		ex_01.StoreFileIntoStringArrayList(fileName, fileLines);
		
		ArrayList<GregorianCalendar> gregorianDates = new ArrayList<GregorianCalendar>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy MM dd");
		for (String fileLine : fileLines) {
			try {
				Date date = dateFormat.parse(fileLine);
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(date);
				gregorianDates.add(gc);
			} 
			catch (ParseException e) {
				System.out.println("Error: " + e.toString());
				return;
			}
		}
		
		
		GregorianCalendar oldestGregCal = new GregorianCalendar();
		oldestGregCal.setTime(new Date(Long.MIN_VALUE));
		GregorianCalendar mostRecentGregCal = oldestGregCal;
		
		for (GregorianCalendar gregorianDate : gregorianDates) {
			System.out.println("date: " + dateFormat.format(gregorianDate.getTime()));
			if(gregorianDate.compareTo(mostRecentGregCal) > 0)
				mostRecentGregCal = gregorianDate;
		}
		
		if(mostRecentGregCal.compareTo(oldestGregCal) > 0 ){
			System.out.println("Most recent date: " + dateFormat.format(mostRecentGregCal.getTime()));
		}
			
	
		return;
	
	}

}
