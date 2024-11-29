package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Md2Html {
    private final static List<String> MD_DEC = Arrays.asList("*", "**", "_", "__", "--", "`", "++");
    private final static char sep = System.lineSeparator()
            .charAt(System.lineSeparator()
                    .length() - 1);
    
    //Shouldn't be static
    private static Map<String, Boolean> states = new HashMap<>();
    
    public static void main(String[] args) {
        for (String key : MD_DEC) {
            states.put(key, false);
        }
        StringBuilder currentPart = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        try (CustomScanner sc = new CustomScanner(new FileInputStream(args[0]))) {
            while (sc.hasNextLine()) {
                String currentLine = sc.nextLine();
                if (!currentLine.isEmpty() && !currentLine.equals(System.lineSeparator())) {
                    currentPart.append(currentLine);
                } else if (currentPart.length() > 0 && currentLine.equals(System.lineSeparator())) {
                    answer.append(partConverter(currentPart));
                    answer.append(System.lineSeparator());
                    currentPart = new StringBuilder();
                }
            }
            answer.append(partConverter(currentPart));
            //:note:  exception handling
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            writer.write(answer.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static StringBuilder partConverter(StringBuilder currentPart) {
        boolean isHeadline = false;
        StringBuilder result = new StringBuilder();
        int i = 0;
        int headLevel = 0;
        while (currentPart.charAt(i) == '#') {
            headLevel++;
            i++;
        }
        if (headLevel > 0 && Character.isWhitespace(currentPart.charAt(i))
                && headLevel < 7) {
            isHeadline = true;
            currentPart = new StringBuilder(currentPart.substring(i + 1));
        }
        char currentSymb;
        currentPart.insert(0, " ");
        currentPart.insert(0, " ");
        currentPart.append(" ");
        currentPart.append(" ");
        int k = 2;
        while (k < currentPart.length() - 2) {
            currentSymb = currentPart.charAt(k);
            String dec = Character.toString(currentSymb);
            dec += Character.toString(
                    currentPart.charAt(k + 1)); //decorator. Беру два символа и смотрю, является ли это разметкой
            //Является разметкой + не окружён пробелами + не экранирован первый символ двойной разметки
            if (MD_DEC.contains(dec) && (!Character.isWhitespace(currentPart.charAt(k - 1))
                    || !Character.isWhitespace(currentPart.charAt(k + 2)))
                    && (currentPart.charAt(k - 1) != '\\')) {
                result.append(tag(dec));
                k += 2;
                //Является разметкой + не экранирован + не окружён пробелами и не является частью двойной разметки
            } else if (MD_DEC.contains(Character.toString(currentSymb)) && currentPart.charAt(k - 1) != '\\'
                    && (!Character.isWhitespace(currentPart.charAt(k + 1))
                    || !Character.isWhitespace(currentPart.charAt(k - 1)))
                    && currentSymb != currentPart.charAt(k + 1)) {
                result.append(tag(Character.toString(currentSymb)));
                k++;
            } else {
                //Если не наткнулись на символы разметки, добавляем текст, не забывая экранировать
                if (currentSymb != '\\') {
                    switch (currentSymb) {
                        case '>' -> {
                            result.append("&gt;");
                        }
                        case '<' -> {
                            result.append("&lt;");
                        }
                        case '&' -> {
                            result.append("&amp;");
                        }
                        default -> {
                            if (MD_DEC.contains(Character.toString(currentSymb))) {
                                result.append(MD_DEC.get(MD_DEC.indexOf(Character.toString(currentSymb))));
                            } else {
                                result.append(currentSymb);
                            }
                        }
                    }
                }
                k++;
            }
        }
        //Обворачиваем как заголовок
        if (isHeadline) {
            result.insert(0, "<h" + headLevel + ">");
            if (result.charAt(result.length() - 1) == sep) {
                result.replace(result.length() - System.lineSeparator()
                        .length(), result.length(), "</h" + headLevel + ">");
            } else {
                result.append("</h")
                        .append(headLevel)
                        .append(">");
            }
            //Обворачиваем как абзац
        } else {
            result.insert(0, "<p>");
            if (result.charAt(result.length() - 1) == sep) {
                result.replace(result.length() - System.lineSeparator()
                        .length(), result.length(), "</p>");
            } else {
                result.append("</p>");
            }
        }
        return result; //Возвращаем отформатированный блок
    }
    
    public static String tag(String dec) {
        String tag = "";
        if (states.get(dec)) {
            switch (dec) {
                case "*", "_" -> {
                    tag = "</em>";
                    states.put(dec, false);
                }
                case "**", "__" -> {
                    tag = "</strong>";
                    states.put(dec, false);
                }
                case "--" -> {
                    tag = "</s>";
                    states.put(dec, false);
                }
                case "`" -> {
                    tag = "</code>";
                    states.put(dec, false);
                }
                case "++" -> {
                    tag = "</u>";
                    states.put(dec, false);
                }
            }
        } else {
            switch (dec) {
                case "*", "_" -> {
                    tag = "<em>";
                    states.put(dec, true);
                }
                case "**", "__" -> {
                    tag = "<strong>";
                    states.put(dec, true);
                }
                case "--" -> {
                    tag = "<s>";
                    states.put(dec, true);
                }
                case "`" -> {
                    tag = "<code>";
                    states.put(dec, true);
                }
                case "++" -> {
                    tag = "<u>";
                    states.put(dec, true);
                }
            }
        }
        return tag;
    }
}
