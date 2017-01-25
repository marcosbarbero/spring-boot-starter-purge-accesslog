package com.marcosbarbero.boot.purge.accesslog.holder;

import java.nio.file.Path;
import java.util.concurrent.Executors;

import static java.lang.Runtime.getRuntime;
import static java.time.LocalDateTime.now;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.temporal.ChronoUnit.MILLIS;
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

	protected void attachPurgeTask() {
		long initialDelay = 0;

		if (!this.purgeProperties.isExecuteOnStartup()) {
			initialDelay = MILLIS.between(now(), MIDNIGHT);
		}

		final PurgeTask purgeTask = new PurgeTask(this.purgeProperties, this.directory,
				this.prefix, this.suffix);
		final long executionInterval = this.purgeProperties.getExecutionInterval();
		final String executionIntervalUnit = this.purgeProperties
				.getExecutionIntervalUnit().name();

		Executors.newScheduledThreadPool(getRuntime().availableProcessors())
				.scheduleWithFixedDelay(purgeTask, initialDelay, executionInterval,
						valueOf(executionIntervalUnit));
	}

}
