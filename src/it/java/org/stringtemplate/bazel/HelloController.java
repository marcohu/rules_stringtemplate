package org.stringtemplate.bazel;

import java.util.HashMap;
import java.util.Map;


public class HelloController
{
    public Map<String, Object> attributes()
    {
        Map<String, Object> a = new HashMap<>();
        a.put("name", "World");
        return a;
    }

    public static Map<String, Object> map()
    {
        Map<String, Object> a = new HashMap<>();
        a.put("name", "World");
        return a;
    }

    @Override
    public String toString()
    {
        return "HelloController";
    }
}
