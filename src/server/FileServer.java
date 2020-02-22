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
		} catch (URISyntaxException e1) {
			//How the hell did we get here
			e1.printStackTrace();
		}
		if(method == Method.GET) {
			if(uri.getPath().endsWith("/")) {
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
    		}else {
    			try {
					String body = fileDirectory.getFileContent(uri.getPath());
					response = new HttpResponse(HttpVersion.OnePointOh, Status.OK, body);
				} catch (FileNotFoundException e) {
					return new HttpResponse(HttpVersion.OnePointOh, Status.NOT_FOUND, e.getMessage());
				} catch (FileOutsideDirectoryException e) {
					return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
				}
    		}
    	}else if (method == Method.POST){
    		try {
				fileDirectory.createFile(uri.getPath(), request.getBody(), true);//TODO handle the overwrite
				response = new HttpResponse(HttpVersion.OnePointOh, Status.OK);
			} catch (FileAlreadyExistsException e) {
				return new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST, e.getMessage());
			} catch (FileOutsideDirectoryException e) {
				return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
			}
    	}
        return response;
    }

}