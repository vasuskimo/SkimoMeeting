package tv.skimo.meeting.model;
import java.io.Serializable;

public class Skimo implements Serializable
{
	private static final long serialVersionUID = -375414813253543648L;
	private String imageUrl;
	private String videoUrl;
	private String currentTime;
 
	public Skimo()
	{
	}
	
	public Skimo( String imageUrl, String videoUrl)
	{
		this.imageUrl = imageUrl;
		this.videoUrl = videoUrl;
	}

	public Skimo( String imageUrl, String videoUrl, int cTime )
	{
		this.imageUrl = imageUrl;
		this.videoUrl = videoUrl;
		
		int days, hours, minutes, seconds;

		days = cTime / 86400;
		hours = (cTime % 86400 ) / 3600 ;
		minutes = ((cTime % 86400 ) % 3600 ) / 60;
		seconds = ((cTime % 86400 ) % 3600 ) % 60  ;
		
		if(seconds < 10)
			if(minutes < 10)
				this.currentTime = Integer.toString(hours) + ":0" + Long.toString(minutes) + ":0" + 
					Long.toString(seconds);
			else
				this.currentTime = Integer.toString(hours) + ":" + Long.toString(minutes) + ":0" + 
						Long.toString(seconds);
		else
			if(minutes < 10)
				this.currentTime = Integer.toString(hours) + ":0" + Long.toString(minutes) + ":" + 
						Long.toString(seconds);
			else
				this.currentTime = Integer.toString(hours) + ":" + Long.toString(minutes) + ":" + 
					Long.toString(seconds);
			
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl( String imageUrl )
	{
		this.imageUrl = imageUrl;
	}

	public String getVideoUrl()
	{
		return videoUrl;
	}

	public void setVideoUrl( String videoUrl )
	{
		this.videoUrl = videoUrl;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setcurrentTime(String c) {
		this.currentTime = c;
	}
	
}
