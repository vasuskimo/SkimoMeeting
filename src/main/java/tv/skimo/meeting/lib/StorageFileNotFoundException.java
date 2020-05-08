package tv.skimo.meeting.lib;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = -2549915493954955737L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
