package com.change_vision.astah.rest;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.codehaus.jackson.JsonParseException;

import com.atlassian.confluence.setup.BootstrapManager;
import com.change_vision.astah.exporter.DiagramFile;
import com.change_vision.astah.exporter.DiagramJson;
import com.change_vision.astah.exporter.ExportBaseDirectory;
import com.change_vision.astah.exporter.ExportRootDirectory;
import com.sun.jersey.api.NotFoundException;

/**
 * A resource of exported files.
 */
@Path("/attachment")
public class ExportedFileResource {

    private final ExportBaseDirectory exportBase;

    public ExportedFileResource(BootstrapManager bootstrapManager) {
        this.exportBase = new ExportBaseDirectory(bootstrapManager);
    }

    @GET
    @Produces({ "application/json; charset=utf-8" })
    @Path("/diagrams/{attachmentId}/{attachmentVersion}")
    public String getDiagrams(@PathParam("attachmentId") long attachmentId,
            @PathParam("attachmentVersion") int attachmentVersion) {
        ExportRootDirectory exportRoot = new ExportRootDirectory(exportBase, attachmentId, attachmentVersion);
        DiagramJson diagramJson = new DiagramJson(exportRoot);
        try {
            return diagramJson.readFile();
        } catch (IOException e) {
            String message = String.format("No such attachment. id:'%d' version:'%d'",
                    attachmentId, attachmentVersion);
            throw new NotFoundException(message);
        }
    }

    @GET
    @Produces({ "image/png" })
    @Path("/image/{attachmentId}/{attachmentVersion}/{index}.png")
    public File getExportedFileImage(@PathParam("attachmentId") long attachmentId,
            @PathParam("attachmentVersion") int attachmentVersion,
            @PathParam("index") int index) throws JsonParseException, IOException {
        ExportRootDirectory exportRoot = new ExportRootDirectory(exportBase, attachmentId, attachmentVersion);
        DiagramFile file = new DiagramFile(exportRoot);
        return file.getFile(index);
    }

}