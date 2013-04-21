package npakudin;

import java.util.regex.Pattern;

/**
 * Matches string to other string or glob pattern (* and ?).
 * Allows chars: A-Z a-z 0-9 _ * ? . $ []
 */
public class ExcludePath {

    private static Pattern patternForPattern = Pattern.compile("[A-Za-z0-9_\\*\\?\\.\\[\\]$]*");

    private Pattern pattern;
    private String path;
    private boolean isGlob;

    public ExcludePath(String path)
    {
        if (!patternForPattern.matcher(path).matches())
            throw new IllegalArgumentException("path");

        this.path = path;
        if (path.contains("*") || path.contains("?"))
        {
            isGlob = true;
            pattern = Pattern.compile(String.format("^%s$", path.replace("[", "\\[").replace("]", "\\]")
                    .replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace("?", ".")));
        }
    }

    public boolean needExcludePath(String input)
    {
        if (!isGlob)
            return path.equals(input);
        return pattern.matcher(input).matches();
    }
}
