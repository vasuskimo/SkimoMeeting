package tv.skimo.meeting.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(NoteId.class)
public class Annotation {

    @Id
	private String assetId;
    @Id
    private String email;
    @Id
    private String timeCode;

    private String annotationType;
    
    private String note;
    
    public String getAssetId() {
        return assetId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeCode() {
        return timeCode;
    }

    public void setTimeCode(String timeCode) {
        this.timeCode = timeCode;
    }

	public String getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

}