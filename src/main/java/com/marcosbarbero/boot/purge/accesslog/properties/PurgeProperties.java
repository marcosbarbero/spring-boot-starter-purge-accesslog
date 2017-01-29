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

package com.marcosbarbero.boot.purge.accesslog.properties;

import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties.PREFIX;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.EnumSet.of;
import static org.springframework.util.Assert.isTrue;

import lombok.Data;

/**
 * The type Purge properties.
 *
 * @author Marcos Barbero
 * @author Matheus Góes
 */
@Data
@ConfigurationProperties(PREFIX)
public class PurgeProperties implements InitializingBean {

	/**
	 * The constant PREFIX.
	 */
	public static final String PREFIX = "server.accesslog.purge";
	/**
	 * The constant ALLOWED_UNITS.
	 */
	private static final EnumSet<ChronoUnit> ALLOWED_UNITS = of(SECONDS, MINUTES, HOURS,
			DAYS);

	/**
	 * The Enabled.
	 */
	private boolean enabled;
	/**
	 * The Execute on startup.
	 */
	private boolean executeOnStartup;
	/**
	 * The Execution interval.
	 */
	private long executionInterval = 24;
	/**
	 * The Max history.
	 */
	private long maxHistory = 30;
	/**
	 * The Execution interval unit.
	 */
	private ChronoUnit executionIntervalUnit = HOURS;
	/**
	 * The Max history unit.
	 */
	private ChronoUnit maxHistoryUnit = DAYS;

	/**
	 * After properties set.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		isTrue(this.executionInterval > 0, "'executionInterval' must be greater than 0");
		isTrue(this.maxHistory > 0, "'maxHistory' must be greater than 0");
		isTrue(ALLOWED_UNITS.contains(this.executionIntervalUnit),
				"'executionIntervalUnit' must be one of the following units: SECONDS, MINUTES, HOURS, DAYS");
		isTrue(ALLOWED_UNITS.contains(this.maxHistoryUnit),
				"'maxHistoryUnit' must be one of the following units: SECONDS, MINUTES, HOURS, DAYS");
	}
}
