package de.leanovate.jbj.api;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * JBJ settings.
 * <p/>
 * Basically this is a replacement for your php.ini.
 */
public class JbjSettings implements Cloneable {
    public final static EnumSet<ErrorLevel> E_ALL = EnumSet.allOf(ErrorLevel.class);

    private Charset charset = Charset.forName("UTF-8");

    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    private EnumSet<ErrorLevel> errorReporting = EnumSet.of(ErrorLevel.E_ERROR, ErrorLevel.E_WARNING,
            ErrorLevel.E_PARSE, ErrorLevel.E_CORE_ERROR, ErrorLevel.E_CORE_WARNING, ErrorLevel.E_COMPILE_ERROR,
            ErrorLevel.E_COMPILE_WARNING, ErrorLevel.E_USER_ERROR, ErrorLevel.E_USER_WARNING, ErrorLevel.E_USER_NOTICE,
            ErrorLevel.E_RECOVERABLE_ERROR, ErrorLevel.E_DEPRECATED, ErrorLevel.E_USER_DEPRECATED);

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

    public JbjSettings clone() {
        try {
            return (JbjSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new JbjException(e);
        }
    }

    public static enum ErrorLevel {
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
