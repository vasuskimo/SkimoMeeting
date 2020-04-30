package tv.skimo.meeting.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssetUtil
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
 
	// This method creates an assetid directory under public directory and
    // copies the asset into the assetId directory as source.mp4
    public static boolean createAssetDir(String dir, String assetId, String assetName)
    {
	    File file = new File(Constants.PUBLIC + assetId);
	    boolean result = file.mkdir(); 
	    
        File source = new File(dir + assetName);
        File dest = new File(Constants.PUBLIC + assetId + Constants.ASSET_NAME);
		try 
		{
			Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	    return(result);
    } 
    	
	// It creates img,js,css directories under the asset directory
	// It copies css,js and img files under the assetid directory
    public static boolean provisionAsset(String assetId, String assetName)
    {
	    boolean result = false;	    
	    File file1 = new File(Constants.PUBLIC + assetId + Constants.IMG_DIR);
	    result = file1.mkdir();
	    
	    File file2 = new File(Constants.PUBLIC + assetId + Constants.JS_DIR);
	    result = file2.mkdir();
	    
	    File file3 = new File(Constants.PUBLIC + assetId + Constants.CSS_DIR);
	    result = file3.mkdir();
        
        File sourceSlideJS = new File("public/js/glide.js");
        File destSlideJS = new File(Constants.PUBLIC + assetId + "/js/glide.js");
        
        File sourceCSS = new File("public/css/style.css");
        File destCSS = new File(Constants.PUBLIC + assetId + "/css/style.css");

        File sourceIndexJS = new File("public/index.js");
        File destIndexJS = new File(Constants.PUBLIC + assetId + "/js/index.js");
        

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
      System.out.println(AssetUtil.createHash("./upload-dir/","a.mp4"));
      System.out.println(AssetUtil.provisionAsset("./upload-dir/" + "EJUXlL3rHzA", "a.mp4"));
    } 
} 
