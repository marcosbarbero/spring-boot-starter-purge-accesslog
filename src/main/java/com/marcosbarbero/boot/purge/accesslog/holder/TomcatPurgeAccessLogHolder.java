package com.marcosbarbero.boot.purge.accesslog.holder;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.valves.AccessLogValve;

/**
 * @author Matheus Góes
 */
@Slf4j
public class TomcatPurgeAccessLogHolder extends PurgeAccessLogHolder
		implements TomcatContextCustomizer {

	public TomcatPurgeAccessLogHolder(final PurgeProperties purgeProperties,
			final Path directory, final String prefix, final String suffix,
			final AccessLogValve accessLogValve) {
		super(purgeProperties, directory, prefix, suffix, () -> {
			String fileName = null;
			try {
				final Field currentLogFileField = findField(accessLogValve.getClass(),
						"currentLogFile", File.class);
				makeAccessible(currentLogFileField);
				final File currentLogFile = (File) currentLogFileField
						.get(accessLogValve);
				if (currentLogFile != null) {
					fileName = currentLogFile.toPath().getFileName().toString();
				}
			}
			catch (final Exception e) {
				log.warn(e.getMessage(), e);
			}
			return fileName;
		});
	}

	@Override
	public void customize(final Context context) {
		super.attachPurgeTask();
	}
}
