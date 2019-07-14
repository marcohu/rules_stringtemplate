package org.stringtemplate.bazel;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;


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

        Process p = new ProcessBuilder()
            .command("bazel", "build", target)
            .directory(workspace.root.toFile())
            .inheritIO()
            .start();
        p.waitFor();
        assertEquals(0, p.exitValue());

        return workspace.path("bazel-bin");
    }
}
