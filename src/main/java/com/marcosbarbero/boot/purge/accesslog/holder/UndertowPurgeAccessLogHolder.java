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

import java.nio.file.Path;

import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import io.undertow.servlet.api.DeploymentInfo;

/**
 * The type Undertow purge access log holder.
 *
 * @author Matheus Góes
 */
public class UndertowPurgeAccessLogHolder extends PurgeAccessLogHolder
		implements UndertowDeploymentInfoCustomizer {

	/**
	 * Instantiates a new Undertow purge access log holder.
	 *
	 * @param purgeProperties the purge properties
	 * @param directory       the directory
	 * @param prefix          the prefix
	 * @param suffix          the suffix
	 */
	public UndertowPurgeAccessLogHolder(final PurgeProperties purgeProperties,
			final Path directory, final String prefix, final String suffix) {
		super(purgeProperties, directory, prefix, suffix, () -> prefix + suffix);
	}

	/**
	 * Customize.
	 *
	 * @param deploymentInfo the deployment info
	 */
	@Override
	public void customize(final DeploymentInfo deploymentInfo) {
		this.attachPurgeTask();
	}
}
