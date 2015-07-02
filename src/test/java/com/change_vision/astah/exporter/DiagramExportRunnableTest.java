package com.change_vision.astah.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.setup.BootstrapManager;
import com.change_vision.astah.file.AstahBaseDirectory;
import com.change_vision.astah.file.ExportBaseDirectory;

public class DiagramExportRunnableTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private AstahBaseDirectory astahBase;

    @Mock
    private AstahBaseDirectory deletedAsathBase;

    @Mock
    private ExportBaseDirectory exportBase;
    
    @Mock
    private BootstrapManager bootstrapManager;

    @Mock
    private Attachment attachment;

    @Mock
    private Attachment attachment6_8;

    @Mock
    private Attachment gsnAttachment;

    @Mock
    private Attachment noDiagramsAttachment;

    @Mock
    private Attachment errorAttachment;

    private Random random = new Random();

    private DiagramExportRunnable runnable;

    private File outputFolder;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        String confluenceHome = DiagramExportRunnableTest.class.getResource(".").getFile();
        when(bootstrapManager.getConfluenceHome()).thenReturn(confluenceHome);
        astahBase = new AstahBaseDirectory(bootstrapManager);

        when(exportBase.getDirectory()).thenReturn(folder.newFolder());

        folder.newFile("test.asta");
        outputFolder = folder.getRoot();

        {
            when(attachment.getId()).thenReturn(random.nextLong());
            when(attachment.getVersion()).thenReturn(1);
            when(attachment.getFileName()).thenReturn("test.asta");
            InputStream stream = DiagramExportRunnableTest.class.getResourceAsStream("Sample.asta");
            when(attachment.getContentsAsStream()).thenReturn(stream);
        }

        {
            when(attachment6_8.getId()).thenReturn(random.nextLong());
            when(attachment6_8.getVersion()).thenReturn(1);
            when(attachment6_8.getFileName()).thenReturn("6_8.asta");
            InputStream stream = DiagramExportRunnableTest.class.getResourceAsStream("astah_professional6.8(37)_remove_UseCaseDescription.asta");
            when(attachment6_8.getContentsAsStream()).thenReturn(stream);
        }

        {
            when(noDiagramsAttachment.getId()).thenReturn(random.nextLong());
            when(noDiagramsAttachment.getVersion()).thenReturn(1);
            when(noDiagramsAttachment.getFileName()).thenReturn("test.asta");
            InputStream stream = DiagramExportRunnableTest.class.getResourceAsStream("dependency.asta");
            when(noDiagramsAttachment.getContentsAsStream()).thenReturn(stream);
        }

        {
            when(errorAttachment.getId()).thenReturn(random.nextLong());
            when(errorAttachment.getVersion()).thenReturn(1);
            when(errorAttachment.getFileName()).thenReturn("test.asta");
            InputStream stream = DiagramExportRunnableTest.class.getResourceAsStream("test.txt");
            when(errorAttachment.getContentsAsStream()).thenReturn(stream);
        }

        {
            when(gsnAttachment.getId()).thenReturn(random.nextLong());
            when(gsnAttachment.getVersion()).thenReturn(1);
            when(gsnAttachment.getFileName()).thenReturn("test.agml");
            InputStream stream = DiagramExportRunnableTest.class.getResourceAsStream("Sample.agml");
            when(gsnAttachment.getContentsAsStream()).thenReturn(stream);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void export_with_null() throws Exception {
        new DiagramExportRunnable(null, astahBase, exportBase);
    }

    @Test
    public void export() throws Exception {
        runnable = new DiagramExportRunnable(attachment, astahBase, exportBase);
        runnable.setTmpRoot(outputFolder);
        runnable.run();
        assertThat(runnable.success, is(true));
        Collection<File> exportedFiles = FileUtils.listFiles(outputFolder, new String[]{"png"}, true);
        assertThat(exportedFiles.size(),is(27));
    }

    @Test
    public void export6_8() throws Exception {
        runnable = new DiagramExportRunnable(attachment6_8, astahBase, exportBase);
        runnable.setTmpRoot(outputFolder);
        runnable.run();
        assertThat(runnable.success, is(true));
        Collection<File> exportedFiles = FileUtils.listFiles(outputFolder, new String[]{"png"}, true);
        assertThat(exportedFiles.size(),is(48));
    }

    @Test
    public void exportGsn() throws Exception {
        runnable = new DiagramExportRunnable(gsnAttachment, astahBase, exportBase);
        runnable.setTmpRoot(outputFolder);
        runnable.run();
        assertThat(runnable.success, is(true));
        Collection<File> exportedFiles = FileUtils.listFiles(outputFolder, new String[]{"png"}, true);
        assertThat(exportedFiles.size(),is(2));
    }

    @Test
    public void exportWithNoDiagrams() throws Exception {
        runnable = new DiagramExportRunnable(noDiagramsAttachment, astahBase, exportBase);
        runnable.setTmpRoot(outputFolder);
        runnable.run();
        assertThat(runnable.success, is(false));
        Collection<File> exportedFiles = FileUtils.listFiles(outputFolder, new String[]{"png"}, true);
        assertThat(exportedFiles.size(),is(0));
    }

    @Test
    public void exportWhenAstahError() throws Exception {
        runnable = new DiagramExportRunnable(attachment, deletedAsathBase, exportBase);
        runnable.setTmpRoot(folder.getRoot());
        runnable.run();
        assertThat(runnable.success, is(false));
    }
}
