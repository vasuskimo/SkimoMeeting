package tv.skimo.meeting.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LineCounter 
{
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return count;
	}
	
	public static void main(String[] args) throws IOException 
	{
		String fileName = "./pom.xml";
		System.out.println(LineCounter.count(fileName));
	}
}

