package tv.skimo.meeting.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineStatus 
{ 
   private static final Logger log=LoggerFactory.getLogger(EngineStatus.class);
   
   public static boolean isBusy() throws IOException 
   {
		String process,s="";
		try 
		{
			Process p = Runtime.getRuntime().exec(Constants.PS_AUX);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) 
			{
				s+= process;
			}
			input.close();
		} 
		catch (Exception e) 
		{
			log.error("Threw an exception in EngineStatus::isBusy, full stack trace follows:", e);
		}
		int index = s.indexOf(Constants.FFPROBE); 
		if(index != -1)
			return true;
		index  = s.indexOf(Constants.FFMPEG);
		if(index  !=-1)
		  return true;
	    return false;
   }

   public static boolean isRunningSkimo(String assetId) throws IOException 
   {
		String process,s="";
		try 
		{
			Process p = Runtime.getRuntime().exec(Constants.PS_AUX);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) 
			{
				s+= process;
			}
			input.close();
		} 
		catch (Exception e) 
		{
			log.error("Threw an exception in EngineStatus::isRunningSkimo, full stack trace follows:", e);
		}
		int index = s.indexOf(assetId);
		if(index ==  -1)
			return false;
		return true;
   }
   
   
   public static void main(String[] args) throws IOException 
   {
      System.out.println("Engine is busy and is " + EngineStatus.isBusy());
      System.out.println("Engine generating Skimo for " + "1cde74c8 is " + EngineStatus.isRunningSkimo("1cde74c8"));
   }
}
