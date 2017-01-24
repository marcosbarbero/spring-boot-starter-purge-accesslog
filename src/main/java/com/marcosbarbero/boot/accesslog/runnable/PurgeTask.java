package com.marcosbarbero.boot.accesslog.runnable;

import com.marcosbarbero.boot.accesslog.prototype.PurgeProperties;

import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Matheus Góes
 * @since 2017-01-24
 */
@Slf4j
public class PurgeTask implements Runnable {

    /**
     * The constant ANY_CHARACTER_PATTERN.
     */
    private static final String ANY_CHARACTER_PATTERN = ".*";
    /**
     * The Purge properties.
     */
    private final PurgeProperties purgeProperties;
    /**
     * The Access log dir.
     */
    private final File accessLogDir;
    /**
     * The Current log file name.
     */
    private final String currentLogFileName;
    /**
     * The Pattern.
     */
    private final String pattern;

    /**
     * Instantiates a new Purge task.
     *
     * @param purgeProperties the purge properties
     * @param accesslog       the accesslog
     */
    public PurgeTask(final PurgeProperties purgeProperties,
                     final ServerProperties.Undertow.Accesslog accesslog) {
        this.purgeProperties = purgeProperties;
        this.accessLogDir = accesslog.getDir();
        this.currentLogFileName = accesslog.getPrefix() + accesslog.getSuffix();
        this.pattern = this.buildPattern(accesslog);
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        log.trace("Purging access log files...");
        final File[] files = this.accessLogDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return isPurgeable(dir, name);
            }
        });
        if (files != null) {
            for (final File file : files) {
                this.purge(file);
            }
        }
        log.trace("Purging finished!");
    }

    private boolean isPurgeable(final File file, final String fileName) {
        boolean purgeable = false;
        log.trace("File name: {}", fileName);

        if (!this.currentLogFileName.equals(fileName) && fileName.matches(this.pattern)) {
            final TimeUnit maxHistoryUnit = this.purgeProperties.getMaxHistoryUnit();

            final long lastModified = maxHistoryUnit.convert(file.lastModified(),
                    MILLISECONDS);
            log.trace("Last modified: {}", lastModified);

            final long now = maxHistoryUnit.convert(new Date().getTime(), MILLISECONDS);
            log.trace("Now: {}", now);

            final long between = now - lastModified;
            log.trace("Between: {} {}", between, maxHistoryUnit);

            purgeable = between > this.purgeProperties.getMaxHistory();
        }

        log.trace("Purgeable: {}", purgeable);
        return purgeable;
    }

    private void purge(final File accessLogFile) {
        try {
            final boolean deleted = accessLogFile.delete();
            log.trace("Deleted: {}", deleted);
        } catch (final SecurityException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Build pattern string.
     *
     * @param accesslog the accesslog
     * @return the string
     */
    private String buildPattern(final ServerProperties.Undertow.Accesslog accesslog) {
        return new StringBuilder()
                .append(this.normalizeDotCharacter(accesslog.getPrefix()))
                .append(ANY_CHARACTER_PATTERN)
                .append(this.normalizeDotCharacter(accesslog.getSuffix()))
                .append(ANY_CHARACTER_PATTERN).toString();
    }

    /**
     * Normalize dot character string.
     *
     * @param text the text
     * @return the string
     */
    private String normalizeDotCharacter(final String text) {
        return text.replace(".", "\\.");
    }
}
