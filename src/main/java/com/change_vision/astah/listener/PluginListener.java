package com.change_vision.astah.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

public class PluginListener implements DisposableBean {

	private EventPublisher eventPublisher;
	private String ASTAH_BASE;
	
    private static final Logger log = LoggerFactory.getLogger(PluginListener.class);

	public PluginListener(BootstrapManager bootstrapManager,
			EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		eventPublisher.register(this);
		ASTAH_BASE = bootstrapManager.getConfluenceHome() + File.separator + "astah";
		File base = new File(ASTAH_BASE);
		base.mkdir();
	}

	@EventListener
	public void pluginInstallEvent(PluginInstallEvent event) {
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL resource = classLoader.getResource("astah.zip");
		if (resource != null) {
			InputStream in;
			try {
				in = resource.openStream();
				ZipInputStream zin = new ZipInputStream(in);
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					File target = new File(ASTAH_BASE,ze.getName());
					log.info("Unzip:" + target);
					if(ze.isDirectory()){
						target.mkdir();
					}else{
						String path = target.getAbsolutePath();
						log.info("path:" +path);
						FileOutputStream fout = new FileOutputStream(path);
						IOUtils.copy(zin, fout);
						zin.closeEntry();
						fout.close();
					}
				}
				zin.close();
			} catch (IOException e) {
				log.error("error has occured when unzipping", e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
