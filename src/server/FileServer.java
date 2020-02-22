package server;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;

import directory.FileDirectory;
import directory.FileOutsideDirectoryException;
import server.library.HttpRequest;
import server.library.HttpRequestObserver;
import server.library.HttpResponse;
import server.library.HttpVersion;
import server.library.Method;
import server.library.Status;

class FileServer implements HttpRequestObserver {
	FileDirectory fileDirectory;
	
    public FileServer(String fileDirectoryPath) {
		this.fileDirectory = new FileDirectory(fileDirectoryPath);
	}

	@Override
    public HttpResponse onRequest(HttpRequest request) {
    	Method method = request.getMethod();
    	HttpResponse response = null;
    	URI uri = null;
		try {
			uri = new URI(request.getUri());
			if(method == Method.GET) {
				if(uri.getPath().endsWith("/")) {
	    			response = getDirectotyContent(uri);
	    		}else {
	    			response = getFileContent(uri);
	    		}
	    	}else if (method == Method.POST){
	    		response = createFile(uri, request.getBody());
	    	}
		} catch (URISyntaxException e) {
			//How the hell did we get here
			e.printStackTrace();
		}
		return response;
    }

	private HttpResponse createFile(URI uri, String body) {
		HttpResponse response;
		try {
			fileDirectory.createFile(uri.getPath(), body, true);//TODO handle the overwrite
			response = new HttpResponse(HttpVersion.OnePointOh, Status.OK);
		} catch (FileAlreadyExistsException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST, e.getMessage());
		} catch (FileOutsideDirectoryException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
		}
		return response;
	}

	private HttpResponse getFileContent(URI uri) {
		HttpResponse response;
		try {
			String body = fileDirectory.getFileContent(uri.getPath());
			response = new HttpResponse(HttpVersion.OnePointOh, Status.OK, body);
		} catch (FileNotFoundException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.NOT_FOUND, e.getMessage());
		} catch (FileOutsideDirectoryException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
		}
		return response;
	}
	
	private HttpResponse getDirectotyContent(URI uri) {
		HttpResponse response;
		try {
			String body = fileDirectory.listFiles(uri.getPath());
			response = new HttpResponse(HttpVersion.OnePointOh, Status.OK, body);
		} catch (NotDirectoryException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.NOT_FOUND, e.getMessage());
		} catch (FileNotFoundException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.NOT_FOUND, e.getMessage());
		} catch (FileOutsideDirectoryException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
		}
		return response;
	}

}