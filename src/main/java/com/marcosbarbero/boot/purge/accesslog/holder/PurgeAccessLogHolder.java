package com.marcosbarbero.boot.purge.accesslog.holder;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static java.lang.Runtime.getRuntime;
import static java.time.LocalDateTime.now;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.valueOf;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;
import com.marcosbarbero.boot.purge.accesslog.task.PurgeTask;

import lombok.RequiredArgsConstructor;

/**
 * @author Matheus Góes
 */
@RequiredArgsConstructor
public abstract class PurgeAccessLogHolder {

	private final PurgeProperties purgeProperties;
	private final Path directory;
	private final String prefix;
	private final String suffix;
	private final Supplier<String> currentLogFileNameSupplier;

	protected void attachPurgeTask() {
		long initialDelay = 0;

		if (!this.purgeProperties.isExecuteOnStartup()) {
			final LocalDateTime now = now();
			final LocalDateTime midnight = now.plusDays(1).with(MIDNIGHT);
			initialDelay = MILLIS.between(now, midnight);
		}

		final PurgeTask purgeTask = new PurgeTask(this.purgeProperties, this.directory,
				this.prefix, this.suffix, this.currentLogFileNameSupplier);
		final long executionInterval = this.purgeProperties.getExecutionInterval();
		final String executionIntervalUnit = this.purgeProperties
				.getExecutionIntervalUnit().name();

		newScheduledThreadPool(getRuntime().availableProcessors()).scheduleWithFixedDelay(
				purgeTask, initialDelay, executionInterval,
				valueOf(executionIntervalUnit));
	}

}
