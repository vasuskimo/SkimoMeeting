package tv.skimo.meeting.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssetInformation
{ 
	// This method computes the crc32 code of the uploaded file
    public static String createHash(String dir, String assetName)
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
	    
	    return assetId;
    }
 
	// This method creates an assetid directory under public directory
    public static boolean createAssetDir(String dir, String assetId, String assetName)
    {
	    File file = new File("public/" +assetId);
	    boolean result = file.mkdir(); 
	    
        File source = new File(dir + assetName);
        File dest = new File("public/" + assetId + "/source.mp4");
		try 
		{
			Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return(result);
    } 
    	
	// It creates img directory under public directory
	// It copies the uploaded file under the assetid directory\\
    public static boolean createDirs(String assetId, String assetName)
    {
	    boolean result = false;	    
	    File file1 = new File("public/" + assetId + "/img/");
	    result = file1.mkdir();
	    
	    File file2 = new File("public/" + assetId + "/js/");
	    result = file2.mkdir();
	    
	    File file3 = new File("public/" + assetId + "/css/");
	    result = file3.mkdir();
        
        File sourceSlideJS = new File("public/js/glide.js");
        File destSlideJS = new File("public/" + assetId + "/js/glide.js");
        
        File sourceCSS = new File("public/css/style.css");
        File destCSS = new File("public/" + assetId + "/css/style.css");

        File sourceIndexJS = new File("public/index.js");
        File destIndexJS = new File("public/" + assetId + "/js/index.js");
        

        try 
        {
			Files.copy(sourceSlideJS.toPath(), destSlideJS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceCSS.toPath(), destCSS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceIndexJS.toPath(), destIndexJS.toPath(),StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	      
	    return true;  	
    } 
    
    public static void main(String[] args) 
    { 
      System.out.println(AssetInformation.createHash("./upload-dir/","a.mp4"));
      System.out.println(AssetInformation.createDirs("./upload-dir/" + "EJUXlL3rHzA", "a.mp4"));
    } 
} 
