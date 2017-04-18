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

package com.marcosbarbero.boot.purge.accesslog.holder.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.walk;
import static java.time.YearMonth.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

import lombok.NoArgsConstructor;

/**
 * @author Matheus Góes
 */
@NoArgsConstructor
public final class TestUtils {

	public static final DateTimeFormatter YEAR_MONTH_FORMATTER = ofPattern("yyyy-MM");
	public static final String ACCESS_LOG_FILE_NAME_PATTERN = "%s%s.%s-%02d%s";
	public static final String CURRENT_FILE_NAME_PATTERN = "%s.%d-%02d-%d%s";

	public static void resetTestDirectory(final String directory) throws IOException {
		final Path testDir = Paths.get(directory);
		if (!exists(testDir)) {
			createDirectories(testDir);
		}
		else {
			walk(testDir).filter(file -> !isDirectory(file)).forEach(file -> {
				try {
					delete(file);
				}
				catch (final IOException e) {
					rethrowRuntimeException(e);
				}
			});
		}
	}

	public static void createOldAccessLogFiles(final int amount, final String tempDir,
			final String prefix, final String suffix) throws IOException {
		for (int i = 0; i < amount; i++) {
			final String fileName = format(ACCESS_LOG_FILE_NAME_PATTERN, tempDir, prefix,
					now().format(YEAR_MONTH_FORMATTER), i + 1, suffix);
			createFile(Paths.get(fileName));
		}
	}

	public static String getCurrentAccessLogFileName(final String prefix,
			final String suffix, final boolean appendCurrentDate) {
		String fileName;
		if (appendCurrentDate) {
			final LocalDate now = LocalDate.now();
			fileName = format(CURRENT_FILE_NAME_PATTERN, prefix, now.getYear(),
					now.getMonthValue(), now.getDayOfMonth(), suffix);
		}
		else {
			fileName = prefix + suffix;
		}
		return fileName;
	}
}