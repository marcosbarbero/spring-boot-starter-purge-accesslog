package com.marcosbarbero.boot.purge.accesslog.holder;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import org.apache.catalina.Context;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author Matheus Góes
 */
@Slf4j
public class TomcatPurgeAccessLogHolder extends PurgeAccessLogHolder
        implements TomcatContextCustomizer {

    private static final String FIELD_NAME = "currentLogFile";

    public TomcatPurgeAccessLogHolder(final PurgeProperties purgeProperties,
                                      final Path directory, final String prefix, final String suffix,
                                      final AccessLogValve accessLogValve) {
        super(purgeProperties, directory, prefix, suffix, getCurrentLogFileName(accessLogValve));
    }

    private static Supplier<String> getCurrentLogFileName(final AccessLogValve accessLogValve) {
        return () -> {
            String fileName = null;
            try {
                final Field currentLogFileField = findField(accessLogValve.getClass(), FIELD_NAME, File.class);
                makeAccessible(currentLogFileField);
                final File currentLogFile = (File) currentLogFileField
                        .get(accessLogValve);
                if (currentLogFile != null) {
                    fileName = currentLogFile.toPath().getFileName().toString();
                }
            } catch (final Exception e) {
                log.warn(e.getMessage(), e);
            }
            return fileName;
        };
    }

    @Override
    public void customize(final Context context) {
        super.attachPurgeTask();
    }
}
