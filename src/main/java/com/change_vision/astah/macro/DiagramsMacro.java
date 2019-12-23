package com.change_vision.astah.macro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.change_vision.astah.file.DiagramFile;
import com.change_vision.astah.file.DiagramJson;
import com.change_vision.astah.file.ExportBaseDirectory;
import com.change_vision.astah.file.ExportRootDirectory;

public class DiagramsMacro implements Macro, EditorImagePlaceholder {

    private static final String LOADING_IMAGE_PATH = "/download/resources/com.change_vision.astah.astah-confluence-macro/images/loading.png";

    private static final Logger logger = LoggerFactory.getLogger(DiagramsMacro.class);

    private final AttachmentManager attachmentManager;

    private final WritableDownloadResourceManager writableDownloadResourceManager;
    
    private final PageManager pageManager;

    private final ExportBaseDirectory exportBase;
    
    public DiagramsMacro(AttachmentManager attachmentManager,BootstrapManager bootstrapManager,WritableDownloadResourceManager WritableDownloadResourceManager,PageManager pageManager) {
        this.attachmentManager = attachmentManager;
        this.exportBase = new ExportBaseDirectory(bootstrapManager);
        this.writableDownloadResourceManager = WritableDownloadResourceManager;
        this.pageManager = pageManager;
    }

    @Override
    public String execute(Map<String, String> params, String bodyContent,
            ConversionContext conversionContext) throws MacroExecutionException {
        DiagramsMacroParameter parameter = new DiagramsMacroParameter(pageManager, attachmentManager,conversionContext,params);
        int number = parameter.getPageNumber();
        Attachment targetAttachment = parameter.getAttachment();
        ExportRootDirectory exportRoot = new ExportRootDirectory(exportBase, targetAttachment);
        boolean isRenderSinglePage = isRenderPDF(conversionContext) || isRenderWord(conversionContext) || isRenderHTMLExport(conversionContext);
        if(isRenderSinglePage){
            return renderDiagramOnlyFirst(number, exportRoot);
        }
        return renderDiagramsMacro(exportRoot,number);
    }

    private boolean isRenderPDF(ConversionContext conversionContext) {
        return RenderContext.PDF.equals(conversionContext.getOutputType());
    }

    private boolean isRenderWord(ConversionContext conversionContext) {
        // Supported ??
        return RenderContext.WORD.equals(conversionContext.getOutputType());
    }

    private boolean isRenderHTMLExport(ConversionContext conversionContext) {
        return RenderContext.HTML_EXPORT.equals(conversionContext.getOutputType());
    }

    private String renderDiagramOnlyFirst(int number, ExportRootDirectory exportRoot) {
        DiagramJson diagramJson = new DiagramJson(exportRoot);
        DiagramFile diagramFile = new DiagramFile(exportRoot);
        File file = diagramFile.getFile(number);
        String exportImage = writeForExport(file);
        return "<h6>" + diagramJson.getName(number) + "</h6>"+ exportImage;
    }

    private String writeForExport(File file) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String userName = user == null ? "" : user.getName();
        DownloadResourceWriter writer = writableDownloadResourceManager.getResourceWriter(userName, file.getName(), "");
        OutputStream outputStream = writer.getStreamForWriting();
        try {
            InputStream inputStream = null;;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.warn("error has occurred.",e);
                return "<div>Export error: Astah Macro</div>";
            }
            try {
                IOUtils.copy(inputStream , outputStream);
            } catch (IOException e) {
                logger.warn("error has occurred.",e);
                return "<div>Export error: Astah Macro</div>";
            }
        } finally {
            // close the output stream
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn("error has occurred.",e);
                    return "<div>Export error: Astah Macro</div>";
                }
            }
        }
        return "<img src=\""+ writer.getResourcePath() + "\"/>";
    }

    private String renderDiagramsMacro(ExportRootDirectory exportRoot, int number) {
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        DiagramJson diagramsFile = getDiagramsFile(exportRoot);

        UUID uid = UUID.randomUUID();
        context.put("id", uid.toString());
        context.put("attachmentId", exportRoot.getAttachmentId());
        context.put("attachmentVersion", exportRoot.getAttachmentVersion());
        context.put("number", number);
        context.put("generated", diagramsFile.exists());
        return VelocityUtils.getRenderedTemplate("vm/viewer.vm", context);
    }

    private String getAttachmentVersion(Attachment targetAttachment) {
        Integer attachmentVersion = targetAttachment.getVersion();
        return String.valueOf(attachmentVersion);
    }

    private String getAttachmentId(Attachment targetAttachment) {
        long id = targetAttachment.getId();
        return String.valueOf(id);
    }

    private DiagramJson getDiagramsFile(ExportRootDirectory exportRoot){
        return new DiagramJson(exportRoot);
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(Map<String, String> params,
            ConversionContext conversionContext) {
        DiagramsMacroParameter parameter = new DiagramsMacroParameter(pageManager, attachmentManager, conversionContext, params);
        Attachment targetAttachment = parameter.getAttachment();
        int pageNumber = parameter.getPageNumber();
        String attachmentId = getAttachmentId(targetAttachment);
        String attachmentVersion = getAttachmentVersion(targetAttachment);
        String imagePath = getImagePath(pageNumber, attachmentId, attachmentVersion);
        ExportRootDirectory exportRoot = new ExportRootDirectory(exportBase, attachmentId, attachmentVersion);
        DiagramFile file = new DiagramFile(exportRoot);
        File exported = file.getFile(0);
        if (exported == null || exported.exists() == false) {
            return new DefaultImagePlaceholder(LOADING_IMAGE_PATH, false, new ImageDimensions(480, 320));
        }
        return new DefaultImagePlaceholder(imagePath, false, new ImageDimensions(480, 320));
    }

    private String getImagePath(int pageNumber, String attachmentId, String attachmentVersion) {
        return "/rest/astah/1.0/attachment/image/" + attachmentId + "/"+ attachmentVersion + "/" + pageNumber + ".png";
    }

}
