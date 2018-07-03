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

package com.marcosbarbero.boot.purge.accesslog.holder;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;
import com.marcosbarbero.boot.purge.accesslog.task.PurgeTask;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.valueOf;

/**
 * The type Purge access log holder.
 *
 * @author Matheus Góes
 */
@RequiredArgsConstructor
public abstract class PurgeAccessLogHolder {

	/**
	 * The Purge properties.
	 */
	private final PurgeProperties purgeProperties;
	/**
	 * The Directory.
	 */
	private final Path directory;
	/**
	 * The Prefix.
	 */
	private final String prefix;
	/**
	 * The Suffix.
	 */
	private final String suffix;
	/**
	 * The Current log file name supplier.
	 */
	private final Supplier<String> currentLogFileNameSupplier;

	/**
	 * Creates a scheduled thread pool and schedules the purge task according to the
	 * properties. If executeOnStartup property is false, then the task is scheduled to
	 * midnight of the next day.
	 */
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

		ThreadFactory threadFactory = r -> new Thread(r, "access-log-purge-worker");
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(threadFactory);

		executor.scheduleWithFixedDelay(purgeTask,
										initialDelay,
										executionInterval,
										valueOf(executionIntervalUnit));
	}

}
