package example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Production {

    public interface Transformation {
        String transform(String source);
    }

    public static class FileToCopy {
        public final Path base;
        public final Path file;

        public FileToCopy(Path base, Path file) {
            this.base = base;
            this.file = file;
        }

        public Path relativeToBase() {
            return base.relativize(file);
        }

        @Override
        public String toString() {
            return relativeToBase().toString();
        }
    }

    public static void main(String[] args) throws IOException {
        new Production().run();
    }

    private final List<Transformation> transformations = new LinkedList<>();

    Production() {
        transformations.add(source -> source.replaceAll("package org.junit.platform.launcher.tagexpression;", "package com.github.signed.beep;"));
        transformations.add(source -> source.replaceAll("org.junit.platform.commons.util.PreconditionViolationException", "com.github.signed.external.PreconditionViolationException"));
        transformations.add(source -> source.replaceAll("org.junit.platform.launcher.tagexpression", "com.github.signed.beep"));
        transformations.add(source -> source.replaceAll("org.junit.platform.engine.TestTag", "com.github.signed.external.TestTag"));
        transformations.add(source -> {
            int packageLine = source.indexOf("package ");
            if (-1 == packageLine) {
                return source;
            }
            return source.substring(packageLine);
        });
        transformations.add(source -> source.replaceAll("import org.apiguardian.api.API;\n", ""));
        transformations.add(source -> source.replaceAll("import static org.apiguardian.api.API.Status.INTERNAL;\n", ""));
        transformations.add(source -> {
            while (true) {
                int apiAnnotationIndex = source.indexOf("@API(");
                if (-1 == apiAnnotationIndex) {
                    return source;
                }

                int endOfLine = source.indexOf("\n", apiAnnotationIndex+1);
                int startOfLineWithApiAnnotation = source.substring(0, apiAnnotationIndex).lastIndexOf("\n");
                String linesBeforeApiAnnotation = source.substring(0, startOfLineWithApiAnnotation);
                String linesAfterApiAnnotation = source.substring(endOfLine);
                source = linesBeforeApiAnnotation + linesAfterApiAnnotation;
            }
        });

    }

    private void run() throws IOException {
        Path destinationBase = Paths.get("/Users/wischan/dev/github/signed/beep/src/");
        Path productionFilesDestination = destinationBase.resolve("main/java/com/github/signed/beep");
        Path testFilesDestination = destinationBase.resolve("test/java/com/github/signed/beep");
        Files.createDirectories(productionFilesDestination);
        Files.createDirectories(testFilesDestination);

        Path productionBase = Paths.get("/Users/wischan/dev/github/signed/junit5/junit-platform-launcher/src/main/java/org/junit/platform/launcher/tagexpression");
        Path testBase = Paths.get("/Users/wischan/dev/github/signed/junit5/platform-tests/src/test/java/org/junit/platform/launcher/tagexpression");

        sync(productionBase, productionFilesDestination);
        sync(testBase, testFilesDestination);
    }

    private void sync(Path sourceBase, Path destinationBase) throws IOException {
        List<FileToCopy> sourceFiles = filesIn(sourceBase);
        List<Path> relativeSource = sourceFiles.stream().map(FileToCopy::relativeToBase).collect(Collectors.toList());
        List<FileToCopy> destinationFiles = filesIn(destinationBase);
        List<Path> relativeDestination = destinationFiles.stream().map(FileToCopy::relativeToBase).collect(Collectors.toList());

        // copy files that are not yet in destination
        sourceFiles.stream().filter(path -> !relativeDestination.contains(path.relativeToBase()))
                .forEach(notInDestination -> {
                    try {
                        Path destinationFile = destinationBase.resolve(notInDestination.relativeToBase());
                        Files.write(destinationFile, loadTransformedSource(notInDestination.file).getBytes(), StandardOpenOption.CREATE_NEW);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        // update files that are already in destination
        sourceFiles.stream().filter(path -> relativeDestination.contains(path.relativeToBase()))
                .forEach(alreadyInDestination -> {
                    try {
                        Path destinationFile = destinationBase.resolve(alreadyInDestination.relativeToBase());
                        Files.write(destinationFile, loadTransformedSource(alreadyInDestination.file).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        // remove files from destination that are no longer in source
        destinationFiles.stream().filter(existingInDestination -> !relativeSource.contains(existingInDestination.relativeToBase()))
                .forEach(toDelete -> {
                    try {
                        Files.delete(toDelete.file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private String loadTransformedSource(Path sourceFile) throws IOException {
        byte[] bytes = Files.readAllBytes(sourceFile);
        String sourceFileString = new String(bytes, StandardCharsets.UTF_8);
        return transformations.stream().reduce(sourceFileString, (s, transformation) -> transformation.transform(s), (previous, updated) -> updated);
    }

    private List<FileToCopy> filesIn(Path basePath) throws IOException {
        List<Path> files = new LinkedList<>();
        Files.walkFileTree(basePath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                files.add(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        return files.stream().map(file -> {
            return new FileToCopy(basePath, file);
        }).collect(Collectors.toList());
    }
}
