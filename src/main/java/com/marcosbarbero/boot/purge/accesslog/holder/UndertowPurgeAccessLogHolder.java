package com.marcosbarbero.boot.purge.accesslog.holder;

import java.nio.file.Path;

import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * @author Matheus Góes
 */
public class UndertowPurgeAccessLogHolder extends PurgeAccessLogHolder
		implements UndertowDeploymentInfoCustomizer {

	public UndertowPurgeAccessLogHolder(final PurgeProperties purgeProperties,
			final Path directory, final String prefix, final String suffix) {
		super(purgeProperties, directory, prefix, suffix, () -> prefix + suffix);
	}

	@Override
	public void customize(final DeploymentInfo deploymentInfo) {
		this.attachPurgeTask();
	}
}
