package org.stringtemplate.bazel;

import org.junit.Test;


public class BazelTest extends BazelTestSupport
{
    @Test
    public void json() throws Exception
    {
        build("//st4/Json/...");
    }

    @Test
    public void inheritance() throws Exception
    {
        build("//st4/Inheritance/...");
    }

    @Test
    public void controller() throws Exception
    {
        build("//st4/Controller/...");
    }

    @Test
    public void data() throws Exception
    {
        build("//st4/Data/...");
    }

    @Test
    public void raw() throws Exception
    {
        build("//st4/Raw/...");
    }

    @Test
    public void template() throws Exception
    {
        build("//st4/Template/...");
    }
}
