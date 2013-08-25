package de.leanovate.jbj.api;

import javax.annotation.Nonnull;
import java.io.OutputStream;

/**
 * JBJ runtime environment.
 * <p/>
 * Usually you only need one of theses for all your PHP scripts.
 */
public interface JbjEnvironment {

    /**
     * Just run a PHP script.
     *
     * @param phpScript the filename of the PHP script to be found by the {@link JbjScriptLocator}
     * @param output    where to write the stdout to
     */
    void run(String phpScript, OutputStream output);

    /**
     * Run a PHP script with command line arguments.
     *
     * @param phpScript the filename of the PHP script to be found by the {@link JbjScriptLocator}
     * @param output    where to write the stdout to
     * @oaram args the command line arguments
     */
    void run(String phpScript, String[] args, OutputStream output);

    /**
     * Run a PHP script within the context of a HTTP request.
     *
     * @param phpScript the filename of the PHP script to be found by the {@link JbjScriptLocator}
     * @param request   HTTP request information
     * @param output    where to write the stdout to
     */
    void run(String phpScript, RequestInfo request, OutputStream output);

    /**
     * JBJ environment builder.
     */
    abstract class Builder {
        protected JbjSettings settings = new JbjSettings();
        protected JbjScriptLocator scriptLocator = new DefaultJbjScriptLocator();
        protected OutputStream errorStream = null;

        Builder withSettings(JbjSettings settings) {
            this.settings = settings;
            return this;
        }

        Builder withScriptLocator(JbjScriptLocator scriptLocator) {
            this.scriptLocator = scriptLocator;
            return this;
        }

        Builder withErrStream(OutputStream errorStream) {
            this.errorStream = errorStream;
            return this;
        }

        /**
         * Build the environment.
         */
        @Nonnull
        abstract JbjEnvironment build();
    }
}
