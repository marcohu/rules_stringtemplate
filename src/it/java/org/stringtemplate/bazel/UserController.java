package org.stringtemplate.bazel;

import java.util.HashMap;
import java.util.Map;


public class UserController
{
    public Map<String, Object> attributes()
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("x", new User("100", "parrt"));
        return data;
    }

    public int attributes(Map<?, ?> attributes)
    {
        return attributes.size();
    }
}
