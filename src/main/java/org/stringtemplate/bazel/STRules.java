package org.stringtemplate.bazel;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;


public class STRules
{
    private final static Pattern NAME_PATTERN = Pattern.compile("^(?: : \\[(.+?)\\])?(.+?)\\(.*?::= <<", Pattern.MULTILINE);

    private Options options;
    private Path sandbox;
    private ClassLoader loader;
    private Object errorHandler;
    private int errors;

    private STRules(Options options, Path sandbox, ClassLoader loader) throws Exception
    {
        this.options = options;
        this.sandbox = sandbox;
        this.loader = loader;
        this.errorHandler = errorHandler(loader);
    }

    public static void main(String[] args) throws Exception
    {
        Options options = Options.parse(args);
        Path sandbox = Paths.get(".").toRealPath();

        // use reflection to not tie the rule binary to a specific StringTemplate version
        try (URLClassLoader loader = classloader(sandbox, options.classpath))
        {
            STRules rules = new STRules(options, sandbox, loader);
            rules.process();
        }
    }

    private void process() throws Exception
    {
        Path template = sandbox.resolve(options.input);

        if (Files.notExists(template))
            throw new FileNotFoundException("Input template not found: " + options.input);

        if (!template.toString().endsWith(".st") && !template.toString().endsWith(".stg"))
            throw new IllegalArgumentException("Template must have extension .st or .stg: " + options.input);

        Class<?> $STErrorListener = loader.loadClass("org.stringtemplate.v4.STErrorListener");
        Class<?> $ST = loader.loadClass("org.stringtemplate.v4.ST");
        Object st = template(template);

        if (st == null  || errors > 0)
        {
            throw new RuntimeException(String.format("Unable to process template.%n%s", errorHandler.toString()));
        }

        // configure template via controller
        if (!options.controller.isEmpty())
        {
            Class<?> cl = loader.loadClass(options.controller);
            
            Method method = null;

            if (options.method.isEmpty())
            {
                method = Arrays.stream(cl.getMethods())
                    .filter(m -> "attributes".equals(m.getName()) && m.getParameterCount() == 0)
                    .findAny()
                    .get();
            }
            else
            {
                method = cl.getMethod(options.method);

                if (!method.getReturnType().isAssignableFrom(Map.class))
                {
                    throw new IllegalArgumentException(String.format("The return type of the method %s is not of type Map<String, Object>", options.method));
                }
            }

            Object instance = null;

            if (!Modifier.isStatic(method.getModifiers()))
            {
                 instance = cl.getConstructor().newInstance();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) method.invoke(instance);

            for (Map.Entry<String, Object> entry : attributes.entrySet())
            {
                $ST.getMethod("add", String.class, Object.class)
                   .invoke(st, entry.getKey(), entry.getValue());
            }
        }

        // configure template via JSON data file
        for (String file : options.json)
        {
            try (Reader in = Files.newBufferedReader(sandbox.resolve(file), options.encoding))
            {
                JsonObject json = Json.createReader(in).readObject();
                injectAttributes($ST, st, json);
            }
        }

        // configure template via JSON data
        if (!options.data.isEmpty())
        {
            JsonObject json = Json.createReader(new StringReader(options.data)).readObject();
            injectAttributes($ST, st, json);
        }

        Path file = sandbox.resolve(options.output);
        Files.createDirectories(file.getParent());

        try (Writer out = new FileWriter(file.toFile(), options.encoding))
        {
            Object writer = loader
                .loadClass("org.stringtemplate.v4.AutoIndentWriter")
                .getConstructor(Writer.class)
                .newInstance(out);
            $ST.getMethod("write", loader.loadClass("org.stringtemplate.v4.STWriter"), $STErrorListener)
               .invoke(st, writer, errorHandler);
        }

        if (errors > 0)
        {
            throw new RuntimeException(errorHandler.toString());
        }
    }


    private void injectAttributes(Class<?> $ST, Object st, JsonObject json) throws Exception
    {
        Class<?> $ModelAdapter = loader.loadClass("org.stringtemplate.v4.ModelAdaptor");
        Class<?> $Interpreter = loader.loadClass("org.stringtemplate.v4.Interpreter");
        Object adaptor = jsonAdaptor(loader);

        for (Object key : json.keySet())
        {
            Object value = $ModelAdapter
                .getMethod("getProperty", $Interpreter, $ST, Object.class, Object.class, String.class)
                .invoke(adaptor, null, st, json, key, key);

            try
            {
                $ST.getMethod("add", String.class, Object.class).invoke(st, key, value);
            }
            catch (InvocationTargetException ex)
            {
                throw (Exception) ex.getCause();
            }
        }
    }


    private Object template(Path path) throws Exception
    {
        Class<?> $STErrorListener = loader.loadClass("org.stringtemplate.v4.STErrorListener");
        Object st = null;

        try
        {
            if (options.raw)
            {
                Class<?> $STRawGroupDir = loader.loadClass("org.stringtemplate.v4.STRawGroupDir");
                Object group = $STRawGroupDir
                    .getConstructor(String.class, String.class, char.class, char.class)
                    .newInstance(path.getParent().toString(), options.encoding.name(), options.startDelim, options.stopDelim);
                $STRawGroupDir.getField("verbose").set(group, options.verbose);
                $STRawGroupDir
                    .getMethod("setListener", $STErrorListener)
                    .invoke(group, errorHandler);
                st = $STRawGroupDir
                    .getMethod("getInstanceOf", String.class)
                    .invoke(group, stripExtension(path.getFileName().toString()));

                registerAdaptors(group);
            }
            else if (path.getFileName().toString().endsWith(".stg"))
            {
                Class<?> $STGroupFile = loader.loadClass("org.stringtemplate.v4.STGroupFile");
                Object file = $STGroupFile
                    .getConstructor(URL.class, String.class, char.class, char.class)
                    .newInstance(path.toUri().toURL(), options.encoding.name(), options.startDelim, options.stopDelim);
                $STGroupFile.getField("verbose").set(file, options.verbose);
                $STGroupFile
                  .getMethod("setListener", $STErrorListener)
                  .invoke(file, errorHandler);
                List<String> names = templateNames((String) $STGroupFile.getMethod("show").invoke(file));
                st = $STGroupFile
                    .getMethod("getInstanceOf", String.class)
                    .invoke(file, names.iterator().next());
                registerAdaptors(file);
            }
            else
            {
                Class<?> $STGroupDir = loader.loadClass("org.stringtemplate.v4.STGroupDir");
                Object group = $STGroupDir
                    .getConstructor(String.class, String.class, char.class, char.class)
                    .newInstance(path.getParent().toString(), options.encoding.name(), options.startDelim, options.stopDelim);
                $STGroupDir.getField("verbose").set(group, options.verbose);
                $STGroupDir
                    .getMethod("setListener", $STErrorListener)
                    .invoke(group, errorHandler);
                st = $STGroupDir
                    .getMethod("getInstanceOf", String.class)
                    .invoke(group, stripExtension(path.getFileName().toString()));
                registerAdaptors(group);
            }
        }
        catch (InvocationTargetException ex)
        {
            // TODO exception should originate from StringTemplate and already have been logged,
            // but maybe we should verify that the cause is of type STException and throw otherwise
        }

        return st;
    }

    /**
     * Registers any ModelAdaptors returned by the user provided adaptor factory.
     */
    private void registerAdaptors(Object group) throws Exception
    {
        if (!options.adaptor.isEmpty())
        {
            Object adaptor = loader.loadClass(options.adaptor).getConstructor().newInstance();
            Map<?, ?> adaptors = (Map<?, ?>) adaptor.getClass().getMethod("adaptors").invoke(adaptor);
            Class<?> $STGroup = loader.loadClass("org.stringtemplate.v4.STGroup");
            Class<?> $ModelAdaptor = loader.loadClass("org.stringtemplate.v4.ModelAdaptor");

            for (Map.Entry<?, ?> e : adaptors.entrySet())
            {
                $STGroup.getMethod("registerModelAdaptor", Class.class, $ModelAdaptor).invoke(group, e.getKey(), e.getValue());
            }
        }
    }

    private Object[] toArray(JsonArray array)
    {
        Object[] result = new Object[array.size()];

        for (int i = 0, size = array.size(); i < size; i++)
        {
            Object item = array.get(i);

            if (item == JsonValue.NULL)
            {
                item = null;
            }
            else if (item instanceof JsonArray)
            {
                item = toArray((JsonArray) item);
            }
            else if (item instanceof JsonString)
            {
                item = ((JsonString) item).getString();
            }

            result[i] = item;
        }

        return result;
    }


    private static URLClassLoader classloader(Path sandbox, String[] classpath) throws IOException
    {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.jar");
        Collection<URL> urls = new LinkedHashSet<>();

        for (String path : classpath)
        {
            Path lib = sandbox.resolve(path);

            if (matcher.matches(lib)
                    || Files.isDirectory(lib))
            {
                if (Files.notExists(lib))
                {
                    throw new FileNotFoundException("Classpath entry could not be found: " + path);
                }

                urls.add(lib.toUri().toURL());
            }
        }

        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private Object errorHandler(ClassLoader loader) throws Exception
    {
        Class<?> $STErrorListener = loader.loadClass("org.stringtemplate.v4.STErrorListener");
        Class<?> $ErrorBuffer = loader.loadClass("org.stringtemplate.v4.misc.ErrorBuffer");
        Object buffer = $ErrorBuffer.getConstructor().newInstance();

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception
            {
                switch (method.getName())
                {
                    case "runTimeError":
                    {
                        if (options.failOnError)
                        {
                            errors++;
                            // we still want ErrorBuffer to record the message
                            Class<?> $STMessage = loader.loadClass("org.stringtemplate.v4.misc.STMessage");
                            $ErrorBuffer.getMethod("IOError", $STMessage).invoke(buffer, args);
                        }
                        else
                        {
                            Class<?> $STError = loader.loadClass("org.stringtemplate.v4.misc.STMessage");
                            Class<?> $ErrorType = loader.loadClass("org.stringtemplate.v4.misc.ErrorType");

                            if ($STError.getField("error").get(args[0]) != $ErrorType.getField("NO_SUCH_PROPERTY").get(null))
                                errors++;
                        }
                        break;
                    }
                    case "toString":
                    {
                        return buffer.toString();
                    }
                    default:
                    {
                        errors++;
                        break;
                    }
                }

                return method.invoke(buffer, args);
            }
        };
        return Proxy.newProxyInstance($STErrorListener.getClassLoader(),
                new Class[] { $STErrorListener },
                handler);
    }

    private Object jsonAdaptor(ClassLoader loader) throws Exception
    {
        Class<?> $ST = loader.loadClass("org.stringtemplate.v4.ST");
        Class<?> $STGroup = loader.loadClass("org.stringtemplate.v4.STGroup");
        Class<?> $ModelAdapter = loader.loadClass("org.stringtemplate.v4.ModelAdaptor");

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception
            {
                Object st = args[1];
                JsonObject o = (JsonObject) args[2];
                String propertyName = (String) args[4];
                Object value = o.get(propertyName);

                if (value instanceof JsonArray)
                {
                    value = toArray((JsonArray) value);
                }
                else if (value == JsonValue.NULL)
                {
                    value = null;
                }
                else if (value instanceof JsonString)
                {
                    JsonString string = (JsonString) value;
                    value = string.getString();
                }
                else if (value instanceof JsonObject)
                {
                    JsonObject object = (JsonObject) value;
                    if (object.keySet().size() > 0)
                    {
                        // by convention the first element denotes the template
                        String template = object.keySet().iterator().next();
                        Object group = st
                            .getClass()
                            .getField("groupThatCreatedThisInstance")
                            .get(st);
                        Object t = $STGroup
                            .getMethod("getInstanceOf", String.class)
                            .invoke(group, template);

                        // if there is a template with this name, we populate it with
                        // the attributes
                        if (t != null)
                        {
                            value = t;
                            JsonObject json = (JsonObject) object.get(template);
                            injectAttributes($ST, value, json);
                        }
                    }
                }

                return value;
            }
        };

        return Proxy.newProxyInstance($ModelAdapter.getClassLoader(),
                                      new Class[] { $ModelAdapter },
                                      handler);
    }


    private static String stripExtension(String name)
    {
        int dot = name.lastIndexOf('.');

        // we already checked that the filename contains an extension
        return name.substring(0, dot);
    }


    /**
     * Determines the template names defined in the given template.
     *
     * @param template the template.
     *
     * @return the template names.
     *
     * @throws Exception if something went wrong.
     */
    private List<String> templateNames(String template) throws Exception
    {
        List<String> names = new ArrayList<>();
        Matcher nameMatcher = NAME_PATTERN.matcher(template);

        while (nameMatcher.find())
        {
            String name = nameMatcher.group(2);

            // indicates imported template
            if (nameMatcher.group(1) != null)
            {
                Path target = findImport(nameMatcher.group(1));

                // TODO what about directories?
                if (Files.isRegularFile(target))
                {
                    Object st = template(target);
                    Class<?> $STGroup = loader.loadClass("org.stringtemplate.v4.STGroup");
                    names.addAll(templateNames((String) $STGroup.getMethod("show").invoke(st.getClass().getField("groupThatCreatedThisInstance").get(st))));
                }
            }

            names.add(name);
        }
        return names;
    }

    private Path findImport(String name)
    {
        for (String path : options.imports)
        {
            Path file = sandbox.resolve(path);

            if (file.equals(sandbox.resolve(options.input)))
                throw new IllegalArgumentException("Input not allowed as import: " + options.input);

            if (file.getFileName().toString().contains(name))
            {
                return file;
            }
        }

        throw new RuntimeException("Could not find import " + name +  " in imports " + options.imports); 
    }


    private static class Options
    {
        public String[] classpath;
        public String adaptor = "";
        public String controller = "";
        public String data = "";
        public String method = "";
        public String input;
        public String output;
        public char startDelim = '<';
        public char stopDelim = '>';
        public Charset encoding;
        public boolean failOnError;
        public boolean raw;
        public boolean verbose;
        public List<String> json = new ArrayList<>();
        public List<String> imports = new ArrayList<>();

        public static Options parse(String... args)
        {
            Options options = new Options();
            options.data = Environment.variable("DATA");
            options.classpath = Environment.variable("DEPS").split(",");

            for (int i = 0; i < args.length; i += 2)
            {
                switch (args[i])
                {
                    case "--adaptor":
                        options.adaptor = args[i + 1];
                        break;
                    case "--controller":
                        options.controller = args[i + 1];
                        break;
                    case "--failOnError":
                        options.failOnError = Boolean.parseBoolean(args[i + 1]);
                        break;
                    case "--method":
                        options.method = args[i + 1];
                        break;
                    case "--encoding":
                        options.encoding = Charset.forName(args[i + 1]);
                        break;
                    case "--input":
                        options.input = args[i + 1];
                        break;
                    case "--output":
                        options.output = args[i + 1];
                        break;
                    case "--import":
                        options.imports.add(args[i + 1]);
                        break;
                    case "--json":
                        options.json.add(args[i + 1]);
                        break;
                    case "--raw":
                        options.raw = Boolean.parseBoolean(args[i + 1]);
                        break;
                    case "--startDelim":
                        options.startDelim = args[i + 1].charAt(0);
                        break;
                    case "--stopDelim":
                        options.stopDelim = args[i + 1].charAt(0);
                        break;
                    case "--verbose":
                        options.verbose = Boolean.parseBoolean(args[i + 1]);
                        break;
                }
            }

            return options;
        }
    }
}
