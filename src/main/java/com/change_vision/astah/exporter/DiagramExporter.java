package com.change_vision.astah.exporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.pages.Attachment;
import com.change_vision.astah.util.Util;

public class DiagramExporter {
	
	private static final Logger logger = LoggerFactory.getLogger(DiagramExporter.class);
	
	private final String ASTAH_BASE;

	private final String OUTPUT_BASE;
	
    private final File tmpRoot = new File(System.getProperty("java.io.tmpdir"));
	
	private final Util util = new Util();
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public DiagramExporter(String astahBase, String outputBase){
		this.ASTAH_BASE = astahBase;
		this.OUTPUT_BASE = outputBase;
	}

	public void export(final Attachment attachment) {
		// TODO think to use thread.
		Runnable runner = new Runnable() {
			public void run() {
				File file = storeToTempDir(attachment);
				logger.info("file : " + file.getAbsolutePath());
				
				String outputRoot = OUTPUT_BASE  + File.separator + String.valueOf(attachment.getId()) + File.separator + String.valueOf(attachment.getVersion());
				File javaHome = new File(System.getProperty("java.home"));
				File javaBin = new File(javaHome,"bin");
				File javaCommand = new File(javaBin,"java");
				String[] commands = new String[]{
					javaCommand.getAbsolutePath(),
					"-Xms16m",
					"-Xmx384m",
					"-Djava.awt.headless=true",
					"-cp",
					ASTAH_BASE + File.separator + "astah-community.jar",
					"com.change_vision.jude.cmdline.JudeCommandRunner",
					"-image",
					"all",
					"-resized",
					"-f",
					file.getAbsolutePath(),
					"-t",
					"png",
					"-o",
					outputRoot
				};
				for (String command: commands){
					logger.info("args:" + command);
				}
				ProcessBuilder builder = new ProcessBuilder(commands);
				builder.redirectErrorStream(true);
				Process p = null;
				try {
					p = builder.start();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				InputStream is = p.getInputStream();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(is));
					String line;
					while (null != (line = reader.readLine())) {
						logger.info(line);
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
				List<File> pngFiles = new ArrayList<File>();
				File parent = new File(outputRoot);
				traverseFiles(pngFiles,parent);
				
				exportFileIndexFile(outputRoot, pngFiles);
				exportDiagarmIndexFile(outputRoot, pngFiles);
			}
		};
		Thread exportThread = new Thread(runner);
		exportThread.start();
	}
	
	private void exportFileIndexFile(String outputRoot, List<File> pngFiles) {
		try {
			String[] filePaths = new String[pngFiles.size()];
			for (int index = 0; index < pngFiles.size(); index ++) {
				File pngFile = pngFiles.get(index);
				String relativePath = util.getRelativePath(OUTPUT_BASE,pngFile);
				filePaths[index] = relativePath;
			}
			mapper.writeValue(new File(outputRoot,"file.json"), filePaths);
		} catch (IOException e) {
			logger.error("error has occurred when making index file.",e);
		}
	}

	private void exportDiagarmIndexFile(String outputRoot, List<File> pngFiles) {
		try {
			String[] fileNames = new String[pngFiles.size()];
			for (int index = 0; index < pngFiles.size(); index ++) {
				File pngFile = pngFiles.get(index);
				String filename = util.getFilename(pngFile);
				fileNames[index] = filename;
			}
			mapper.writeValue(new File(outputRoot,"diagram.json"), fileNames);
		} catch (IOException e) {
			logger.error("error has occurred when making index file.",e);
		}
	}

	private void traverseFiles(List<File> pngFiles, File parent) {
		File[] files = parent.listFiles();
		for (File file : files) {
			if(file.isDirectory()){
				traverseFiles(pngFiles, file);
			}
			
			String suffix = util.getFileExtension(file);
			if(suffix != null && suffix.equalsIgnoreCase("png")){
				pngFiles.add(file);
			}
		}
	}

	private File storeToTempDir(Attachment attachment){
		try {
			long id = attachment.getId();
			File tmpDir = new File(tmpRoot,String.valueOf(id));
			tmpDir.mkdirs();
			File tmpFile = new File(tmpDir,attachment.getFileName());
			InputStream inputStream = attachment.getContentsAsStream();
			FileOutputStream outputStream = new FileOutputStream(tmpFile);
			IOUtils.copy(inputStream, outputStream);
			IOUtils.closeQuietly(outputStream);
			tmpFile.deleteOnExit();
			return tmpFile;
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(),e);
			return null;
		}
	}
 
}
