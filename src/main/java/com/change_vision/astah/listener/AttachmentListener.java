package com.change_vision.astah.listener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.change_vision.astah.util.Util;
 
public class AttachmentListener implements DisposableBean{
    private static final Logger log = LoggerFactory.getLogger(AttachmentListener.class);
 
    protected EventPublisher eventPublisher;
    
    private final File tmpRoot = new File(System.getProperty("java.io.tmpdir"));

	private final String OUTPUT_BASE;
	
	private final Util util = new Util();
	
	private final ObjectMapper mapper = new ObjectMapper();

	private String ASTAH_BASE; 
 
    public AttachmentListener(BootstrapManager bootstrapManager,EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        eventPublisher.register(this);
		OUTPUT_BASE = bootstrapManager.getConfluenceHome() + File.separator +  "astah-exported";
		ASTAH_BASE = bootstrapManager.getConfluenceHome() + File.separator + "astah";

        log.info("created attachment listener");
    }
 
    @EventListener
    public void attachmentCreateEvent(AttachmentCreateEvent event) {
    	log.info("attachmentCreateEvent!!");
        exportDiagramImages(event);
    }
    
    @EventListener
    public void attachmentUpdateEvent(AttachmentUpdateEvent event) {
    	log.info("attachmentUpdateEvent!!");
        exportDiagramImages(event);
    }

	private void exportDiagramImages(AttachmentEvent event) {
		log.info("attachment : " + event);
		boolean updateEvent = (event instanceof AttachmentUpdateEvent);
        List<Attachment> attachments = event.getAttachments();
        for (final Attachment attachment : attachments) {
			String extension = attachment.getFileExtension();
			boolean needToExportEvent = updateEvent || attachment.isNew();
			boolean isTargetExtension = extension != null && (extension.equals("asta") ||
								extension.equals("jude") ||
								extension.equals("juth"));
			if(needToExportEvent && isTargetExtension){
				Runnable runner = new Runnable() {
					public void run() {
						log.info("start export : " + attachment.getId());
						exportAndTraverseFiles(attachment);
						log.info("end export : " + attachment.getId());
					}
				};
				Thread exportThread = new Thread(runner);
				exportThread.start();
			}
		}
	}
    
	private void exportAndTraverseFiles(Attachment attachment) {
		File file = storeToTempDir(attachment);
		log.info("file : " + file.getAbsolutePath());
		
		String outputRoot = OUTPUT_BASE  + File.separator + String.valueOf(attachment.getId()) + File.separator + String.valueOf(attachment.getVersion());
		File javaHome = new File(System.getProperty("java.home"));
		File javaBin = new File(javaHome,"bin");
		File javaCommand = new File(javaBin,"java");
		String[] commands = new String[]{
			javaCommand.getAbsolutePath(),
			"-Xms16m",
			"-Xmx384m",
			"-Djava.awt.headless=true",
			"-cp",
			ASTAH_BASE + File.separator + "astah-community.jar",
			"com.change_vision.jude.cmdline.JudeCommandRunner",
			"-image",
			"all",
			"-resized",
			"-f",
			file.getAbsolutePath(),
			"-t",
			"png",
			"-o",
			outputRoot
		};
		for (String command: commands){
			log.info("args:" + command);
		}
		ProcessBuilder builder = new ProcessBuilder(commands);
		try {
			Process p = builder.start();
			p.waitFor();
		} catch (IOException e) {
			log.error("error has occurred when exporting.",e);
		} catch (InterruptedException e) {
			log.error("interappted exporting", e);
		}
		List<File> pngFiles = new ArrayList<File>();
		traverseFiles(pngFiles,new File(outputRoot));
		
		exportFileIndexFile(outputRoot, pngFiles);
		exportDiagarmIndexFile(outputRoot, pngFiles);

	}

	private void exportFileIndexFile(String outputRoot, List<File> pngFiles) {
		try {
			String[] filePaths = new String[pngFiles.size()];
			for (int index = 0; index < pngFiles.size(); index ++) {
				File pngFile = pngFiles.get(index);
				String relativePath = util.getRelativePath(OUTPUT_BASE,pngFile);
				filePaths[index] = relativePath;
			}
			mapper.writeValue(new File(outputRoot,"file.json"), filePaths);
		} catch (IOException e) {
			log.error("error has occurred when making index file.",e);
		}
	}

	private void exportDiagarmIndexFile(String outputRoot, List<File> pngFiles) {
		try {
			String[] fileNames = new String[pngFiles.size()];
			for (int index = 0; index < pngFiles.size(); index ++) {
				File pngFile = pngFiles.get(index);
				String filename = util.getFilename(pngFile);
				fileNames[index] = filename;
			}
			mapper.writeValue(new File(outputRoot,"diagram.json"), fileNames);
		} catch (IOException e) {
			log.error("error has occurred when making index file.",e);
		}
	}

	private void traverseFiles(List<File> pngFiles, File parent) {
		File[] files = parent.listFiles();
		for (File file : files) {
			if(file.isDirectory()){
				traverseFiles(pngFiles, file);
			}
			
			String suffix = util.getFileExtension(file);
			if(suffix != null && suffix.equalsIgnoreCase("png")){
				pngFiles.add(file);
			}
		}
	}

	private File storeToTempDir(Attachment attachment){
		try {
			long id = attachment.getId();
			File tmpDir = new File(tmpRoot,String.valueOf(id));
			tmpDir.mkdirs();
			File tmpFile = new File(tmpDir,attachment.getFileName());
			InputStream inputStream = attachment.getContentsAsStream();
			FileOutputStream outputStream = new FileOutputStream(tmpFile);
			IOUtils.copy(inputStream, outputStream);
			IOUtils.closeQuietly(outputStream);
			tmpFile.deleteOnExit();
			return tmpFile;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(),e);
			return null;
		}
	}
 
    // Unregister the listener if the plugin is uninstalled or disabled.
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }
}