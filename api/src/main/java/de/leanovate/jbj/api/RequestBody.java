package de.leanovate.jbj.api;

import javax.annotation.Nonnull;

/**
 * Generic request body.
 */
public interface RequestBody {
    @Nonnull
    String getContentType();
}
