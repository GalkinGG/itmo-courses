import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordStatWordsSuffix {
    public static void main(String[] args) {
        Map<String, Integer> wordStats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader
        (new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            char symbol;
            int code = reader.read();
            String preffix = "";
            StringBuilder word = new StringBuilder();
            while (code != -1) {
                symbol = (char) code;
                while ((Character.isLetter(symbol)) || (symbol == '\'')
                        || (Character.DASH_PUNCTUATION == Character.getType(symbol))) {
                    word.append(symbol);
                    code = reader.read();
                    symbol = (char) code;
                }
                if (!(word.toString().equals(""))) {
                    if (word.length()>3) {
                        preffix = word.substring(word.length() - 3);
                    } else {
                        preffix = word.toString();
                    }
                    if (wordStats.containsKey(preffix.toLowerCase())) {
                        wordStats.put(preffix.toLowerCase(), wordStats.get(preffix.toLowerCase()) + 1);
                    } else {
                        wordStats.put(preffix.toLowerCase(), 1);
                    }
                }
                word = new StringBuilder();
                code = reader.read();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordStats.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByKey());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
        (new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> pair: list) {
                writer.write((pair.getKey() + " " + pair.getValue()));
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
    }
}