package com.change_vision.astah.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.setup.BootstrapManager;

@RunWith(MockitoJUnitRunner.class)
public class AstahBaseDirectoryTest {

    @Mock
    private BootstrapManager notInitializedBootstrapManager;

    @Mock
    private BootstrapManager bootstrapManager;

    @Before
    public void before() throws Exception {
        String validPath = AstahBaseDirectoryTest.class.getResource(".").getFile();
        when(bootstrapManager.getConfluenceHome()).thenReturn(validPath);
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

}
