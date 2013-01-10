package com.change_vision.astah.listener;
import java.io.File;
import java.util.List;

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
import com.change_vision.astah.exporter.DiagramExporter;
 
public class AttachmentListener implements DisposableBean{

	private static final String EXTENSION_OF_ASTA = "asta";

	private static final String EXTENSION_OF_JUDE = "jude";

	private static final String EXTENSION_OF_JUDE_THINK = "juth";

	private static final Logger logger = LoggerFactory.getLogger(AttachmentListener.class);
 
    protected EventPublisher eventPublisher;
    
	private DiagramExporter diagramExporter;
 
    public AttachmentListener(BootstrapManager bootstrapManager,EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        eventPublisher.register(this);
		String outputBase = bootstrapManager.getConfluenceHome() + File.separator +  "astah-exported";
		String astahBase = bootstrapManager.getConfluenceHome() + File.separator + "astah";
		diagramExporter = new DiagramExporter(astahBase,outputBase);
        logger.info("created attachment listener");
    }
 
    @EventListener
    public void attachmentCreateEvent(AttachmentCreateEvent event) {
    	logger.info("attachmentCreateEvent!!");
        exportDiagramImages(event);
    }
    
    @EventListener
    public void attachmentUpdateEvent(AttachmentUpdateEvent event) {
    	logger.info("attachmentUpdateEvent!!");
        exportDiagramImages(event);
    }

	private void exportDiagramImages(AttachmentEvent event) {
		logger.info("attachment event : " + event);
		boolean updateEvent = (event instanceof AttachmentUpdateEvent);
        List<Attachment> attachments = event.getAttachments();
        for (final Attachment attachment : attachments) {
        	if(logger.isDebugEnabled()){
        		logger.debug("attachment : " + attachment.getFileName());
        	}
			String extension = attachment.getFileExtension();
			if(needsToExport(updateEvent, attachment) && isTargetExtension(extension)){
				logger.info("start export : " + attachment.getId());
				diagramExporter.export(attachment);
				logger.info("end export : " + attachment.getId());
			}
		}
	}

	private boolean needsToExport(boolean updateEvent, Attachment attachment) {
		return updateEvent || attachment.isNew();
	}

	private boolean isTargetExtension(String extension) {
		return isAstah(extension) ||
							isJude(extension) ||
							isThink(extension);
	}

	private boolean isAstah(String extension) {
		return extension != null && extension.equals(EXTENSION_OF_ASTA);
	}
	
	private boolean isJude(String extension) {
		return extension != null && extension.equals(EXTENSION_OF_JUDE);
	}

	private boolean isThink(String extension) {
		return extension != null && extension.equals(EXTENSION_OF_JUDE_THINK);
	}
	
    // Unregister the listener if the plugin is uninstalled or disabled.
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }
    
    void setDiagramExporter(DiagramExporter diagramExporter) {
		this.diagramExporter = diagramExporter;
	}
}