import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppCountPosition {
    public static void main(String[] args) {
        Map<String, Integer> wordStats = new LinkedHashMap<>();
        Map<String, ArrayList<String>> wordPos = new HashMap<>();
        String word = "";
        int currentPos = 1;
        int currentLine = 1;
        try (CustomScanner reader = new CustomScanner(new FileInputStream(args[0]))) {
            while (reader.hasNextLine()) {
                try (CustomScanner line = new CustomScanner(reader.nextLine())) {
                    while (line.hasNextWord()) {
                        word = line.nextWord();
                        if (!word.isEmpty()) {
                            wordStats.put(word.toLowerCase(), wordStats.getOrDefault(word.toLowerCase(), 0) + 1);
                            wordPos.putIfAbsent(word.toLowerCase(), new ArrayList<String>());
                            wordPos.get(word.toLowerCase()).add(currentLine + ":" + currentPos);
                            currentPos++;
                        }
                    }
                    currentLine++;
                    currentPos = 1;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordStats.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                     new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> entry : list) {
                writer.write(entry.getKey() + " " + entry.getValue());
                for (String k: wordPos.get(entry.getKey())) {
                    writer.write(" " + k);
                }
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
    }
}
