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

import com.marcosbarbero.boot.purge.accesslog.holder.TomcatPurgeAccessLogHolder;
import com.marcosbarbero.boot.purge.accesslog.holder.UndertowPurgeAccessLogHolder;
import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Accesslog;
import org.springframework.boot.autoconfigure.web.ServerProperties.Undertow;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

import static com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties.PREFIX;

/**
 * The type Purge access log auto configuration.
 */
@Configuration
@EnableConfigurationProperties(PurgeProperties.class)
@ConditionalOnClass(ServerProperties.class)
@ConditionalOnProperty(name = PREFIX + ".enabled", havingValue = "true")
public class PurgeAccessLogAutoConfiguration {

    /**
     * The type Undertow purge access log configuration.
     */
    @ConditionalOnClass(io.undertow.Undertow.class)
    @ConditionalOnProperty(name = "server.undertow.accesslog.enabled", havingValue = "true")
    public static class UndertowPurgeAccessLogConfiguration {

        /**
         * Purge access log customizer embedded servlet container customizer.
         *
         * @param serverProperties the server properties
         * @param purgeProperties  the purge properties
         * @return the embedded servlet container customizer
         */
        @Bean
        public EmbeddedServletContainerCustomizer purgeAccessLogCustomizer(
                final ServerProperties serverProperties,
                final PurgeProperties purgeProperties) {
            return container -> {
                final UndertowEmbeddedServletContainerFactory factory = (UndertowEmbeddedServletContainerFactory)
                        container;
                final Undertow.Accesslog accesslog = serverProperties.getUndertow()
                        .getAccesslog();
                final UndertowPurgeAccessLogHolder accessLogHolder = new UndertowPurgeAccessLogHolder(
                        purgeProperties, accesslog.getDir().toPath(),
                        accesslog.getPrefix(), accesslog.getSuffix());
                factory.addDeploymentInfoCustomizers(accessLogHolder);
            };
        }
    }

    /**
     * The type Tomcat purge access log configuration.
     */
    @ConditionalOnClass(AccessLogValve.class)
    @ConditionalOnProperty(name = "server.tomcat.accesslog.enabled", havingValue = "true")
    public static class TomcatPurgeAccessLogConfiguration {

        /**
         * Purge access log customizer embedded servlet container customizer.
         *
         * @param serverProperties the server properties
         * @param purgeProperties  the purge properties
         * @return the embedded servlet container customizer
         */
        @Bean
        public EmbeddedServletContainerCustomizer purgeAccessLogCustomizer(
                final ServerProperties serverProperties,
                final PurgeProperties purgeProperties) {
            return container -> {
                final TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory) container;
                final Accesslog accesslog = serverProperties.getTomcat().getAccesslog();
                factory.getEngineValves().stream()
                        .filter(valve -> valve instanceof AccessLogValve)
                        .map(valve -> (AccessLogValve) valve).findFirst()
                        .ifPresent(valve -> {
                            final TomcatPurgeAccessLogHolder accessLogHolder = new TomcatPurgeAccessLogHolder(
                                    purgeProperties, Paths.get(accesslog.getDirectory()),
                                    accesslog.getPrefix(), accesslog.getSuffix(), valve);
                            factory.addContextCustomizers(accessLogHolder);
                        });
            };
        }
    }
}
