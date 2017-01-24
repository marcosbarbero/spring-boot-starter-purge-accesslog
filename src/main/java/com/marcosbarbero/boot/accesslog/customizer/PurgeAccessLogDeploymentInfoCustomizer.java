package com.marcosbarbero.boot.accesslog.customizer;

import com.marcosbarbero.boot.accesslog.prototype.PurgeProperties;
import com.marcosbarbero.boot.accesslog.runnable.PurgeTask;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.undertow.servlet.api.DeploymentInfo;

import static java.lang.Runtime.getRuntime;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * @author Marcos Barbero
 * @author Matheus Góes
 * @since 2017-01-24
 */
public class PurgeAccessLogDeploymentInfoCustomizer
        implements UndertowDeploymentInfoCustomizer {

    private final PurgeProperties purgeProperties;
    private final ServerProperties.Undertow.Accesslog accesslog;

    public PurgeAccessLogDeploymentInfoCustomizer(final PurgeProperties purgeProperties,
                                                  final ServerProperties.Undertow.Accesslog accesslog) {
        this.purgeProperties = purgeProperties;
        this.accesslog = accesslog;
    }

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        long initialDelay = 0;

        if (!this.purgeProperties.isExecuteOnStartup()) {
            final Calendar baseDate = Calendar.getInstance();
            baseDate.set(HOUR_OF_DAY, 0);
            baseDate.set(MINUTE, 0);
            baseDate.set(SECOND, 0);
            baseDate.set(MILLISECOND, 0);
            baseDate.add(DAY_OF_MONTH, 1);

            final long midnight = baseDate.getTimeInMillis();
            final long now = new Date().getTime();

            initialDelay = midnight - now;
        }

        final PurgeTask purgeTask = new PurgeTask(this.purgeProperties, this.accesslog);
        final long executionInterval = this.purgeProperties.getExecutionInterval();
        final TimeUnit executionIntervalUnit = this.purgeProperties
                .getExecutionIntervalUnit();

        Executors.newScheduledThreadPool(getRuntime().availableProcessors())
                .scheduleWithFixedDelay(purgeTask, initialDelay, executionInterval,
                        executionIntervalUnit);
    }
}
