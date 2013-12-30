package de.leanovate.jbj.utils.layeredfs;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.Pattern;

public class RegexPathMatcher implements PathMatcher {
    final static String NAME = "regex";

    private final Pattern pattern;

    RegexPathMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(Path path) {
        return this.pattern.matcher(path.toString()).matches();
    }
}
