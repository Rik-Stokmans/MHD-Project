package mhd.mhd.Utils;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static final Pattern PATTERN = Pattern.compile(
            "<(#[a-f0-9]{6}|aqua|black|blue|dark_(aqua|blue|gray|green|purple|red)|gray|gold|green|light_purple|red|white|yellow)>",
            Pattern.CASE_INSENSITIVE
    );

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        final Matcher matcher = PATTERN.matcher(text);

        while (matcher.find()) {
            try {
                final ChatColor chatColor = ChatColor.of(matcher.group(1));

                if (chatColor != null) {
                    text = StringUtils.replace(text, matcher.group(), chatColor.toString());
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }
}