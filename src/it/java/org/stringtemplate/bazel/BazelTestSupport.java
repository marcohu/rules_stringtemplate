package org.stringtemplate.bazel;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Bazel test support.
 *
 * @author  Marco Hunsicker
 */
class BazelTestSupport
{
    /**
     * Builds the given target in the examples workspace.
     *
     * @param   target  the target to build.
     *
     * @return  the path to the bazel-bin directory of the workspace.
     *
     * @throws  Exception  if an error occurred.
     */
    protected Path build(String target) throws Exception
    {
        TestWorkspace workspace = new TestWorkspace();
        Path repositoryCache = Paths
            .get(System.getProperty("user.home"))
            .resolve(".cache/bazel/_bazel_" + System.getProperty("user.name") +  "/cache/repos/v1");

        // TODO by default, Bazel 2.0 does not seem to share the repository cache for
        // tests which causes the dependencies to be downloaded each time, we therefore
        // try to share it manually
        Process p = new ProcessBuilder()
            .command("bazel", "build", "--repository_cache", repositoryCache.toString(), target)
            .directory(workspace.root.toFile())
            .inheritIO()
            .start();
        p.waitFor();
        assertEquals(0, p.exitValue());

        return workspace.path("bazel-bin");
    }
}
