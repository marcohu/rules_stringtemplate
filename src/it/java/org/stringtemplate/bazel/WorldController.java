package org.stringtemplate.bazel;

import java.util.HashMap;
import java.util.Map;


public class WorldController
{
    public Map<String, Object> attributes()
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", new User("World"));
        return data;
    }
}
