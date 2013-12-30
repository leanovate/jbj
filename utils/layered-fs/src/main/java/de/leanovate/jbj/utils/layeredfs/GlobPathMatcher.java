package de.leanovate.jbj.utils.layeredfs;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

public class GlobPathMatcher implements PathMatcher {
    final static String NAME = "glob";

    private final Path patternPath;
    private final List<Match> matches;

    public GlobPathMatcher(Path patternPath) {
        this.patternPath = patternPath;
        this.matches = new ArrayList<>(patternPath.getNameCount());
        for (int i = 0; i < patternPath.getNameCount(); ++i) {
            this.matches.add(convertToMatch(patternPath.getName(i).toString()));
        }
    }

    @Override
    public boolean matches(Path path) {
        if (path.isAbsolute() != this.patternPath.isAbsolute()) {
            return false;
        }
        LayeredAbstractPath elementPath = (LayeredAbstractPath) path;
        return this.matches(elementPath.nameElements, this.matches);
    }

    private boolean matches(List<String> elements, List<Match> matches) {
        if (elements.isEmpty()) {
            for (Match match : matches) {
                if (!match.isFlexible()) {
                    return false;
                }
            }
            return true;
        }

        String element = elements.get(0);
        if (elements.size() == 1) {
            for (int i = 0; i < matches.size(); ++i) {
                Match match = matches.get(i);
                if (!match.isFlexible()) {
                    if (!match.matches(element)) {
                        return false;
                    } else if (i == matches.size() - 1) {
                        return true;
                    } else {
                        List<Match> remainingMatches = matches.subList(i + 1, matches.size());
                        return this.matches(Collections.<String>emptyList(), remainingMatches);
                    }
                }
            }
        }

        if (matches.isEmpty()) {
            return false;
        }

        Match firstMatch = matches.get(0);
        if (!firstMatch.isFlexible()) {
            if (firstMatch.matches(element) && matches.size() > 1) {
                return this.matches(elements.subList(1, elements.size()), matches.subList(1, matches.size()));
            } else {
                return false;
            }
        } else {
            List<String> remainingElements = elements.subList(1, elements.size());
            return this.matches(remainingElements, matches)
                    || this.matches(remainingElements, matches.subList(1, matches.size()));
        }

    }

    private Match convertToMatch(String element) {
        if (element.equals("**")) {
            return FlexibleMatch.INSTANCE;
        }
        Stream stream = new Stream(element);
        StringBuilder buffer = new StringBuilder();

        this.parseGeneric(stream, buffer, ExitHandler.EMPTY, element);
        Pattern pattern = Pattern.compile(buffer.toString(), CASE_INSENSITIVE | UNICODE_CASE);
        return new PatternMatch(pattern);
    }

    private char parseGeneric(Stream stream, StringBuilder buffer, ExitHandler exitHandler, String element) {
        while (stream.hasNext()) {
            char next = stream.next();
            if (exitHandler.isExit(next)) {
                return next;
            }
            switch (next) {
                case '*':
                    buffer.append(".*");
                    break;
                case '?':
                    buffer.append('.');
                    break;
                case '[':
                    this.parseRange(stream, buffer, element);
                    break;
                case '{':
                    this.parseGroup(stream, buffer, element);
                    break;
                case '\\':
                    if (!stream.hasNext()) {
                        throw new PatternSyntaxException("\\must be followed by content", element, element.length() - 1);
                    }
                    buffer.append('\\').append(stream.next());
                    break;
                default:
                    this.appendSafe(next, buffer);
                    break;
            }
        }
        return exitHandler.endOfStream(element);
    }

    private void parseGroup(Stream stream, StringBuilder buffer, String element) {
        List<String> groups = new ArrayList<>(4);
        StringBuilder groupBuffer = new StringBuilder();

        while (this.parseGeneric(stream, groupBuffer, ExitHandler.GROUP, element) != '}') {
            groups.add(groupBuffer.toString());
            groupBuffer = new StringBuilder(groupBuffer.length());
        }
        groups.add(groupBuffer.toString());

        boolean first = true;
        buffer.append('(');
        for (String group : groups) {
            if (!first) {
                buffer.append('|');
            } else {
                first = false;
            }
            buffer.append('(');
            buffer.append(group);
            buffer.append(')');
        }
        buffer.append(')');

    }

    private void parseRange(Stream stream, StringBuilder buffer, String element) {
        StringBuilder rangeBuffer = new StringBuilder();
        this.parseGeneric(stream, rangeBuffer, ExitHandler.RANGE, element);

        buffer.append('[');
        buffer.append(rangeBuffer);
        buffer.append(']');
    }

    private void appendSafe(char c, StringBuilder buffer) {
        if (c == '^' || c == '$' || c == '.') {
            buffer.append('\\');
        }
        buffer.append(c);
    }

    interface Match {
        boolean isFlexible();

        boolean matches(String element);
    }

    static final class Stream {
        private final String contents;
        private int position;

        Stream(String contents) {
            this.contents = contents;
            this.position = 0;
        }

        boolean hasNext() {
            return this.position < this.contents.length();
        }

        char next() {
            char value = this.contents.charAt(this.position);
            this.position += 1;
            return value;
        }
    }

    enum FlexibleMatch implements Match {
        INSTANCE;

        @Override
        public boolean isFlexible() {
            return true;
        }

        @Override
        public boolean matches(String element) {
            return true;
        }

        @Override
        public String toString() {
            return "**";
        }
    }

    static final class PatternMatch implements Match {
        private final Pattern pattern;

        PatternMatch(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean isFlexible() {
            return false;
        }

        @Override
        public boolean matches(String element) {
            return this.pattern.matcher(element).matches();
        }

        @Override
        public String toString() {
            return this.pattern.toString();
        }
    }

    enum ExitHandler {
        EMPTY {
            @Override
            boolean isExit(char c) {
                return false;
            }

            @Override
            char endOfStream(String element) {
                return 0; // doesn't matter, will be ignored
            }

        },
        GROUP {
            @Override
            boolean isExit(char c) {
                return c == ',' || c == '}';
            }

            @Override
            char endOfStream(String element) {
                throw new PatternSyntaxException("expected }", element, element.length() - 1);
            }

        },
        RANGE {
            @Override
            boolean isExit(char c) {
                return c == ']';
            }

            @Override
            char endOfStream(String element) {
                throw new PatternSyntaxException("expected ]", element, element.length() - 1);
            }

        };

        abstract boolean isExit(char c);

        abstract char endOfStream(String element);
    }
}
