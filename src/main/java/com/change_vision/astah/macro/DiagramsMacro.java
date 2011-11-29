package com.change_vision.astah.macro;

import java.util.Map;
import java.util.UUID;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;

public class DiagramsMacro implements Macro {

	private final AttachmentManager attachmentManager;
    private final PageManager pageManager;
    
    public DiagramsMacro(AttachmentManager attachmentManager, PageManager pageManager) {
        this.attachmentManager = attachmentManager;
        this.pageManager = pageManager;
    }


	@Override
	public String execute(Map<String, String> params, String bodyContent,
			ConversionContext conversionContext) throws MacroExecutionException {
		String space = params.containsKey("space") ? params.get("space") : conversionContext.getSpaceKey();
		String page = params.containsKey("page") ? params.get("page") : conversionContext.getEntity().getTitle();
		Page targetPage = pageManager.getPage(space, page);
		String attachmentTitle = params.get("name");
		Attachment targetAttachment = attachmentManager.getAttachment(targetPage, attachmentTitle);
		Map<String, Object> context = MacroUtils.defaultVelocityContext();
		UUID uid =  UUID.randomUUID();
		context.put("id", uid.toString());
		context.put("attachmentId", targetAttachment.getId());
		context.put("attachmentVersion", targetAttachment.getAttachmentVersion());
		return VelocityUtils.getRenderedTemplate("vm/viewer.vm", context);
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}

}
