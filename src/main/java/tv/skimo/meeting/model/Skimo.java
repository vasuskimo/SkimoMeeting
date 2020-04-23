package tv.skimo.meeting.model;
import java.io.Serializable;

public class Skimo implements Serializable
{
	private String imageUrl;
	private String videoUrl;
 
	public Skimo()
	{
	}

	public Skimo( String imageUrl, String videoUrl )
	{
		this.imageUrl = imageUrl;
		this.videoUrl = videoUrl;
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
}
