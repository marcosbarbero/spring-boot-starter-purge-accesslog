package com.marcosbarbero.boot.purge.accesslog.task;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static java.nio.file.Files.*;
import static java.time.Instant.now;
import static java.time.Instant.ofEpochMilli;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Matheus Góes
 * @author Marcos Barbero
 */
@Slf4j
public class PurgeTask implements Runnable {

	private static final String ANY_CHARACTER_PATTERN = ".*";
	private final PurgeProperties purgeProperties;
	private final Path accessLogDir;
	private final String pattern;
	private final Supplier<String> currentLogFileNameSupplier;

	public PurgeTask(final PurgeProperties purgeProperties, final Path directory,
			final String prefix, final String suffix,
			final Supplier<String> currentLogFileNameSupplier) {
		this.purgeProperties = purgeProperties;
		this.accessLogDir = directory;
		this.pattern = this.buildPattern(prefix, suffix);
		this.currentLogFileNameSupplier = currentLogFileNameSupplier;
	}

	@Override
	public void run() {
		try {
			list(this.accessLogDir).filter(this::isPurgeable).forEach(this::purge);
		}
		catch (final IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	private boolean isPurgeable(final Path accessLogPath) {
		boolean purgeable = false;
		try {
			final String fileName = accessLogPath.getFileName().toString();

			if (!fileName.equals(this.currentLogFileNameSupplier.get())
					&& fileName.matches(this.pattern)) {
				final FileTime lastModifiedTime = getLastModifiedTime(accessLogPath);
				final Instant lastModifiedInstant = ofEpochMilli(
						lastModifiedTime.toMillis());
				final ChronoUnit maxHistoryUnit = this.purgeProperties
						.getMaxHistoryUnit();
				final long between = maxHistoryUnit.between(lastModifiedInstant, now());
				purgeable = between > this.purgeProperties.getMaxHistory();
			}

		}
		catch (final IOException e) {
			log.warn(e.getMessage(), e);
		}
		return purgeable;
	}

	private void purge(final Path accessLogPath) {
		try {
			deleteIfExists(accessLogPath);
		}
		catch (final IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	private String buildPattern(final String prefix, final String suffix) {
		return new StringBuilder().append(this.escape(prefix))
				.append(ANY_CHARACTER_PATTERN).append(this.escape(suffix))
				.append(ANY_CHARACTER_PATTERN).toString();
	}

	private String escape(final String text) {
		return text.replace(".", "\\.");
	}
}
