/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

import de.leanovate.jbj.runtime.JbjExtension;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

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
    void run(String phpScript, RequestInfo request, Response output);

    /**
     * JBJ environment builder.
     */
    abstract class Builder {
        protected JbjSettings settings = new JbjSettings();
        protected JbjScriptLocator scriptLocator = new DefaultJbjScriptLocator();
        protected JbjProcessExecutor processExecutor = new DefaultJbjProcessExecutor();
        protected FileSystem fileSystem = FileSystems.getDefault();
        protected OutputStream errorStream = null;
        protected List<JbjExtension> extendions = new ArrayList<>();

        public Builder withSettings(JbjSettings settings) {
            this.settings = settings;
            return this;
        }

        public Builder withScriptLocator(JbjScriptLocator scriptLocator) {
            this.scriptLocator = scriptLocator;
            return this;
        }

        public Builder withProcessExecutor(JbjProcessExecutor processExecutor) {
            this.processExecutor = processExecutor;
            return this;
        }

        public Builder withFileSystem(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
            return this;
        }

        public Builder withErrStream(OutputStream errorStream) {
            this.errorStream = errorStream;
            return this;
        }

        public Builder withExtension(JbjExtension extension) {
            extendions.add(extension);
            return this;
        }

        /**
         * Build the environment.
         */
        @Nonnull
        public abstract JbjEnvironment build();
    }
}
