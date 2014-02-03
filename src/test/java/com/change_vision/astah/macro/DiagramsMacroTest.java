package com.change_vision.astah.macro;

import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.BootstrapManager;

/**
 * Testing {@link com.change_vision.astah.DiagramsMacro}
 */
public class DiagramsMacroTest {

    @Mock
    private Attachment attachmentAstahFile;

    @Mock
    private AttachmentManager attachmentManager;
    
    @Mock
    private BootstrapManager bootstrapManager;
    
    @Mock
    private WritableDownloadResourceManager exportDownloadResourceManager;

    @Mock
    private ConversionContext conversionContext;

    @Mock
    private PageManager pageManager;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.validateMockitoUsage();
        when(attachmentAstahFile.getFileExtension()).thenReturn("asta");
        when(attachmentAstahFile.getFileName()).thenReturn("test.asta");
        InputStream stream = DiagramsMacroTest.class.getResourceAsStream("Sample.asta");
        when(attachmentAstahFile.getContentsAsStream()).thenReturn(stream);

    }

    @Test
    @Ignore("I can't find how to initialize ConfluenceVelocityManager")
    public void basic() throws MacroExecutionException {

        Page page = new Page();
        page.setTitle("hoge");
        page.addAttachment(attachmentAstahFile);

        when(conversionContext.getEntity()).thenReturn(page);
        when(attachmentManager.getAttachment(page, attachmentAstahFile.getFileName())).thenReturn(
                attachmentAstahFile);

        DiagramsMacro macro = new DiagramsMacro(attachmentManager, bootstrapManager, exportDownloadResourceManager, pageManager);
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", attachmentAstahFile.getFileName());
        macro.execute(params, page.getContentEntityObject().getBodyAsString(), conversionContext);
    }

}