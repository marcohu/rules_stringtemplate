package org.stringtemplate.bazel;


/**
 * Resolves paths in the Bazel runfiles directory. Necessary to circumvent
 * differences in the folder structure when running under an IDE or with
 * Bazel.
 *
 * @author  Marco Hunsicker
 */
class Runfiles
{
    private static String runfiles = System.getenv("RUNFILES_DIR");

    public static void runfiles()
    {
        if (runfiles != null)
        {
            System.setProperty("user.dir", runfiles + "/rules_stringtemplate");
        }
    }

    /**
     * Joins the given path to the Runfiles directory.
     *
     * @param path the path.
     *
     * @return the joined path.
     */
    public static String path(String path)
    {
        return runfiles != null ? runfiles + "/rules_stringtemplate/" + path : path;
    }

    /**
     * Joins the given path to the Runfiles external directory.
     *
     * @param path the path.
     *
     * @return the joined path.
     */
    public static String external(String path)
    {
        return runfiles != null ? runfiles + "/rules_stringtemplate/external/" + path : path;
    }
}