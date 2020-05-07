package tv.skimo.meeting.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineCounter 
{
    private static final Logger log=LoggerFactory.getLogger(LineCounter.class);

	public static int count(String fileName)  
	{
	    FileReader fr;
		int count = 1;
		try 
		{
			fr = new FileReader(fileName);
			BufferedReader bufr = new BufferedReader(fr);
	    
			String line = bufr.readLine();
			while(line != null)
			{
				line = bufr.readLine(); 
				count++;
			}
			bufr.close();
		} 
		catch (Exception e) 
		{
			log.error("Threw an exception in LineCounter::count, full stack trace follows:", e);
		}
	    return count;
	}
	
	public static void main(String[] args) throws IOException 
	{
		String fileName = "./pom.xml";
		System.out.println(LineCounter.count(fileName));
	}
}

