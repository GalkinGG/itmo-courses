package info.kgeorgiy.ja.galkin.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * Allows user to produce a class that implements given interfaces.
 * This class implements the {@link Impler} and {@link JarImpler} interfaces,
 * providing mechanisms to automatically generate
 * implementations of specified interfaces at runtime and compile them into a JAR file.
 *
 * @author Galkin Gleb
 */
public class Implementor implements Impler, JarImpler {

    /**
     * Line separator for current system to format generated source code.
     */
    private static final String SEPARATOR = System.lineSeparator();

    /**
     * Indentation used in formatting generated source code.
     */
    private static final String TAB = " ".repeat(4);

    /**
     * Bit mask to filter out abstract and transient modifiers.
     */
    private static final int NOT_PRINT_MODS_MASK = ~(Modifier.ABSTRACT | Modifier.TRANSIENT);

    /**
     * Visitor to delete files and directories recursively.
     */
    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Constructs a new {@code Implementor}.
     */
    public Implementor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        validateInput(token, root);
        try (BufferedWriter writer = createWriter(root, token)) {
            StringBuilder sb = new StringBuilder();
            String pack = token.getPackage().toString();
            if (pack != null) {
                sb.append(pack).append(";").append(SEPARATOR).append(SEPARATOR);
            }

            sb.append("public class ").append(token.getSimpleName()).append("Impl ")
                    .append("implements ").append(token.getCanonicalName())
                    .append(" {").append(SEPARATOR).append(SEPARATOR);

            writer.write(toUTF(sb.toString()));
            writeMethods(token, writer);
            writer.write("}");
        } catch (IOException e) {
            throw new ImplerException("Error during writing to file", e);
        } catch (InvalidPathException e) {
            throw new ImplerException("Invalid Path", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path temp;
        try {
            temp = Files.createTempDirectory(jarFile.getParent(), "_temp");
        } catch (SecurityException | IOException e) {
            throw new ImplerException("Exception during creation temp directory", e);
        }
        Path sourceFile = getPath(token, ".class");
        implement(token, temp);
        compile(token, temp);
        try (JarOutputStream target = new JarOutputStream(Files.newOutputStream(jarFile))) {
            addFileToJar(sourceFile, temp, target);
        } catch (IOException e) {
            throw new ImplerException("Error during writing to jar", e);
        }
        try {
            Files.walkFileTree(temp, DELETE_VISITOR);
        } catch (IOException e) {
            throw new ImplerException("Error during deleting temp directory", e);
        }
    }

    /**
     * Validates input arguments.
     * Check if a given token and root path are correct.
     *
     * @param token the interface class.
     * @param root  the root directory.
     * @throws ImplerException if token or root are null, given class is
     *                         not interface or one is private/final interface
     */
    private void validateInput(Class<?> token, Path root) throws ImplerException {
        if (token == null) {
            throw new ImplerException("Token is null");
        }
        if (root == null) {
            throw new ImplerException("Root is null");
        }
        if (!token.isInterface()) {
            throw new ImplerException("The specified class is not an interface");
        }
        if ((token.getModifiers() & (Modifier.PRIVATE | Modifier.FINAL)) > 0) {
            throw new ImplerException("Incorrect interface");
        }
    }

    /**
     * Writes implemented methods to the writer.
     * Construct methods implementations and writes it to given writer.
     *
     * @param token  the interface class.
     * @param writer the writer to write methods to.
     * @throws ImplerException if parameter or return types are {@code private}.
     * @throws IOException     if an I/O error occurs while writing to {@code writer}
     */
    private void writeMethods(Class<?> token, BufferedWriter writer) throws ImplerException, IOException {
        Method[] methods = token.getMethods();
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            if (Modifier.isPrivate(method.getReturnType().getModifiers())) {
                throw new ImplerException("Method '" + method.getName() + "' return type is private");
            }
            sb.append(printMethod(method));
        }
        writer.write(toUTF(sb.toString()));
    }


    /**
     * Construct the implementation of a single given method.
     * Forms a string representation of given method.
     *
     * @param method the method to implement.
     * @return a string containing the implementation of the method.
     * @throws ImplerException if a parameter type is private.
     */
    private String printMethod(Method method) throws ImplerException {
        if (Arrays.stream(method.getParameterTypes()).anyMatch(type -> Modifier.isPrivate(type.getModifiers()))) {
            throw new ImplerException("Param type is private");
        }
        if (method.isDefault()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        int mods = method.getModifiers() & NOT_PRINT_MODS_MASK;
        Class<?> returnType = method.getReturnType();
        String typeName = returnType.isArray()
                ? returnType.getComponentType().getName() + "[]"
                : returnType.getCanonicalName();

        sb.append(TAB).append(Modifier.toString(mods)).append(" ").append(typeName)
                .append(" ").append(method.getName()).append("(");

        Parameter[] params = method.getParameters();

        if (params.length != 0) {
            sb.append(Arrays.stream(params).map(
                    param -> (param.getType().isArray()
                            ? param.getType().getComponentType().getName() + "[]"
                            : param.getType().getCanonicalName()) + " " + param.getName()
            ).collect(Collectors.joining(", ")));
        }
        sb.append(")");

        Class<?>[] excepTypes = method.getExceptionTypes();
        if (excepTypes.length != 0) {
            sb.append(Arrays.stream(excepTypes).map(Type::getTypeName)
                    .collect(Collectors.joining(",", " throws ", "")));
        }

        sb.append(" {").append(SEPARATOR);
        sb.append(TAB).append(TAB).append("return ")
                .append(getDefaultValue(method.getReturnType())).append(";").append(SEPARATOR);
        sb.append(TAB).append("}").append(SEPARATOR).append(SEPARATOR);

        return sb.toString();
    }

    /**
     * Adds a file to the given {@link JarOutputStream}.
     *
     * @param source the file to add to the JAR.
     * @param temp   the temporary directory where program saves temp files.
     * @param target the JAR output stream to add the file to.
     * @throws IOException if an I/O error occurs during writing to {@code target}
     */
    private void addFileToJar(Path source, Path temp, JarOutputStream target) throws IOException {
        String name = source.toString().replace("\\", "/");
        JarEntry entry = new JarEntry(name);
        target.putNextEntry(entry);
        Files.copy(temp.resolve(source), target);
        target.closeEntry();
    }

    /**
     * Returns a string representation of the default value for a given type.
     *
     * @param type the type for which to return the default value.
     * @return the string containing default value for the type.
     */
    private String getDefaultValue(Class<?> type) {
        if (type.equals(void.class)) {
            return "";
        }
        if (type.equals(boolean.class)) {
            return "false";
        }
        if (type.isPrimitive()) {
            return "0";
        }
        return "null";
    }

    /**
     * Returns the package name of a class.
     *
     * @param token the class.
     * @return the package name.
     */
    private String getPackageName(Class<?> token) {
        return token.getPackage() == null ? "" : token.getPackageName().replace('.', File.separatorChar);
    }

    /**
     * Returns the path of a class with a given extension.
     *
     * @param token the class.
     * @param ext   the extension. Might be ".class" or ".java".
     * @return the path of the implementation class with given extension.
     */
    private Path getPath(Class<?> token, String ext) {
        return Path.of(getPackageName(token))
                .resolve(token.getSimpleName() + "Impl" + ext);
    }

    /**
     * Creates a {@link BufferedWriter} for a given root directory and class.
     *
     * @param root  the root directory.
     * @param token the class.
     * @return the buffered writer.
     * @throws IOException if an I/O error occurs.
     */
    private BufferedWriter createWriter(Path root, Class<?> token) throws IOException {
        Path path = Path.of(
                root.toString(), getPackageName(token)).resolve(token.getSimpleName() + "Impl.java"
        );

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        return Files.newBufferedWriter(path);
    }

    /**
     * Compiles a class with a given token and directory.
     *
     * @param token the class.
     * @param dir   the directory.
     * @throws ImplerException if compilation fails: java compiler is not found
     *                         or compilation error occurs
     * @see ToolProvider#getSystemJavaCompiler()
     */
    private void compile(Class<?> token, Path dir) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            throw new ImplerException("No java compiler is provided");
        }
        String classpath;
        try {
            classpath = Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();

        } catch (URISyntaxException e) {
            throw new ImplerException("Can't convert URL to URI", e);
        }

        final int exitCode = compiler.run(null, null, null, "-encoding", "UTF-8",
                "-classpath", classpath,
                dir.resolve(getPath(token, ".java")).toString()
        );
        if (exitCode != 0) {
            throw new ImplerException("Can't compile class");
        }
    }

    /**
     * Converts a string to UTF format.
     *
     * @param string the string to convert.
     * @return the converted string.
     */
    private String toUTF(String string) {
        StringBuilder res = new StringBuilder();
        for (char c : string.toCharArray()) {
            res.append((c >= 128 ? String.format("\\u%04x", (int) c) : c));
        }
        return res.toString();
    }

    /**
     * Usage: <ul>
     * <li>{@code Implementor <token> <output>}
     * <li>{@code JarImplementor -jar <token> <output>}
     * </ul>
     *
     * @param args array of command line arguments:
     *             <ul>
     *             <li>token -- class token for implementing
     *             <li>output -- file for writing new class
     *             </ul>
     * @see Implementor#implement(Class, Path)
     * @see Implementor#implementJar(Class, Path)
     */
    public static void main(String[] args) {
        Implementor implementor = new Implementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Path.of(args[1]));
            } else if (args.length == 3 && args[0].equals("-jar")) {
                implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
            } else {
                System.err.println("Invalid arguments count");
            }
        } catch (ImplerException e) {
            System.err.println("Exception during implementation " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Interface not found " + e.getMessage());
        }
    }
}
