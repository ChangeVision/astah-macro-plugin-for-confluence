package com.change_vision.astah.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.event.api.EventPublisher;
import com.change_vision.astah.exporter.DiagramExportRunnable;

public class AttachmentListenerTest {

    @Mock
    private BootstrapManager bootstrapManager;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AttachmentCreateEvent createEvent;

    @Mock
    private AttachmentUpdateEvent updateEvent;

    @Mock
    private Attachment attachmentTextFile;

    @Mock
    private Attachment attachmentAstahFile;

    @Mock
    private Attachment attachmentJudeFile;

    @Mock
    private Attachment attachmentJuthFile;

    @Mock
    private ScheduledExecutorService scheduledExecutorService;

    @Mock
    private Attachment attachmentNoExtensionFile;

    @Mock
    private Attachment attachementAgmlFile;

    private AttachmentListener listener;

    private ArrayList<Attachment> attachments;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        listener = new AttachmentListener(bootstrapManager, eventPublisher);
        listener.setScheduledExecutorService(scheduledExecutorService);

        attachments = new ArrayList<Attachment>();
        when(createEvent.getAttachments()).thenReturn(attachments);
        when(updateEvent.getAttachments()).thenReturn(attachments); // using same attachments

        when(attachmentTextFile.getFileExtension()).thenReturn("txt");
        when(attachmentTextFile.getFileName()).thenReturn("test.txt");

        when(attachmentAstahFile.getFileExtension()).thenReturn("asta");
        when(attachmentAstahFile.getFileName()).thenReturn("test.asta");

        when(attachmentJudeFile.getFileExtension()).thenReturn("jude");
        when(attachmentJudeFile.getFileName()).thenReturn("test.jude");

        when(attachmentJuthFile.getFileExtension()).thenReturn("juth");
        when(attachmentJuthFile.getFileName()).thenReturn("test.juth");

        when(attachmentNoExtensionFile.getFileExtension()).thenReturn("");
        when(attachmentNoExtensionFile.getFileName()).thenReturn("test");

        when(attachementAgmlFile.getFileExtension()).thenReturn("agml");
        when(attachementAgmlFile.getFileName()).thenReturn("test.agml");

    }

    @Test
    public void createWithNoAttachments() {
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService, never()).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachmentWithTextFile() throws Exception {
        when(attachmentTextFile.isNew()).thenReturn(true);
        attachments.add(attachmentTextFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService, never()).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachmentWithNoExtensionFile() throws Exception {
        when(attachmentNoExtensionFile.isNew()).thenReturn(true);
        attachments.add(attachmentNoExtensionFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService, never()).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachmentWithAstahFile() throws Exception {
        when(attachmentAstahFile.isNew()).thenReturn(true);
        attachments.add(attachmentAstahFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachmentWithJudeFile() throws Exception {
        when(attachmentJudeFile.isNew()).thenReturn(true);
        attachments.add(attachmentJudeFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachmentWithJuthFile() throws Exception {
        when(attachmentJuthFile.isNew()).thenReturn(true);
        attachments.add(attachmentJuthFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void createAttachementWithAgmlFile() throws Exception {
        when(attachementAgmlFile.isNew()).thenReturn(true);
        attachments.add(attachementAgmlFile);
        listener.attachmentCreateEvent(createEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachmentWithTextFile() throws Exception {
        attachments.add(attachmentTextFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService, never()).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachmentWithNoExtensionFile() throws Exception {
        attachments.add(attachmentNoExtensionFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService, never()).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachmentWithAstahFile() throws Exception {
        attachments.add(attachmentAstahFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachmentWithJudeFile() throws Exception {
        attachments.add(attachmentJudeFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachmentWithJuthFile() throws Exception {
        attachments.add(attachmentJuthFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

    @Test
    public void updateAttachementWithAgmlFile() throws Exception {
        attachments.add(attachementAgmlFile);
        listener.attachmentUpdateEvent(updateEvent);
        verify(scheduledExecutorService).execute(any(DiagramExportRunnable.class));
    }

}
