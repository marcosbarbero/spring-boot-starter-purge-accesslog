/*
 * Copyright 2016 the original author or authors.
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

package com.marcosbarbero.boot.accesslog.prototype;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.marcosbarbero.boot.accesslog.prototype.PurgeProperties.PREFIX;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.util.Assert.isTrue;

import lombok.Data;

/**
 * @author Marcos Barbero
 * @since 2017-01-23
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class PurgeProperties implements InitializingBean {
	public static final String PREFIX = "server.accesslog.purge";

	private static final EnumSet<TimeUnit> ALLOWED_UNITS = EnumSet.of(SECONDS, MINUTES,
			HOURS, DAYS);

	private boolean enabled;
	private boolean executeOnStartup;
	private long executionInterval = 24;
	private long maxHistory = 30;
	private TimeUnit executionIntervalUnit = HOURS;
	private TimeUnit maxHistoryUnit = DAYS;

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
