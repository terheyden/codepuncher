package com.terheyden.templates;

import java.nio.file.Path;

/**
 * Info about a file found during {@link #findAllFilesWithInfo(Path)}.
 */
class RelativeFile {

    final Path sourceDir;
    final Path relativeFile;
    final Path absoluteFile;

    /**
     * Track the source dir where the search began,
     * and a file that was found relative to that dir.
     * @param sourceDir where the search began
     * @param fileFound a file found relative to the source dir
     */
    RelativeFile(Path sourceDir, Path fileFound) {
        this.sourceDir = sourceDir;
        this.relativeFile = sourceDir.relativize(fileFound);
        this.absoluteFile = fileFound.toAbsolutePath();
    }

    /**
     * If one explicit file is specified, then the source dir is the parent of that file.
     * @param fileFound the file, relative to its parent dir
     */
    RelativeFile(Path fileFound) {
        this.sourceDir = fileFound.getParent();
        this.relativeFile = fileFound.getFileName();
        this.absoluteFile = fileFound.toAbsolutePath();
    }

    Path calculateTargetFile(Path targetDir) {
        // path.resolve(relativePath) will append the relative onto the original path,
        // essentially mirroring the original dir structure in the save dir.
        return targetDir.resolve(relativeFile).toAbsolutePath();
    }

    public Path getSourceDir() {
        return sourceDir;
    }

    public Path getRelativeFile() {
        return relativeFile;
    }

    public Path getAbsoluteFile() {
        return absoluteFile;
    }
}
