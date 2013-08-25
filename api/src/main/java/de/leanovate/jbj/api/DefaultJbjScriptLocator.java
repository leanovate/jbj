package de.leanovate.jbj.api;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Default implementation of the {@link JbjScriptLocator} interface.
 * <p/>
 * This is just supposed to get you started in real world you want to replace this by something more sophisticated.
 */
public class DefaultJbjScriptLocator implements JbjScriptLocator {
    @Nullable
    @Override
    public String getETag(String filename) {
        File file = new File(filename);

        if (file.isFile() && file.canRead())
            return String.valueOf(file.lastModified());
        return null;
    }

    @Nullable
    @Override
    public Script readScript(String filename) {
        File file = new File(filename);

        if (file.isFile() && file.canRead()) {
            try (InputStream in = new FileInputStream(file);
                 Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"))) {
                StringBuilder builder = new StringBuilder();
                char buffer[] = new char[8192];
                int read;

                while ((read = reader.read(buffer)) > 0) {
                    builder.append(buffer, 0, read);
                }

                return new Script(file.getAbsolutePath(), String.valueOf(file.lastModified()), builder.toString());
            } catch (IOException e) {
                throw new JbjException(e);
            }
        } else {
            return null;
        }
    }
}
