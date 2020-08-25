package htw.prog3.ui.cli.control;

import java.util.regex.Pattern;

public class ValidationPattern {
    public static final Pattern STATE_CHANGE_COMMAND = Pattern.compile(":\\s*(config|[cdrup])(\\s+|\\b)");
    public static final Pattern SINGLE_WORD = Pattern.compile("\\s*\\w+\\s*");
    public static final Pattern TWO_WORDS = Pattern.compile("\\s*\\w+\\s+\\w+\\s*");
    public static final Pattern MULTIPLE_HAZARDS_CONTAINED = Pattern.compile("\\s+\\w{2,}\\s*(,\\s*\\w{2,}\\s*)+");
    public static final Pattern ADD_CARGO_SYNTAX =
            Pattern.compile("\\s*(?:\\w+\\s+){2}\\d+(.\\d+)?\\s+\\d+(?:\\s*,|(?:\\s+\\w{2,}\\s*(?:,\\s*\\w+\\s*){0,3}))\\s*[YNyn]\\s+[YNyn]\\s*");
    public static final Pattern GROUP1_WORD_FOLLOWED_BY_COMMA = Pattern.compile("(\\w+)\\s*,\\s*");
    public static final Pattern DIGIT = Pattern.compile("\\s*\\d+\\s*");
    public static final Pattern SINGLE_WORD_FOLLOWED_BY_DIGIT = Pattern.compile("\\s*\\w+\\s+\\d+\\s*");
    public static final Pattern WHITESPACE_SEQUENCE = Pattern.compile("\\s+");
    public static final Pattern BLANK = Pattern.compile("\\s*");
}