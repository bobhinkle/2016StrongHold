package IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static Logger instance = null;
	private String path = "/home/lvuser/";
	private File file;
    private BufferedWriter out;
	public Logger(){
		try{
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;
			file = new File(path + dateFormat.format(date) + ".txt") ;		
			out = new BufferedWriter(new FileWriter(file));
		}catch (Exception e) {
	        // TODO Auto-generated catch block
	        System.out.println(e);
	    }
	}
	public void writeToLog(String item){
		try {			
			out.write(item);
			out.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static Logger getInstance(){
		if( instance == null )
            instance = new Logger();
        return instance;
	}
}
