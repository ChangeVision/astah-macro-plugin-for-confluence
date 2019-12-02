package com.change_vision.astah.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.setup.BootstrapManager;
import com.change_vision.astah.exporter.DiagramExportRunnableTest;
import com.change_vision.astah.file.AstahBaseDirectory;

@RunWith(MockitoJUnitRunner.class)
public class AstahBaseDirectoryTest {

    @Mock
    private BootstrapManager notInitializedBootstrapManager;

    @Mock
    private BootstrapManager bootstrapManager;

    @Before
    public void before() throws Exception {
        String validPath = DiagramExportRunnableTest.class.getResource(".").getFile();
        File localHome = new File(validPath);
        when(bootstrapManager.getLocalHome()).thenReturn(localHome);
        File invalidHome = new File("");
        when(notInitializedBootstrapManager.getLocalHome()).thenReturn(invalidHome);
    }

    @Test
    public void isNotValidWhenBootstrapManagerIsNotInitialized() {
        AstahBaseDirectory astahBase = new AstahBaseDirectory(notInitializedBootstrapManager);
        assertThat(astahBase.isValid(),is(false));
    }

    @Test
    public void isValidWhenBootstrapManagerIsInitialized() throws Exception {
        AstahBaseDirectory astahBase = new AstahBaseDirectory(bootstrapManager);
        assertThat(astahBase.isValid(),is(true));
    }

    @Test
    public void getGsnJarWithAgmlFile() throws Exception {
        AstahBaseDirectory astahBase = new AstahBaseDirectory(bootstrapManager);
        File astahJar = astahBase.getAstahJar(new File("test.agml"));
        assertThat(astahJar.getName(),is("astah-gsn.jar"));
    }

    @Test
    public void getCommunityJarWithAstaFile() throws Exception {
        AstahBaseDirectory astahBase = new AstahBaseDirectory(bootstrapManager);
        File astahJar = astahBase.getAstahJar(new File("test.asta"));
        assertThat(astahJar.getName(),is("astah-community.jar"));
    }

    @Test
    public void getCommunityJarWithTxtFile() throws Exception {
        AstahBaseDirectory astahBase = new AstahBaseDirectory(bootstrapManager);
        File astahJar = astahBase.getAstahJar(new File("test.txt"));
        assertThat(astahJar.getName(),is("astah-community.jar"));
    }
}
