package com.change_vision.astah.macro;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;

public class DiagramsMacroParameter {
    
    private static final Logger logger = LoggerFactory.getLogger(DiagramsMacroParameter.class);

    private static final String PARAM_OF_NUMBER = "number";
    private static final String PARAM_OF_ATTACHMENT_NAME = "name";
    private static final String PARAM_OF_PAGE_NAME = "page";
    
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final ConversionContext conversionContext;
    private final Map<String, String> params;


    public DiagramsMacroParameter(PageManager pageManager, AttachmentManager attachmentManager, ConversionContext conversionContext, Map<String, String> params) {
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.conversionContext = conversionContext;
        this.params = params;
    }

    public int getPageNumber() {
        String number = params.get(PARAM_OF_NUMBER);
        if (number == null)
            return 0;
        try {
            return Integer.valueOf(number) - 1;
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    public String getAttachmentName() {
        return params.get(PARAM_OF_ATTACHMENT_NAME);
    }

    public String getPageName() {
        return params.get(PARAM_OF_PAGE_NAME);
   }

    public Attachment getAttachment() {
        logger.trace("args name:'{}' number:'{}'", getAttachmentName(), getPageNumber());
        ContentEntityObject entity = getEntity();
        
        String attachmentTitle = getAttachmentName();
        Attachment targetAttachment = attachmentManager.getAttachment(entity, attachmentTitle);
        return targetAttachment;
    }

    private ContentEntityObject getEntity() {
        if (getPageName() == null || getPageName().isEmpty()) {
            return conversionContext.getEntity();
        }
        String pageTitle = getPageName();
        String spaceKey = conversionContext.getSpaceKey();
        return pageManager.getPage(spaceKey, pageTitle);
    }


}
