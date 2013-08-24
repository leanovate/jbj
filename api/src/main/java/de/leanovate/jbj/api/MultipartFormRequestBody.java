package de.leanovate.jbj.api;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Multipart form request body.
 * <p/>
 * Use this in case of content type "multipart/form-data".
 */
public interface MultipartFormRequestBody extends RequestBody {
    @Nonnull
    Map<String, List<String>> getFormDataParts();

    @Nonnull
    List<FileData> getFileData();

    interface FileData {
        @Nonnull
        String getKey();

        @Nonnull
        String getFilename();

        @Nonnull
        String getContentType();

        @Nonnull
        String getTempfilePath();
    }
}
