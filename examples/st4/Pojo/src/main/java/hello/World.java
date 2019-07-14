package hello;

import java.util.*;


public class World
{
    public Map<String, Object> attributes()
    {
        Map<String, Object> a = new HashMap<>();
        a.put("user", new User("World"));
        return a;
    }
}
