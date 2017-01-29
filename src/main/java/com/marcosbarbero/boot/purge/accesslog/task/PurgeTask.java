/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marcosbarbero.boot.purge.accesslog.task;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.list;
import static java.time.Instant.now;
import static java.time.Instant.ofEpochMilli;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * The type Purge task.
 *
 * @author Matheus Góes
 * @author Marcos Barbero
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
	private final Path accessLogDir;
	/**
	 * The Pattern.
	 */
	private final String pattern;
	/**
	 * The Current log file name supplier.
	 */
	private final Supplier<String> currentLogFileNameSupplier;

	/**
	 * Instantiates a new Purge task.
	 *
	 * @param purgeProperties the purge properties
	 * @param directory the directory
	 * @param prefix the prefix
	 * @param suffix the suffix
	 * @param currentLogFileNameSupplier the current log file name supplier
	 */
	public PurgeTask(final PurgeProperties purgeProperties, final Path directory,
			final String prefix, final String suffix,
			final Supplier<String> currentLogFileNameSupplier) {
		this.purgeProperties = purgeProperties;
		this.accessLogDir = directory;
		this.pattern = this.buildPattern(prefix, suffix);
		this.currentLogFileNameSupplier = currentLogFileNameSupplier;
	}

	/**
	 * Lists all files from access log directory and checks if they are eligible for
	 * purge.
	 */
	@Override
	public void run() {
		try {
			list(this.accessLogDir).filter(this::isPurgeable).forEach(this::purge);
		}
		catch (final IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Checks if this file is not the current access log file and if it is eligible for
	 * purge according to the last modified time.
	 *
	 * @param accessLogPath file from access log directory
	 * @return A boolean indicating if this file is eligible, or not, for purge
	 */
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

	/**
	 * Deletes the eligible access log file.
	 *
	 * @param accessLogPath the file that will be deleted
	 */
	private void purge(final Path accessLogPath) {
		try {
			deleteIfExists(accessLogPath);
		}
		catch (final IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Builds the filename pattern from access log prefix and suffix to check if the file
	 * is eligible for purge.
	 *
	 * @param prefix the access log prefix
	 * @param suffix the access log suffix
	 * @return The filename pattern
	 */
	private String buildPattern(final String prefix, final String suffix) {
		return new StringBuilder().append(this.escape(prefix))
				.append(ANY_CHARACTER_PATTERN).append(this.escape(suffix))
				.append(ANY_CHARACTER_PATTERN).toString();
	}

	/**
	 * Escapes all dot characters from the text.
	 *
	 * @param text String that must be escaped
	 * @return The escaped String
	 */
	private String escape(final String text) {
		return text.replace(".", "\\.");
	}
}
