package de.leanovate.jbj.api;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * JBJ settings.
 *
 * Basically this is a replacement for your php.ini.
 */
public class JbjSettings {
    public final static EnumSet<ErrorLevel> E_ALL = EnumSet.allOf(ErrorLevel.class);

    private Charset charset = Charset.forName("UTF-8");

    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    private EnumSet<ErrorLevel> errorReporting = E_ALL;

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public EnumSet<ErrorLevel> getErrorReporting() {
        return errorReporting;
    }

    public void setErrorReporting(EnumSet<ErrorLevel> errorReporting) {
        this.errorReporting = errorReporting;
    }

    enum ErrorLevel {
        E_ERROR(1),
        E_WARNING(2),
        E_PARSE(4),
        E_NOTICE(8),
        E_CORE_ERROR(16),
        E_CORE_WARNING(32),
        E_COMPILE_ERROR(64),
        E_COMPILE_WARNING(128),
        E_USER_ERROR(256),
        E_USER_WARNING(512),
        E_USER_NOTICE(1024),
        E_STRICT(2048),
        E_RECOVERABLE_ERROR(4096),
        E_DEPRECATED(8192),
        E_USER_DEPRECATED(16384);

        private int value;

        private ErrorLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
