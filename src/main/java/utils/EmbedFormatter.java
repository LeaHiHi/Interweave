package utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EmbedFormatter {
    /**
     * Formats a MessageEmbed and returns an okay-looking string representation of it.
     * @param e The MessageEmbed to format.
     * @return A {@link String} formatted from an embed.
     */
    public static String formatEmbed(MessageEmbed e) {
        if (e.getTitle() == null && e.getDescription() == null) {
            // Probably a link, skip it.
            return "";
        }
        ArrayList<String> strings = new ArrayList<>();
        strings.add((e.getTitle() == null ? "" : e.getTitle()) + ((e.getAuthor() == null) ? "" : ", " + e.getAuthor().getName()));

        if (e.getDescription() != null) {
            for (String s : e.getDescription().split("\r?\n")) {
                if (!s.isEmpty()) {
                    strings.add(s);
                }
            }
        }
        for (MessageEmbed.Field f : e.getFields()) {
            strings.add(f.getName() + ": " + f.getValue());
        }
        if (e.getFooter() != null) {
            strings.add(e.getFooter().getText());
        }

        StringBuilder response = new StringBuilder();
        String[] stringArray = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            stringArray[i] = StringUtils.stripEnd(strings.get(i), "\n");
        }

        for (int i = 0; i < stringArray.length; i++) {
            if (i == 0) {
                stringArray[i] = "╽ " + stringArray[i] + "\n";
            }
            else if (i == stringArray.length - 1) {
                stringArray[i] = "╿ " + stringArray[i] + "\n";
            }
            else {
                stringArray[i] = "┃ " + stringArray[i] + "\n";
            }
            response.append(stringArray[i]);
        }
        return response.toString();
    }
}
