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

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.Supplier;

import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.Context;
import org.apache.catalina.valves.AccessLogValve;

/**
 * The type Tomcat purge access log holder.
 *
 * @author Matheus Góes
 * @author Marcos Barbero
 */
@Slf4j
public class TomcatPurgeAccessLogHolder extends PurgeAccessLogHolder
		implements TomcatContextCustomizer {

	/**
	 * The Current log file field.
	 */
	static final String CURRENT_LOG_FILE_FIELD = "currentLogFile";

	/**
	 * Instantiates a new Tomcat purge access log holder.
	 *
	 * @param purgeProperties the purge properties
	 * @param directory the directory
	 * @param prefix the prefix
	 * @param suffix the suffix
	 * @param accessLogValve the access log valve
	 */
	public TomcatPurgeAccessLogHolder(final PurgeProperties purgeProperties,
			final Path directory, final String prefix, final String suffix,
			final AccessLogValve accessLogValve) {
		super(purgeProperties, directory, prefix, suffix,
				createCurrentLogFileNameSupplier(accessLogValve));
	}

	/**
	 * Retrieves current access log file name from AccessLogValve to avoid accidental
	 * exclusion.
	 *
	 * @param accessLogValve the access log valve
	 * @return A Supplier that knows how to retrieve the file name
	 */
	private static Supplier<String> createCurrentLogFileNameSupplier(
			final AccessLogValve accessLogValve) {
		return () -> {
			String fileName = null;
			try {
				final Field currentLogFileField = findField(accessLogValve.getClass(),
						CURRENT_LOG_FILE_FIELD, File.class);
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
		};
	}

	/**
	 * Customize.
	 *
	 * @param context the context
	 */
	@Override
	public void customize(final Context context) {
		super.attachPurgeTask();
	}
}
