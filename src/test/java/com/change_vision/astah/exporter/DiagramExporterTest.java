package com.change_vision.astah.exporter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DiagramExporterTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private String ASTAH_BASE;

	private String OUTPUT_BASE;
	
	@Before
	public void before() throws Exception {
		ASTAH_BASE = folder.newFolder().getAbsolutePath();
		OUTPUT_BASE = folder.newFolder().getAbsolutePath();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void export_with_null() throws Exception {
		DiagramExporter exporter = new DiagramExporter(ASTAH_BASE, OUTPUT_BASE);
		exporter.export(null);
	}

}
