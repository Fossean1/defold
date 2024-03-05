package com.defold.extension.pipeline;

import java.io.File;
import java.util.List;
import java.util.Objects;

public interface ILuaTranspiler {

    /**
     * @return A resource path to a build file, a project-relative path starting with slash,
     * e.g. {@code "/tlconfig.lua"}
     */
    String getBuildFileResourcePath();

    /**
     * @return A file extension for the source code files of the transpiled language,
     * e.g. {@code "ext"}
     */
    String getSourceExt();

    /**
     * Build the project from the source dir to output dir
     *
     * @param pluginDir a dir inside the project root that contains unpacked plugins that may be
     *                  used for compilation (i.e. transpiler binaries)
     * @param sourceDir a dir that is guaranteed to have all the source code files as reported
     *                  by {@link #getSourceExt()}, and a build file, as reported by
     *                  {@link #getBuildFileResourcePath()}. This might be a real project dir,
     *                  or it could be a temporary directory if some sources are coming from
     *                  the dependency zip files.
     * @param outputDir a dir to put the transpiled lua files to. All lua files from the
     *                  directory will be compiled as a part of the project compilation. The
     *                  directory is preserved over editor transpiler runs and editor restarts,
     *                  so it's a responsibility of the transpiler to clear it from the old files
     * @return a possibly empty or nullable list of build issues
     */
    List<Issue> transpile(File pluginDir, File sourceDir, File outputDir) throws Exception;

    final class Issue {
        public final Severity severity;
        public final String resourcePath;
        public final int lineNumber;
        public final String message;

        /**
         * @param severity     issue severity
         * @param resourcePath compiled resource path of the build file or some of the source
         *                     files relative to project root, must start with slash
         * @param lineNumber   1-indexed line number
         * @param message      issue message to present to the user
         */
        public Issue(Severity severity, String resourcePath, int lineNumber, String message) {
            this.severity = severity;
            this.resourcePath = resourcePath;
            this.lineNumber = lineNumber;
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Issue issue = (Issue) o;
            return lineNumber == issue.lineNumber && severity == issue.severity && Objects.equals(resourcePath, issue.resourcePath) && Objects.equals(message, issue.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(severity, resourcePath, lineNumber, message);
        }

        @Override
        public String toString() {
            return "Issue{" +
                    "severity=" + severity +
                    ", resourcePath='" + resourcePath + '\'' +
                    ", lineNumber=" + lineNumber +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    enum Severity {INFO, WARNING, ERROR}
}
