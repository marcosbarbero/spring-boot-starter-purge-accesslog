package com.marcosbarbero.boot.purge.accesslog.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Matheus Góes
 */
@Slf4j
public class PurgeTask implements Runnable {

	/**
	 * The constant ANY_CHARACTER_PATTERN.
	 */
	private static final String ANY_CHARACTER_PATTERN = ".*";
	/**
	 * The Purge properties.
	 */
	private final PurgeProperties purgeProperties;
	/**
	 * The Access log dir.
	 */
	private Path accessLogDir;
	/**
	 * The Current log file name.
	 */
	private String currentLogFileName;
	/**
	 * The Pattern.
	 */
	private String pattern;

	public PurgeTask(final PurgeProperties purgeProperties, final Path directory,
			final String prefix, final String suffix) {
		this.purgeProperties = purgeProperties;
		this.accessLogDir = directory;
		this.currentLogFileName = prefix + suffix;
		this.pattern = this.buildPattern(prefix, suffix);
	}

	@Override
	public void run() {
		log.trace("Purging access log files...");
		try {
			Files.list(this.accessLogDir).filter(this::isPurgeable).forEach(this::purge);
		}
		catch (final IOException e) {
			log.error(e.getMessage(), e);
		}
		log.trace("Purging finished!");
	}

	private boolean isPurgeable(final Path accessLogPath) {
		boolean purgeable = false;
		try {
			final String fileName = accessLogPath.getFileName().toString();
			log.trace("File name: {}", fileName);

			if (!this.currentLogFileName.equals(fileName)
					&& fileName.matches(this.pattern)) {
				final FileTime lastModifiedTime = Files
						.getLastModifiedTime(accessLogPath);
				final Instant lastModifiedInstant = Instant
						.ofEpochMilli(lastModifiedTime.toMillis());
				log.trace("Last modified instant: {}", lastModifiedInstant);

				final Instant now = Instant.now();
				log.trace("Now: {}", now);

				final ChronoUnit maxHistoryUnit = this.purgeProperties
						.getMaxHistoryUnit();
				final long between = maxHistoryUnit.between(lastModifiedInstant, now);
				log.trace("Between: {} {}", between, maxHistoryUnit);

				purgeable = between > this.purgeProperties.getMaxHistory();
			}

			log.trace("Purgeable: {}", purgeable);
		}
		catch (final IOException e) {
			log.error(e.getMessage(), e);
		}
		return purgeable;
	}

	private void purge(final Path accessLogPath) {
		try {
			final boolean deleted = Files.deleteIfExists(accessLogPath);
			log.trace("Deleted: {}", deleted);
		}
		catch (final IOException e) {
			log.error(e.getMessage(), e);
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
