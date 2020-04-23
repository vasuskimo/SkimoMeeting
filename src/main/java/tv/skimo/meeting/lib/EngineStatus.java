package tv.skimo.meeting.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EngineStatus 
{
   public static boolean isBusy() throws IOException 
   {
		String process,s="";
		try 
		{
			Process p = Runtime.getRuntime().exec("ps aux");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) 
			{
				s+= process;
			}
			input.close();
		} 
		catch (Exception err) 
		{
			err.printStackTrace();
		}
		int index = s.indexOf("ffprobe"); 
		if(index != -1)
			return true;
		index  = s.indexOf("ffmpeg");
		if(index  !=-1)
		  return true;
	    return false;
   }

   public static boolean isRunningSkimo(String assetId) throws IOException 
   {
		String process,s="";
		try 
		{
			Process p = Runtime.getRuntime().exec("ps aux");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) 
			{
				s+= process;
			}
			input.close();
		} 
		catch (Exception err) 
		{
			err.printStackTrace();
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
