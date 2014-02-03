package com.change_vision.astah.macro;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;

@RunWith(MockitoJUnitRunner.class)
public class DiagramsMacroParameterTest {

    private Map<String, String> params;
    private DiagramsMacroParameter parameter;
    @Mock
    private ConversionContext conversionContext;
    @Mock
    private AttachmentManager attachmentManager;
    @Mock
    private PageManager pageManager;
    @Mock
    private ContentEntityObject currentPage;
    @Mock
    private Page otherPage;
    @Mock
    private Attachment attachment;
    
    @Before
    public void before() throws Exception {
        params = new HashMap<String, String>();
        parameter = new DiagramsMacroParameter(pageManager, attachmentManager, conversionContext, params);
    }

    @Test
    public void getPageNumberReturn0WhenNull() {
        int pageNumber = parameter.getPageNumber();
        assertThat(pageNumber,is(0));
    }
    
    @Test
    public void getPageNumberReturnCorrectNumberFormat() throws Exception {
        params.put("number", "2"); // 0 start
        int pageNumber = parameter.getPageNumber();
        assertThat(pageNumber,is(1));
    }
    
    @Test
    public void getPageNumberReturn0WhenCannotParseNumberFormat() throws Exception {
        params.put("number", "number");
        int pageNumber = parameter.getPageNumber();
        assertThat(pageNumber,is(0));
    }
    
    @Test
    public void getAttachmentNameWhenNull() throws Exception {
        String attachmentName = parameter.getAttachmentName();
        assertThat(attachmentName,is(nullValue()));
    }
    
    @Test
    public void getAttachmentNameWhenSet() throws Exception {
        params.put("name", "attachment name");
        String attachmentName = parameter.getAttachmentName();
        assertThat(attachmentName,is("attachment name"));
    }
    
    @Test
    public void getPageNameWhenNull() throws Exception {
        String pageName = parameter.getPageName();
        assertThat(pageName,is(nullValue()));
    }

    @Test
    public void getPageNameWhenSet() throws Exception {
        params.put("page", "Page Name");
        String pageName = parameter.getPageName();
        assertThat(pageName,is("Page Name"));
    }
    
    @Test
    public void getAttachmentWhenNull() throws Exception {
        Attachment attachment = parameter.getAttachment();
        assertThat(attachment,is(nullValue()));
    }
    
    @Test
    public void getAttachementWhenSet() throws Exception {
        when(conversionContext.getEntity()).thenReturn(currentPage);
        when(attachmentManager.getAttachment(currentPage, "attachment.txt")).thenReturn(attachment);
        params.put("name", "attachment.txt");
        Attachment actual = parameter.getAttachment();
        assertThat(actual,is(attachment));
    }
    
    @Test
    public void getAttachmentWhenPageNameSet() throws Exception {
        when(conversionContext.getEntity()).thenReturn(currentPage);
        when(conversionContext.getSpaceKey()).thenReturn("freespace");
        when(pageManager.getPage("freespace", "FooBar")).thenReturn(otherPage);
        when(attachmentManager.getAttachment(otherPage, "attachment.txt")).thenReturn(attachment);
        params.put("name", "attachment.txt");
        params.put("page", "FooBar");
        Attachment actual = parameter.getAttachment();
        assertThat(actual,is(attachment));
    }
    
}
