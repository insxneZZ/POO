package org.upm.poo.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandParser {
    private CommandParser() {}
    private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    public static List<String> splitArgs(String line) {
        if (line == null) return List.of();

        String s = normalize(line);

        List<String> out = new ArrayList<>();
        Matcher m = TOKEN.matcher(s);
        while (m.find()) {
            String quoted = m.group(1);
            String plain  = m.group(2);
            out.add(quoted != null ? quoted : plain);
        }
        return out;
    }

    private static String normalize(String in) {
        String s = in
                .replace('“', '"') // “
                .replace('”', '"') // ”
                .replace('\u00A0', ' ') // NBSP
                .replace('\u2007', ' ') // Figure space
                .replace('\u202F', ' '); // Narrow NBSP
        return s.trim();
    }
}
