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

package com.marcosbarbero.boot.purge.accesslog;

import java.nio.file.Paths;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Accesslog;
import org.springframework.boot.autoconfigure.web.ServerProperties.Undertow;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties.PREFIX;
import static java.lang.Boolean.TRUE;

import com.marcosbarbero.boot.purge.accesslog.holder.TomcatPurgeAccessLogHolder;
import com.marcosbarbero.boot.purge.accesslog.holder.UndertowPurgeAccessLogHolder;
import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import lombok.RequiredArgsConstructor;

import org.apache.catalina.valves.AccessLogValve;

/**
 * The type Purge access log auto configuration.
 *
 * @author Marcos Barbero
 * @author Matheus Góes
 */
@Configuration
@EnableConfigurationProperties(PurgeProperties.class)
@ConditionalOnClass(ServerProperties.class)
@ConditionalOnProperty(name = PREFIX + ".enabled", havingValue = "true")
public class PurgeAccessLogAutoConfiguration {

	/**
	 * Purge access log customizer purge access log customizer.
	 *
	 * @param serverProperties the server properties
	 * @param purgeProperties the purge properties
	 * @return the purge access log customizer
	 */
	@Bean
	public PurgeAccessLogCustomizer purgeAccessLogCustomizer(
			final ServerProperties serverProperties,
			final PurgeProperties purgeProperties) {
		return new PurgeAccessLogCustomizer(serverProperties, purgeProperties);
	}

	/**
	 * The type Purge access log customizer.
	 */
	@RequiredArgsConstructor
	static class PurgeAccessLogCustomizer implements EmbeddedServletContainerCustomizer {

		/**
		 * The Server properties.
		 */
		private final ServerProperties serverProperties;
		/**
		 * The Purge properties.
		 */
		private final PurgeProperties purgeProperties;

		/**
		 * Customize.
		 *
		 * @param container the container
		 */
		@Override
		public void customize(final ConfigurableEmbeddedServletContainer container) {
			if (container instanceof JettyEmbeddedServletContainerFactory) {
				return;
			}

			if (container instanceof TomcatEmbeddedServletContainerFactory) {
				this.configureTomcatContainerFactory(
						(TomcatEmbeddedServletContainerFactory) container);
			}
			else if (container instanceof UndertowEmbeddedServletContainerFactory) {
				this.configureUndertowContainerFactory(
						(UndertowEmbeddedServletContainerFactory) container);
			}
		}

		/**
		 * Configure tomcat container factory.
		 *
		 * @param factory the factory
		 */
		private void configureTomcatContainerFactory(
				final TomcatEmbeddedServletContainerFactory factory) {
			final Accesslog accesslog = this.serverProperties.getTomcat().getAccesslog();
			if (accesslog.isEnabled()) {
				factory.getEngineValves().stream()
						.filter(valve -> valve instanceof AccessLogValve)
						.map(valve -> (AccessLogValve) valve).findFirst()
						.ifPresent(valve -> {
							final TomcatPurgeAccessLogHolder accessLogHolder = new TomcatPurgeAccessLogHolder(
									this.purgeProperties,
									Paths.get(accesslog.getDirectory()),
									accesslog.getPrefix(), accesslog.getSuffix(), valve);
							factory.addContextCustomizers(accessLogHolder);
						});
			}
		}

		/**
		 * Configure undertow container factory.
		 *
		 * @param factory the factory
		 */
		private void configureUndertowContainerFactory(
				final UndertowEmbeddedServletContainerFactory factory) {
			final Undertow.Accesslog accesslog = this.serverProperties.getUndertow()
					.getAccesslog();
			if (TRUE.equals(accesslog.getEnabled())) {
				final UndertowPurgeAccessLogHolder accessLogHolder = new UndertowPurgeAccessLogHolder(
						this.purgeProperties, accesslog.getDir().toPath(),
						accesslog.getPrefix(), accesslog.getSuffix());
				factory.addDeploymentInfoCustomizers(accessLogHolder);
			}
		}
	}

}