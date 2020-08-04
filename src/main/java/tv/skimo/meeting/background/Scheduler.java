package tv.skimo.meeting.background;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import tv.skimo.engine.SkimoEngine;
import tv.skimo.meeting.lib.FileSorter;
import tv.skimo.meeting.lib.ThymeLeafConfig;
import tv.skimo.meeting.model.Skimo;
import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;
import tv.skimo.meeting.utils.LineCounter;
import tv.skimo.meeting.utils.Zipper;

@Component
public class Scheduler 
{
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	
	@Scheduled(fixedDelay = Constants.SKIMO_JOB_EVERY_MINUTE)
	public void executeSkimoJobs() 
	{
		try 
		{
			if(EngineStatus.isBusy())
			{
				log.info("Background task: engine is busy");
			}
			else
			{
				File[] dirs = new File(Constants.PUBLIC).listFiles(File::isDirectory);
				File[] assetDirs = null;
				String assetId = "";
			
				// look for directories that don't have js, css and img
				// provision asset directory 
				// detect timecodes that has scene changes
				// =======================================
				for(int i=0; i < dirs.length; i++)
				{
					if((!dirs[i].getName().equals("js")) && (!dirs[i].getName().equals("css")) && 
						(!dirs[i].getName().equals("img"))) 
					{
						assetId = dirs[i].getName();
						assetDirs = new File(Constants.PUBLIC + dirs[i].getName()).listFiles(File::isDirectory);
						if(assetDirs.length < 1)
						{
							log.info("Background task: kicking off Skimo for " + assetId);
							AssetUtil.provisionAsset(assetId, Constants.PUBLIC + assetId + Constants.ASSET_NAME);
							SkimoEngine.generatePoster(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);	
							SkimoEngine.generateThumbnails(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SkimoEngine.detectScenes(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SkimoEngine.generateSRT(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SkimoEngine.generateSub(assetId);
							SkimoEngine.generateSummary(assetId);
						}
				        File indexFile = new File(Constants.PUBLIC + assetId + "/skimo.html");
					    if(!indexFile.exists())
					    	generateSkimoFile(assetId);
					}
					
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			log.error("Threw an exception in Scheduler::executeSkimoJobs, full stack trace follows:", e);
		}
	}
	
	@Scheduled(fixedDelay = Constants.CLEANUP_TASK_EVERY_DAY)
	public void cleanupUploadDirectory() 
	{
		File dir = new File(Constants.UPLOAD_DIR);
		
		log.info("Background cleanup upload directory task kicked off");

		deleteFiles(dir);
	}

	@Scheduled(fixedDelay = Constants.CLEANUP_TASK_EVERY_DAY)
	public void cleanupAssetDirectories() 
	{
		File[] files = new File(Constants.PUBLIC).listFiles(File::isDirectory);
		
		log.info("Background cleanup asset task kicked off");
		
        for (File f:files)
        {
        	// delete all older directories than 7 days other than css,img,js 
        	
			if((!f.getName().equals("js")) && (!f.getName().equals("css")) && 
					(!f.getName().equals("img"))) 
			{
				long diff = new Date().getTime() - f.lastModified();

	        	if (diff > Constants.WEEKLY * 24 * 60 * 60 * 1000)
	        	{
		        	log.info("Background cleanup task trying to cleanup " + f.getName());
	        		for (File f2 : f.listFiles()) 
	        		{
	        			for (File f3 : f2.listFiles()) 
	        				f3.delete();
	        			f2.delete();
	        		}
	        		f.delete();
	        		log.info("Background cleanup task cleaned up " + f.getName());
	        	}
			}
        }
	}

	public void generateSkimoFile(String assetId) 
	{
		File dir = new File(Constants.PUBLIC + assetId);	
		String timeCodeResource = dir + "/timecodes.txt";
	    File fTimeResource = new File(timeCodeResource); 
		String videoResource = dir  + Constants.ASSET_NAME;
        Writer writer = null;
        File indexFile = new File(Constants.PUBLIC + assetId + "/skimo.html");
        File imgDirect = new File(Constants.PUBLIC + assetId + "/img");
		int noOfLines = 0;
		String baseUrl;
		log.info("Inside generateSkimoFile for " + assetId);

		try 
		{
			if(fTimeResource.exists())
			{
				noOfLines = LineCounter.count(timeCodeResource);
			}
			
			if(!EngineStatus.isBusy() && imgDirect.exists() && (noOfLines > 3))
			{
				List<String> timeCodeList = null;
				try (Stream<String> lines = Files.lines( Paths.get(timeCodeResource)))
				{
					timeCodeList = lines.collect( Collectors.toList() );
				}
				catch ( IOException e )
				{
					log.error("Threw an exception in Scheduler::generateSkimoFile, full stack trace follows:", e);
				}
				timeCodeList.add(0,"0.0");
	
				List<Integer> updatedList = new ArrayList<>();
				String initVal = timeCodeList.get( 0 );
				if ( initVal == null )
				{
					log.warn("Scheduler::generateSkimoFiles initVal is null for assetId " + assetId);
				}
				updatedList.add(0);
				for ( int i = 0; i < timeCodeList.size(); i++ )
				{ 
					if ( ( Double.parseDouble( timeCodeList.get( i ) ) - Double.parseDouble( initVal ) ) > 30 )
					{
						updatedList.add(i );
						initVal = timeCodeList.get( i );
						i = timeCodeList.indexOf( timeCodeList.get( i ) ) - 1;
					}
				}
			    File imgDir = new File(Constants.PUBLIC + assetId + Constants.IMG_DIR);
				List<String> imgList = FileSorter.sort(imgDir);
				
				File videoFile = new File(videoResource);
				String videoFileName = videoFile.getName();
				baseUrl = "../" +assetId  + "/";
				ArrayList<Skimo> skimoList = new ArrayList<>();
				List<String> finalImgList = imgList;
				
				List<String> updatedTimeCodeList =new ArrayList<>(1000);
	
				try
				{
					for(int  i=0; i < updatedList.size();  i++)
					{
						int ix = updatedList.get(i);
						updatedTimeCodeList.add(timeCodeList.get(ix).toString());
					}
				}
				catch(Exception e)
				{
					log.error("Threw an exception in Scheduler::generateSkimoFiles, full stack trace follows:", e);
				}
				
				final ArrayList<String> result = SkimoEngine.getTextFromImage(assetId);
				IntStream.range(1, updatedTimeCodeList.size() ).forEach( i -> {
					double v = Double.parseDouble( updatedTimeCodeList.get( i ) );
					int videoTime = ( int ) v;
					log.info("text is " + result.get(i));
					skimoList.add( new Skimo( baseUrl.concat( videoFileName ).concat( "#t=" + videoTime ),videoTime,result.get(i+1)) );
				} );
	
				Skimo  first_item =  new Skimo(baseUrl.concat( videoFileName ).concat( "#t=" + "0" ) ,0, result.get(0));
				
			    Context context = new Context();
			    context.setVariable("first_item", first_item);
			    context.setVariable("mediaList", skimoList);
	
	
		    	Scheduler s = new Scheduler();
		    	s.cleanupDir(Constants.PUBLIC + assetId + Constants.IMG_DIR);
		    	File file = new File(Constants.PUBLIC + assetId + Constants.TIME_CODE_FILE);
		    	file.delete();
		        String[] skimoFiles = {Constants.PUBLIC + assetId};
		        String zipFile = "upload-dir/" + assetId + ".zip";
		        Zipper zipUtil = new Zipper();
		    	try 
		    	{
		    		writer = new FileWriter(Constants.PUBLIC + assetId + "/skimo.html");
		    		writer.write(ThymeLeafConfig.getTemplateEngine().process("skimo.html", context));
		    		writer.close();
		            zipUtil.zip(skimoFiles, zipFile);
		    	} 
		    	catch (Exception e) 
		    	{
					log.error("Threw an exception in Scheduler::generateSkimoFiles, full stack trace follows:", e);
		    	}
			}

		} 
		catch (Exception e) 
		{
			log.error("Threw an exception in Scheduler::generateSkimoFiles, full stack trace follows:", e);
		}
	}

	
	public  void cleanupDir(String name) 
	{
		File dir = new File(name);
		
		log.info("Cleanup  directory " + name);

		deleteFilesRegardless(dir);
		dir.delete();
	}
	
	public  void deleteFiles(File dir) 
	{
	    File[] files = dir.listFiles();
	    if(files != null) 
	    {
	        for (final File file : files) 
	        {
	           log.info(file.getAbsolutePath());
	           deleteFiles(file);
	        }
	    }
	    long diff = new Date().getTime() - dir.lastModified();

    	if (diff > Constants.WEEKLY * 24 * 60 * 60 * 1000)
    	{
    		if(!dir.isDirectory())
    		   dir.delete();
    	}
	}

	public  void deleteFilesRegardless(File dir) 
	{
	    File[] files = dir.listFiles();
	    if(files != null) 
	    {
	        for (final File file : files) 
	        {
	           log.info(file.getAbsolutePath());
	           deleteFilesRegardless(file);
	        }
	    }
    	if(!dir.isDirectory())
    		dir.delete();
	}
	
	public static void main(String args[])
	{
		Scheduler s = new Scheduler();
		//s.executeSkimoJobs();
		s.cleanupDir("public/28bb529e/img/");
	}
}