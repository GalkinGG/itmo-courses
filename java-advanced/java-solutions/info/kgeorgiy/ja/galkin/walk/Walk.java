package info.kgeorgiy.ja.galkin.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class Walk {

    String inputFile;
    String outputFile;

    Charset charset = StandardCharsets.UTF_8;
    int BUFFER_SIZE = 2048;

    public Walk(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public static void main(String[] args) {
        if (args != null && args.length == 2) {
            try {
                new Walk(args[0], args[1]).run();
            } catch (NoSuchFileException e) {
                System.err.println("No file exist: " + e.getMessage());
            } catch (SecurityException | CreateDirectoryException | InvalidPathException | IOException |
                     NullPointerException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("Give program arguments: <input> <output>");
        }
    }

    private void run() throws IOException, CreateDirectoryException, SecurityException, InvalidPathException {
        List<String> files = Files.readAllLines(Paths.get(inputFile), charset);
        writeHashes(files);
    }

    private void writeHashes(List<String> files) throws IOException, CreateDirectoryException, SecurityException, InvalidPathException {
        Path out;
        try {
            out = Paths.get(outputFile);
        } catch (InvalidPathException e) {
            throw new InvalidPathException("Invalid path", "Invalid path: " + outputFile);
        }
        if (out.getParent() != null) {
            try {
                Files.createDirectories(out.getParent());
            } catch (SecurityException e) {
                throw new SecurityException("Insufficient permissions to create a directory");
            } catch (IOException e) {
                throw new CreateDirectoryException("Can't create directory for output file");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(out, charset)) {
            for (String file : files) {
                writer.write(String.format("%08x", getFileHash(file)) + " " + file + System.lineSeparator());
            }
        } catch (SecurityException e) {
            throw new SecurityException("Insufficient permissions to write to the output file");
        } catch (InvalidPathException e) {
            throw new InvalidPathException("Invalid path", "Invalid path: " + out);
        } catch (IOException e) {
            throw new IOException("Error while working with the output file: " + e.getMessage());
        }
    }

    private long getFileHash(String file) {
        try (ByteChannel input = Files.newByteChannel(Paths.get(file))) {

            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            int hash = 0;
            int read = input.read(buf);
            while (read >= 0) {
                buf.flip();
                hash = countHash(buf, read, hash);
                read = input.read(buf);
            }
            hash += (hash << 3);
            hash ^= (hash >>> 11);
            hash += (hash << 15);
            return hash & 0xFFFFFFFFL;
        } catch (InvalidPathException | SecurityException | IOException e) {
            return 0;
        }
    }

    private int countHash(final ByteBuffer buf, int length, int hash) {
        for (int i = 0; i < length; i++) {
            hash += (buf.get() & 0xFF);
            hash += (hash << 10);
            hash ^= (hash >>> 6);
        }
        return hash;
    }
}
