package org.stringtemplate.bazel;


public class User
{
    private final String id;
    private final String name;

    public User(String name)
    {
        this("-1", name);
    }

    public User(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String id()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return id + ":"+name;
    }

    public String getName()
    {
        return name;
    }
}