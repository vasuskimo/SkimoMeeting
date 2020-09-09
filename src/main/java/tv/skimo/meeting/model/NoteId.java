package tv.skimo.meeting.model;

import java.io.Serializable;

public class NoteId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2716069348493734681L;

	private String assetId;
 
    private String email;
    
    private String timeCode;
 

    public NoteId() { }
    
    public NoteId(String assetId, String email, String timeCode) {
        this.assetId = assetId;
        this.email = email;
        this.timeCode = timeCode;
    }
    
    
 
}