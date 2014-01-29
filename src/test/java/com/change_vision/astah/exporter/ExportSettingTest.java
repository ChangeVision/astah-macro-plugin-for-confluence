package com.change_vision.astah.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class ExportSettingTest {

    private ExportSetting setting;

    @Before
    public void before() throws Exception {
        setting = new ExportSetting();
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseWithNull() throws Exception {
        setting.parse(null);
    }

    @Test
    public void parseWithEmpty() {
        String[] args = setting.parse("");
        assertThat(args.length,is(0));
    }
    @Test
    public void parseWithSpaceAndTab() {
        String[] args = setting.parse(" \t");
        assertThat(args.length,is(0));
    }

    @Test
    public void parseWithALine() throws Exception {
        String[] args = setting.parse("-Xms=16m");
        assertThat(args.length,is(1));
        assertThat(args[0],is("-Xms=16m"));
    }

    @Test
    public void parseWithLines() throws Exception {
        String[] args = setting.parse("-Xms=16m\n-Xmx=384m");
        assertThat(args.length,is(2));
        assertThat(args[0],is("-Xms=16m"));
        assertThat(args[1],is("-Xmx=384m"));
    }
    
    @Test
    public void parseWithLinesAndEmptyLine() throws Exception {
        String[] args = setting.parse("-Xms=16m\n\n-Xmx=384m");
        assertThat(args.length,is(2));
        assertThat(args[0],is("-Xms=16m"));
        assertThat(args[1],is("-Xmx=384m"));
    }

    @Test
    public void parseWithLinesAndComment() throws Exception {
        String[] args = setting.parse("-Xms=16m\n#Heap Size\n-Xmx=384m");
        assertThat(args.length,is(2));
        assertThat(args[0],is("-Xms=16m"));
        assertThat(args[1],is("-Xmx=384m"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void loadWithNull() throws Exception {
        setting.load(null);
    }
    
    @Test
    public void loadWithEmptyFile() throws Exception {
        File file = File.createTempFile("setting", "ini");
        String[] args = setting.load(file);
        assertThat(args.length,is(0));
    }
    
    @Test
    public void loadWithSettingFile() throws Exception {
        URL resource = ExportSettingTest.class.getResource("export.ini");
        File file = new File(resource.getFile());
        String[] args = setting.load(file);
        assertThat(args.length,is(2));
        assertThat(args[0],is("-Xms=16m"));
        assertThat(args[1],is("-Xmx=384m"));
    }

}
