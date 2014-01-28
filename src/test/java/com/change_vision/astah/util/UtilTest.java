package com.change_vision.astah.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class UtilTest {

    private Util util;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() throws Exception {
        util = new Util();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileExtensionShouldThrowExceptionWhenFileNameIsNull() throws Exception {
        util.getFileExtension(null);
    }

    @Test
    public void fileExtensionShouldReturnEmptyWhenFileNameIsEmpty() throws Exception {
        File file = mockFile("");
        assertThat(util.getFileExtension(file), is(""));
    }

    @Test
    public void fileExtensionShouldReturnSameWhenFileNameDoesNotHaveExtension() throws Exception {
        File file = mockFile("test");
        assertThat(util.getFileExtension(file), is("test"));
    }

    @Test
    public void fileExtensionShouldReturnfileExtensionOnlyWhenFileNameHaveExtension()
            throws Exception {
        File file = mockFile("test.txt");
        assertThat(util.getFileExtension(file), is("txt"));
    }

    @Test
    public void fileExtensionShouldReturnBakfileExtensionOnlyWhenFileNameHaveExtension()
            throws Exception {
        File file = mockFile("test.txt.bak");
        assertThat(util.getFileExtension(file), is("bak"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void filenameShouldThrowExceptionWhenFileNameIsNull() throws Exception {
        util.getFilename(null);
    }

    @Test
    public void filenameShouldReturnEmptyWhenFileNameIsEmpty() throws Exception {
        File file = mockFile("");
        assertThat(util.getFilename(file), is(""));
    }

    @Test
    public void FilenameShouldReturnSameWhenFileNameDoesNotHaveExtension() throws Exception {
        File file = mockFile("test");
        assertThat(util.getFilename(file), is("test"));
    }

    @Test
    public void FilenameShouldReturnfileExtensionOnlyWhenFileNameHaveExtension() throws Exception {
        File file = mockFile("test.txt");
        assertThat(util.getFilename(file), is("test"));
    }

    @Test
    public void FilenameShouldReturnBakfileExtensionOnlyWhenFileNameHaveExtension()
            throws Exception {
        File file = mockFile("test.txt.bak");
        assertThat(util.getFilename(file), is("test.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void relativePathShouldThrowExceptionWhenBaseIsNull() throws Exception {
        File file = mock(File.class);
        util.getRelativePath(null, file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void relativePathShouldThrowExceptionWhenFileIsNull() throws Exception {
        String base = System.getProperty("java.io.tmpdir");
        util.getRelativePath(base, null);
    }

    @Test
    public void relativePathShouldReturnEmptyWhenTheSamePath() throws Exception {
        String relativePath = util.getRelativePath(folder.getRoot().getAbsolutePath(),
                folder.getRoot());
        assertThat(relativePath, is(""));
    }

    @Test
    public void relativePathShouldReturnFileNameWhenTheFileIsUnderTheRoot() throws Exception {
        String relativePath = util.getRelativePath(folder.getRoot().getAbsolutePath(),
                folder.newFile("test.txt"));
        assertThat(relativePath, is("test.txt"));
    }

    @Test
    public void relativePathShouldReturnFileNameWhenTheDirIsUnderTheRoot() throws Exception {
        String relativePath = util.getRelativePath(folder.getRoot().getAbsolutePath(),
                folder.newFolder("test"));
        assertThat(relativePath, is("test"));
    }

    @Test
    public void relativePathShouldReturnFileNameWhenTheFileIsUnderTheFolder() throws Exception {
        File parent = folder.newFolder("test");
        File target = new File(parent, "test.txt");
        String relativePath = util.getRelativePath(folder.getRoot().getAbsolutePath(), target);
        assertThat(relativePath, is("test" + File.separator + "test.txt"));
    }

    private File mockFile(String fileName) {
        File file = mock(File.class);
        when(file.getName()).thenReturn(fileName);
        return file;
    }

}
