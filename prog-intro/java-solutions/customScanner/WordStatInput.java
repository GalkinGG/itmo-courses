import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordStatInput {
    public static void main(String[] args) {
        Map<String, Integer> wordStats = new LinkedHashMap<>();
        String word = "";
        try {
            InputStream f = new FileInputStream(args[0]);
            CustomScanner reader = new CustomScanner(f);
            try {
                while (reader.hasNextWord()) {
                    word = reader.nextWord();
                    if (!(word.equals(""))) {
                        if (wordStats.containsKey(word.toLowerCase())) {
                            wordStats.put(word.toLowerCase(), wordStats.get(word.toLowerCase()) + 1);
                        } else {
                            wordStats.put(word.toLowerCase(), 1);
                        }
                    }
                }
            } finally {
                reader.close();
                f.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> entry : wordStats.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
    }
}
