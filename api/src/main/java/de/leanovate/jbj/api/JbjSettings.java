/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

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

    /**
     * The initial charset.
     */
    private Charset charset = Charset.forName("UTF-8");

    /**
     * The initial timezone.
     * (Can be changed within a script.)
     */
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /**
     * Maximum size for output buffers.
     * <p/>
     * <ul>
     * <li>-1 means unlimited (use with care)</li>
     * <li>0 means disabled (i.e. all output is send directly)</li>
     * </ul>
     * See <a href="http://php.net/output-buffering">http://php.net/output-buffering</a> for details.
     */
    private int outputBuffering = 16384;

    /**
     * The initial logging level.
     * (Can be changed within a script.)
     */
    private EnumSet<ErrorLevel> errorReporting = EnumSet.of(ErrorLevel.E_ERROR, ErrorLevel.E_WARNING,
            ErrorLevel.E_PARSE, ErrorLevel.E_CORE_ERROR, ErrorLevel.E_CORE_WARNING, ErrorLevel.E_COMPILE_ERROR,
            ErrorLevel.E_COMPILE_WARNING, ErrorLevel.E_USER_ERROR, ErrorLevel.E_USER_WARNING, ErrorLevel.E_USER_NOTICE,
            ErrorLevel.E_RECOVERABLE_ERROR, ErrorLevel.E_DEPRECATED, ErrorLevel.E_USER_DEPRECATED);

    /**
     * Corresponds to: always_populate_raw_post_data.
     */
    private boolean alwaysPopulateRawPostData = false;

    /**
     * Corresponds to: post_max_size
     */
    private long postMaxSize = -1;

    /**
     * Corresponds to: max_input_nesting_level
     */
    private int maxInputNestingLevel = -1;

    /**
     * Corresponds to: track_errors
     */
    private boolean trackErrors = false;

    /**
     * Corresponds to: display_errors
     */
    private DisplayError displayErrors = DisplayError.STDOUT;

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

    public int getOutputBuffering() {
        return outputBuffering;
    }

    public void setOutputBuffering(int outputBuffering) {
        this.outputBuffering = outputBuffering;
    }

    public EnumSet<ErrorLevel> getErrorReporting() {
        return errorReporting;
    }

    public void setErrorReporting(EnumSet<ErrorLevel> errorReporting) {
        this.errorReporting = errorReporting;
    }


    public boolean isAlwaysPopulateRawPostData() {
        return alwaysPopulateRawPostData;
    }

    public void setAlwaysPopulateRawPostData(boolean alwaysPopulateRawPostData) {
        this.alwaysPopulateRawPostData = alwaysPopulateRawPostData;
    }

    public long getPostMaxSize() {
        return postMaxSize;
    }

    public void setPostMaxSize(long postMaxSize) {
        this.postMaxSize = postMaxSize;
    }

    public int getMaxInputNestingLevel() {
        return maxInputNestingLevel;
    }

    public void setMaxInputNestingLevel(int maxInputNestingLevel) {
        this.maxInputNestingLevel = maxInputNestingLevel;
    }

    public boolean isTrackErrors() {
        return trackErrors;
    }

    public void setTrackErrors(boolean trackErrors) {
        this.trackErrors = trackErrors;
    }

    public DisplayError getDisplayErrors() {
        return displayErrors;
    }

    public void setDisplayErrors(DisplayError displayErrors) {
        this.displayErrors = displayErrors;
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

        public static EnumSet<ErrorLevel> errorLevelsForValue(int errorReporting) {
            EnumSet<ErrorLevel> result = EnumSet.noneOf(ErrorLevel.class);

            for(ErrorLevel errorLevel : ErrorLevel.values()) {
                if ( (errorLevel.getValue() & errorReporting) != 0) {
                    result.add(errorLevel);
                }
            }

            return result;
        }
    }

    public static enum DisplayError {
        STDOUT,
        STDERR,
        NULL
    }
}
