package tv.skimo.meeting.background;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tv.skimo.meeting.lib.AssetInformation;
import tv.skimo.meeting.lib.EngineStatus;
import tv.skimo.meeting.lib.SceneDetector;

@Component
public class Scheduler {

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedDelay = 300000)
	public void executeSkimoJobs() 
	{
		try 
		{
			if(EngineStatus.isBusy())
			{
				log.info("Engine is busy");
			}
			else
			{
				File[] dirs = new File("public/").listFiles(File::isDirectory);
				File[] assetDirs = null;
				for(int i=0; i < dirs.length; i++)
				{
					if((!dirs[i].getName().equals("js")) &&
						(!dirs[i].getName().equals("css")) && 
						(!dirs[i].getName().equals("img"))) 
					{
						String assetId = dirs[i].getName();
						assetDirs = new File("public/" + dirs[i].getName()).listFiles(File::isDirectory);
						if(assetDirs.length < 1)
						{
							log.info("Kicking off Skimo for " + assetId);
							AssetInformation.createDirs(assetId, "public/" + assetId + "/source.mp4");
							SceneDetector.generateFirst("public/" + assetId + "/source.mp4", assetId);
							SceneDetector.generateThumbnail("public/" + assetId + "/source.mp4", assetId);
							SceneDetector.generateSkimo("public/" + assetId + "/source.mp4", assetId);
						}
					}	
					
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Scheduler s = new Scheduler();
		s.executeSkimoJobs();
	}
}