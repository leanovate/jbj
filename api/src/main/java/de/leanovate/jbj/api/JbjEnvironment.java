package de.leanovate.jbj.api;

import javax.annotation.Nonnull;

/**
 * JBJ runtime environment.
 *
 * Usually you only need one of theses for all your PHP scripts.
 */
public interface JbjEnvironment {

    /**
     * JBJ environment builder.
     */
    public interface Builder {
        /**
         * Build the environment.
         */
        @Nonnull
        JbjEnvironment build();
    }
}
