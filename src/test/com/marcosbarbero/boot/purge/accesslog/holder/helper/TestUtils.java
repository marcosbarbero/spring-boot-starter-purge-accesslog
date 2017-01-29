package com.marcosbarbero.boot.purge.accesslog.holder.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectory;
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
	public static final String TEMP_DIR = "./target/purge_access_log_starter_test/";
	public static final String ACCESS_LOG_FILE_NAME_PATTERN = "%s%s.%s-%02d%s";
	public static final String CURRENT_FILE_NAME_PATTERN = "%s.%d-%02d-%d%s";

	public static void resetTestDirectory() throws IOException {
		final Path testDir = Paths.get(TEMP_DIR);
		if (!exists(testDir)) {
			createDirectory(testDir);
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

	public static void createOldAccessLogFiles(final int amount, final String prefix,
			final String suffix) throws IOException {
		for (int i = 0; i < amount; i++) {
			final String fileName = format(ACCESS_LOG_FILE_NAME_PATTERN, TEMP_DIR, prefix,
					now().format(YEAR_MONTH_FORMATTER), i + 1, suffix);
			createFile(Paths.get(fileName));
		}
	}

	public static String getCurrentAccessLogFileName(final String prefix,
			final String suffix, final boolean appendCurrentDate) {
		String fileName = null;
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