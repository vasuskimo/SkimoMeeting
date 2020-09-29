package tv.skimo.meeting.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import tv.skimo.meeting.lib.StorageException;
import tv.skimo.meeting.lib.StorageFileNotFoundException;
import tv.skimo.meeting.lib.StorageProperties;
 
@Service
public class StorageServiceImpl implements StorageService {

	private final Path rootLocation;
	
    private static final Logger log=LoggerFactory.getLogger(StorageServiceImpl.class);

	@Autowired
	public StorageServiceImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	} 
 
	@Override
	public void store(MultipartFile file, String ... args) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		Path source;		
		if(args.length == 0)
		{
			source = Paths.get(this.rootLocation.toString());
		}
		else
		{
			source = Paths.get(this.rootLocation.toString(), args[0]);
			try {
				if(Files.exists(source))
					log.info(source + " already exists ");
				else {
					  File createDir = new File(source.toString());
				      createDir.mkdir();	
					  log.info(source + " directory created");
				}
			} 
			catch (Exception e) {
				log.error("Threw an exception in StorageServiceImpl::store, full stack trace follows:", e);
			}
		}
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				throw new StorageException(
						"Cannot store file with relative path outside current directory "
								+ filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, source.resolve(filename),
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			log.error("Threw an exception in StorageServiceImpl::store, full stack trace follows:", e);
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.filter(f -> f.getFileName().toString().endsWith(".zip"))
				.filter(f -> f.getFileName().toString().length() < 15)
				
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			log.error("Threw an exception in StorageServiceImpl::loadAll, full stack trace follows:", e);
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			log.error("Threw an exception in StorageServiceImpl::init, full stack trace follows:", e);
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
