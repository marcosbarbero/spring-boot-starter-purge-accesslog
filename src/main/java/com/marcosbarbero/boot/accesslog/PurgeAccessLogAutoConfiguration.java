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

package com.marcosbarbero.boot.accesslog;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.marcosbarbero.boot.accesslog.prototype.PurgeProperties;

/**
 * @author Marcos Barbero
 * @since 2017-01-23
 */
@Configuration
@ConditionalOnClass(ServerProperties.class)
@EnableConfigurationProperties(PurgeProperties.class)
@ConditionalOnProperty(name = PurgeProperties.PREFIX + ".enabled", havingValue = "true")
public class PurgeAccessLogAutoConfiguration {

	protected static class PurgeAccessLogCustomizer
			implements EmbeddedServletContainerCustomizer {

		private final ServerProperties serverProperties;
		private final PurgeProperties purgeProperties;

		public PurgeAccessLogCustomizer(ServerProperties serverProperties,
				PurgeProperties purgeProperties) {
			this.serverProperties = serverProperties;
			this.purgeProperties = purgeProperties;
		}

		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			// final UndertowEmbeddedServletContainerFactory factory =
			// (UndertowEmbeddedServletContainerFactory) container;
			// final Accesslog accesslog =
			// this.serverProperties.getUndertow().getAccesslog();
			// if (accesslog != null && TRUE.equals(accesslog.getEnabled()) &&
			// this.purgeProperties.isEnabled()) {
			// factory.addDeploymentInfoCustomizers(new
			// PurgeableAccessLogDeploymentInfoCustomizer(this.purgeProperties,
			// accesslog));
			// }

            if(container instanceof TomcatEmbeddedServletContainerFactory) {

            } else if(container instanceof UndertowEmbeddedServletContainerFactory) {

            } else if(container instanceof JettyEmbeddedServletContainerFactory) {

            }

            if (isAccessLogEnabled(container) && this.purgeProperties.isEnabled()) {

            }
        }

		private boolean isAccessLogEnabled(
				final ConfigurableEmbeddedServletContainer container) {
			return false;
		}
	}

}