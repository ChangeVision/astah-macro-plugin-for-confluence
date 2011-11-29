package com.change_vision.astah.rest;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import com.atlassian.confluence.setup.BootstrapManager;
import com.sun.jersey.api.NotFoundException;

/**
 * A resource of exported files.
 */
@Path("/attachment")
public class ExportedFileResource {

	private final String OUTPUT_BASE;
	private final ObjectMapper mapper = new ObjectMapper(); 
	
	public ExportedFileResource(BootstrapManager bootstrapManager){
		OUTPUT_BASE = bootstrapManager.getConfluenceHome() + File.separator +  "astah-exported";
	}
	
    @GET
    @Produces({"application/json; charset=utf-8"})
    @Path("/diagrams/{attachmentId}/{attachmentVersion}")
    public String getDiagrams(
    		@PathParam("attachmentId") Long attachmentId,
    		@PathParam("attachmentVersion") Long attachmentVersion){
        File idDir = new File(OUTPUT_BASE,attachmentId.toString());
		File versionDir = new File(idDir,attachmentVersion.toString());
		File file = new File(versionDir,"diagram.json");
		try {
			return FileUtils.readFileToString(file,"utf-8");
		} catch (IOException e) {
			String message = String.format("No such attachment. id:'%d' version:'%d'",attachmentId,attachmentVersion);
			throw new NotFoundException(message);
		}
    }
    
    @GET
    @Produces({"image/png"})
    @Path("/image/{attachmentId}/{attachmentVersion}/{index}.png")
    public File getExportedFileImage(
    		@PathParam("attachmentId") Long attachmentId,
    		@PathParam("attachmentVersion") Long attachmentVersion,
    		@PathParam("index") Integer index) throws JsonParseException, IOException{
        File idDir = new File(OUTPUT_BASE,attachmentId.toString());
		File versionDir = new File(idDir,attachmentVersion.toString());
		File indexFile = new File(versionDir,"file.json");
		String[] filePaths = mapper.readValue(indexFile, String[].class);
		String filePath = filePaths[index];
		File file = new File(OUTPUT_BASE,filePath);
       return file;
    }
 
}