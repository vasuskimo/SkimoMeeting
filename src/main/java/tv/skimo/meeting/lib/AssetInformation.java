package tv.skimo.meeting.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssetInformation
{ 
	// This method finds the crc32 code of the uploaded file
	// It creates an assetid directory under content directory
	// It creates img directory under content directory
	// It copies the uploaded file under the assetid directory
    public static String create(String dir, String assetName)
    {
	    long crc = 0;
		try 
		{
			crc = VideoHasher.crc32(dir + assetName);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	    String assetId = Long.toHexString(crc);
	    
	    File file = new File("content/" +assetId);
	    boolean result = file.mkdir(); 
	    if(!result)
	    	return "present";
	    file = new File("content/" + assetId+"/img");
	    result = file.mkdir();
	    
        File source = new File(dir + assetName);
        File dest = new File("content/" + assetId + "/" + assetName);

        try 
        {
			Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	      
	    return assetId;  	
    } 
    
    public static void main(String[] args) 
    { 
      System.out.println(AssetInformation.create("EJUXlL3rHzA", "a.mp4"));
    } 
} 
