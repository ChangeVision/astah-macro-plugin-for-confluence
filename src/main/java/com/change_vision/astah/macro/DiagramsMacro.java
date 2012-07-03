package com.change_vision.astah.macro;

import java.util.Map;
import java.util.UUID;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;

public class DiagramsMacro implements Macro {

	private final AttachmentManager attachmentManager;
    
    public DiagramsMacro(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }


	@Override
	public String execute(Map<String, String> params, String bodyContent,
			ConversionContext conversionContext) throws MacroExecutionException {
		ContentEntityObject entity = conversionContext.getEntity();
		String attachmentTitle = params.get("name");
		Attachment targetAttachment = attachmentManager.getAttachment(entity, attachmentTitle);
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
