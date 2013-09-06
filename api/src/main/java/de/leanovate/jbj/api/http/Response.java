package de.leanovate.jbj.api.http;

import java.io.OutputStream;

/**
 * The http response jbj should write its output too.
 * <p/>
 * Note that - usually - PHP operates with an output buffer, i.e. the result is not written direct on the first lines.
 * This allows PHP to write its own headers. All jbj can assure is, that {@link #getOutputStream} is only called
 * when the output buffer is flushed (either programmatic or when the buffer limit is reached).
 * <p/>
 * Actually there is no guarantee that {@link #setHeader} is not called after that, but the implementor can
 * safely ignore these called, chances are that Apache/mod_php is doing just the same.
 */
public interface Response {
    /**
     * Set the HTTP response status.
     *
     * If not set explicitly "200 OK" is assumed.
     *
     * @param code the HTTP status/error code
     * @param message the status/error message
     */
    void setStatus(int code, String message);

    /**
     * Set/define a HTTP response header.
     *
     * @param name  the name of the header
     * @param value the value
     */
    void setHeader(String name, String value);

    /**
     * Get the outputstream of the body.
     * <p/>
     * All calls to {@link @setHeader} or {@link #setStatus} may be ignored from here on.
     */
    OutputStream getOutputStream();
}
