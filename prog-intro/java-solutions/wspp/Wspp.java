import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Wspp {
    public static void main(String[] args) {
        Map<String, Integer> wordStats = new LinkedHashMap<>();
        Map<String, ArrayList<Integer>> wordPos = new HashMap<>();
        String word = "";
        int currentPos = 1;
        try (CustomScanner reader = new CustomScanner(new FileInputStream(args[0]))) {
            while (reader.hasNextWord()) {
                word = reader.nextWord();
                if (!word.isEmpty()) {
                    wordStats.put(word.toLowerCase(), wordStats.getOrDefault(word.toLowerCase(), 0) + 1);
                    wordPos.putIfAbsent(word.toLowerCase(), new ArrayList<Integer>());
                    wordPos.get(word.toLowerCase()).add(currentPos);
                    currentPos++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception" + " " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                     new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> entry : wordStats.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                for (Integer k: wordPos.get(entry.getKey())) {
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
