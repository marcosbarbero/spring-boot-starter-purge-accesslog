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

import com.marcosbarbero.boot.accesslog.customizer.PurgeAccessLogDeploymentInfoCustomizer;
import com.marcosbarbero.boot.accesslog.prototype.PurgeProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Marcos Barbero
 * @since 2017-01-23
 */
@Configuration
@EnableConfigurationProperties(PurgeProperties.class)
@ConditionalOnClass(ServerProperties.class)
@ConditionalOnProperty(name = PurgeProperties.PREFIX + ".enabled", havingValue = "true")
public class PurgeAccessLogAutoConfiguration {

    @Bean
    public PurgeAccessLogCustomizer purgeAccessLogCustomizer(final ServerProperties serverProperties,
                                                             final PurgeProperties purgeProperties) {
        return new PurgeAccessLogCustomizer(serverProperties, purgeProperties);
    }

    protected static class PurgeAccessLogCustomizer
            implements EmbeddedServletContainerCustomizer {

        private final ServerProperties serverProperties;
        private final PurgeProperties purgeProperties;

        private boolean isTomcat;

        public PurgeAccessLogCustomizer(final ServerProperties serverProperties,
                                        final PurgeProperties purgeProperties) {
            this.serverProperties = serverProperties;
            this.purgeProperties = purgeProperties;
        }

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            if (container instanceof JettyEmbeddedServletContainerFactory) {
                return;
            }

            if (isAccessLogEnabled(container) && this.purgeProperties.isEnabled()) {
                if (isTomcat) {

                } else {
                    final UndertowEmbeddedServletContainerFactory factory = (UndertowEmbeddedServletContainerFactory)
                            container;
                    final ServerProperties.Undertow.Accesslog accesslog = this.serverProperties.getUndertow()
                            .getAccesslog();
                    final PurgeAccessLogDeploymentInfoCustomizer customizer = new
                            PurgeAccessLogDeploymentInfoCustomizer(this.purgeProperties, accesslog);
                    factory.addDeploymentInfoCustomizers(customizer);
                }
            }
        }

        private boolean isAccessLogEnabled(
                final ConfigurableEmbeddedServletContainer container) {
            boolean isAccessLogEnabled = false;
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                isAccessLogEnabled = this.serverProperties.getTomcat().getAccesslog()
                        .isEnabled();
                isTomcat = true;
            } else if (container instanceof UndertowEmbeddedServletContainerFactory) {
                isAccessLogEnabled = this.serverProperties.getUndertow().getAccesslog()
                        .getEnabled();
            }
            return isAccessLogEnabled;
        }
    }

}