package com.marcosbarbero.boot.purge.accesslog.holder;

import java.nio.file.Path;

import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import org.apache.catalina.Context;

/**
 * @author Matheus Góes
 */
public class TomcatPurgeAccessLogHolder extends PurgeAccessLogHolder
		implements TomcatContextCustomizer {

	public TomcatPurgeAccessLogHolder(final PurgeProperties purgeProperties,
			final Path directory, final String prefix, final String suffix) {
		super(purgeProperties, directory, prefix, suffix);
	}

	@Override
	public void customize(final Context context) {
		super.attachPurgeTask();
	}
}
