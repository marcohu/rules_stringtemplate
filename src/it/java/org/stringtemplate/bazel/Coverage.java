package org.stringtemplate.bazel;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


/**
 * DOCME.
 *
 * @author  Marco Hunsicker
 */
public class Coverage
{
    /**
     * DOCME.
     *
     * @param   args  DOCME
     *
     * @throws  IOException           DOCME
     * @throws  InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException
    {
        Files.createDirectories(Paths.get("coverage"));

        Path logs = Paths.get("bazel-testlogs").toRealPath();
        Path coverage = Paths.get("coverage").toRealPath();
        Path src = Paths.get("src").toRealPath();

        PathMatcher matcher = logs.getFileSystem().getPathMatcher("glob:**/coverage.dat");

        List<String> files = new ArrayList<>();

        Files.walkFileTree(logs,
            EnumSet.of(FileVisitOption.FOLLOW_LINKS),
            Integer.MAX_VALUE,
            new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
                {
                    if (matcher.matches(file) && (Files.size(file) > 0))
                    {
                        String name = logs.relativize(file)
                                .toString()
                                .replaceAll("[/\\\\]", "_");

                        Path link = coverage.resolve(name);

                        if (Files.notExists(link))
                        {
                            Files.createSymbolicLink(link, file);
                        }

                        files.add(name.toString());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

        if (Files.notExists(coverage.resolve(src.getFileName())))
        {
            Files.createSymbolicLink(coverage.resolve(src.getFileName()), src);
        }

        ProcessBuilder builder = new ProcessBuilder().command("genhtml",
                "-o",
                coverage.toString(),
                "--ignore-errors",
                "source")
                .inheritIO()
                .directory(coverage.toFile());
        builder.command().addAll(files);

        Process p = builder.start();
        int status = p.waitFor();

        if (status == 0)
        {
            System.out.println("Coverage report at " + coverage.resolve("index.html"));
        }
        else
        {
            System.exit(status);
        }
    }
}
