package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Map;

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
	    			response = getDirectoryContent(uri);
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
			Map<String, String> queryParams = parseQueryParameters(uri);
			boolean overwrite = queryParams.get("overwrite") == null ? false : Boolean.parseBoolean(queryParams.get("overwrite"));
			fileDirectory.createFile(uri.getPath(), body, overwrite);
			response = new HttpResponse(HttpVersion.OnePointOh, Status.OK);
		} catch (FileAlreadyExistsException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST, e.getMessage());
		} catch (FileOutsideDirectoryException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.FORBIDDEN, e.getMessage());
		} catch (FileNotFoundException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.BAD_REQUEST, e.getMessage());
		} catch (IOException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.INTERNAL_SERVER_ERROR, e.getMessage());
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
		} catch (IOException e) {
			return new HttpResponse(HttpVersion.OnePointOh, Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return response;
	}
	
	private HttpResponse getDirectoryContent(URI uri) {
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
	
	private Map<String, String> parseQueryParameters(URI uri){
		Map<String, String> paramters = new HashMap<String, String>();
		if(!(uri.getQuery()==null || uri.getQuery().isEmpty())) {
			String[] params = uri.getQuery().split("&");
			for(String param :params) {
				String[] kvp = param.split("=");
				paramters.put(kvp[0], kvp[1]);
			}
		}
		return paramters;
	}

}