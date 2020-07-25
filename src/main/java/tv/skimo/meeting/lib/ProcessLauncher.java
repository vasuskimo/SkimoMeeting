package tv.skimo.meeting.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import tv.skimo.meeting.utils.Constants;

public class ProcessLauncher 
{	
	public static void launch(String[] command, Logger log, String name)
	{
	
		ProcessBuilder processBuilder = new ProcessBuilder(command); 
	    processBuilder.directory(new File(System.getProperty(Constants.USER_HOME)));
	    
	    try 
	    {
	        Process p = processBuilder.start();
	        p.waitFor(); 
	        BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
	        String line; 
	        while((line = reader.readLine()) != null) 
	        { 
	            log.info(name + line);
	        } 
	    } 
	    catch (Exception e) 
	    {
			log.error("Threw an exception in ProcessLauncher:launch method, full stack trace follows:", e);
	    }
	}

}
