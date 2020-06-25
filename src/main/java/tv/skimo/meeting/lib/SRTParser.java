package tv.skimo.meeting.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.HashMap;

public class SRTParser 
{
	public boolean isInteger( String input ) 
	{ 
	    try 
	    {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( Exception e ) 
	    { 
	        return false; 
	    }
	} 
	public boolean isTimestamp(String inputString)
	{ 
	    SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss,SSS --> HH:mm:ss,SSS");
	    try
	    {
	       format.parse(inputString);
	       return true;
	    }
	    catch(ParseException e)
	    {
	        return false;
	    }
	}
	
	public HashMap<String, String> process(String inputString)
	{
        HashMap<String, String> hMap = new HashMap<String, String>();
	    try 
	    {
	        File myObj = new File(inputString);
	        Scanner myReader = new Scanner(myObj);
	        String key = "";
	        String value = "";
	        while (myReader.hasNextLine()) 
	        {
	          String data = myReader.nextLine();
	          if(isTimestamp(data))
	          {
	      		 key = (data.substring(0, data.indexOf("-"))).split(",")[0];
	          }
	          else if(!isInteger(data))
	          {
	        	  data = data.replaceAll("\\<.*?\\>", "");
	        	  value+= " " + data;
	        	  if(data.contains("."))
	        	  {
	        		  hMap.put(key.trim(), value.trim());
	        		  value = "";
	        	  }
	          }
	        }
	        for (String i : hMap.keySet())
	        	System.out.println(i + ":" + hMap.get(i));
	        myReader.close();
	    } 
	    catch (FileNotFoundException e) 
	    {
	    	System.out.println("An error occurred.");
	    	e.printStackTrace();
	    }
	    return hMap;
	}
	
	public void writeToFile(HashMap <String, String> hMap, String outputFilePath)
	{
        File file = new File(outputFilePath);
		BufferedWriter bf = null;;
	        
	        try{
	            
	            bf = new BufferedWriter( new FileWriter(file) );
	 
	            for(String entry : hMap.keySet())
	            {
	                bf.write( entry + ":" + hMap.get(entry) );	                
	                bf.newLine();
	            }
	            
	            bf.flush();
	 
	        }
	        catch(IOException e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        { 
	            try
	            {
	                bf.close();
	            }catch(Exception e){}
	        }
	}

	public static void main(String[] args) 
	{
		SRTParser s = new SRTParser();
		HashMap<String, String> hMap = s.process("subs.srt");
		s.writeToFile(hMap,"output.sub");
	}

}
