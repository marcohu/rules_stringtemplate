package org.stringtemplate.bazel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.stringtemplate.bazel.Runfiles.external;
import static org.stringtemplate.bazel.Runfiles.path;
import static org.stringtemplate.bazel.Runfiles.runfiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import static org.assertj.core.api.Assertions.*;

public class RulesTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void directoryImport() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("output.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/directoryImport.stg"),
            "--output", output.toString(),
            "--import", "templates",
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };

        process(args);
    }

    @Test
    public void illegalImport() throws Exception
    {
        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/missingImport.stg"),
            "--output", "output",
            "--import", path("src/it/resources/templates/missingImport.stg"),
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        Environment.variable("DATA", "{\n" + 
                "        \"name\": \"T\",\n" + 
                "        \"members\": {\n" + 
                "            \"constants\": {\n" + 
                "                \"typename\": \"MyEnum\",\n" + 
                "                \"names\": [\"A\", \"B\"]\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }");
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Input not allowed as import:");
    }

    @Test
    public void invalidExtension() throws Exception
    {
        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/test.tpl"),
            "--output", "output",
            "--raw", "true",
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "true",
        };

        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Template must have extension .st or .stg:");
    }

    @Test
    public void missingExtension() throws Exception
    {
        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/raw"),
            "--output", "output",
            "--raw", "true",
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "true",
        };

        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Template must have extension .st or .stg:");
    }

    @Test
    public void invalidMethod() throws Exception
    {
        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--method", "toString",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Controller/hello.stg"),
            "--output", "output",
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("The return type of the method toString is not of type Map<String, Object>");
    }

    @Test
    public void missingImport() throws Exception
    {
        runfiles();
        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", ("src/it/resources/templates/directoryImport.stg"),
            "--output", "output",
            "--import", ("src/it/resources/templates/notfound.stg"),
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        Environment.variable("DATA", "{\n" + 
                "        \"name\": \"T\",\n" + 
                "        \"members\": {\n" + 
                "            \"constants\": {\n" + 
                "                \"typename\": \"MyEnum\",\n" + 
                "                \"names\": [\"A\", \"B\"]\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }");
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageStartingWith("Could not find import templates in imports");
    }

    @Test
    public void invalidTemplate() throws Exception
    {
        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--method", "attributes",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/invalid.stg"),
            "--output", "invalid.txt",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to process template.\n" + 
                    "invalid.stg 9:57: implicitly-defined attribute i not visible\n");
    }

    @Test
    public void unclosedAttribute() throws Exception
    {
        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--method", "attributes",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/raw.st"),
            "--output", "invalid.txt",
            "--raw", "true",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Unable to process template.\n" + 
                    "raw 1:5: premature EOF\n");
    }

    @Test
    public void missingTemplate() throws Exception
    {
        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--method", "attributes",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/invali.stg"),
            "--output", "invalid.txt",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(FileNotFoundException.class)
            .hasMessageStartingWith("Input template not found:");
    }

    @Test
    public void adaptor() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("adaptor.txt");

        String[] args = {
            "--adaptor", "org.stringtemplate.bazel.UserAdaptor",
            "--controller", "org.stringtemplate.bazel.UserController",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Adaptor/users.stg"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("100:parrt", text(output));
    }

    @Test
    public void controller() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("controller.txt");

        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Controller/hello.stg"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!", text(output));
    }

    @Test
    public void staticController() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("controller.txt");

        String[] args = {
            "--controller", "org.stringtemplate.bazel.HelloController",
            "--method", "map",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Controller/hello.stg"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!", text(output));
    }

    @Test
    public void data() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("data.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Data/hello.stg"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": \"World\" }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!", text(output));
    }

    @Test
    public void missingAttribute() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("data.txt");

        String[] args = {
            "--controller", "org.stringtemplate.bazel.WorldController",
            "--method", "attributes",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/missingAttribute.st"),
            "--output", output.toString(),
            "--raw", "true",
            "--failOnError", "false",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, !", text(output));
    }

    @Test
    public void missingAttributeFailure() throws Exception
    {
        String[] args = {
            "--controller", "org.stringtemplate.bazel.WorldController",
            "--method", "attributes",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/missingAttribute.st"),
            "--output", "output",
            "--raw", "true",
            "--failOnError", "true",
        };
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageStartingWith("context [/missingAttribute] 1:8 no such property or can't access: org.stringtemplate.bazel.User.age");
    }

    @Test
    public void number() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("data.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Data/hello.stg"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": 1 }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, 1!", text(output));
    }

    @Test
    public void missingObject() throws Exception
    {
        Path java14 = temporaryFolder.getRoot().toPath().resolve("Java14.java");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", path("src/it/resources/templates/Java1_4.stg"),
            "--output", java14.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        Environment.variable("DATA", "{\n" + 
                "        \"name\": \"T\",\n" + 
                "        \"members\": {\n" + 
                "            \"constantS\": {\n" + 
                "                \"typename\": \"MyEnum\",\n" + 
                "                \"names\": [\"A\", \"B\"]\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }");
        process(args);
        assertTrue(Files.exists(java14));
        assertEquals("class T {\n" + 
                "    constantS\n" + 
                "}", text(java14));
    }

    @Test
    public void inhheritance() throws Exception
    {
        Path java14 = temporaryFolder.getRoot().toPath().resolve("Java14.java");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Inheritance/Java1_4.stg"),
            "--output", java14.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        Environment.variable("DATA", "{\n" + 
                "        \"name\": \"T\",\n" + 
                "        \"members\": {\n" + 
                "            \"constants\": {\n" + 
                "                \"typename\": \"MyEnum\",\n" + 
                "                \"names\": [\"A\", \"B\"]\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }");
        assertFalse(Files.exists(java14));
        process(args);
        assertTrue(Files.exists(java14));
        assertEquals("class T {\n" + 
                "    public static final int MyEnum_A=1;\n" + 
                "    public static final int MyEnum_B=2;\n" + 
                "}", text(java14));

        Path java15 = temporaryFolder.getRoot().toPath().resolve("Java15.java");

        args = new String[] {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Inheritance/Java1_5.stg"),
            "--import", external("examples/st4/Inheritance/Java1_4.stg"),
            "--output", java15.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
            "--verbose", "false",
        };
        Environment.variable("DATA", "{\n" + 
                "        \"name\": \"T\",\n" + 
                "        \"members\": {\n" + 
                "            \"constants\": {\n" + 
                "                \"typename\": \"MyEnum\",\n" + 
                "                \"names\": [\"A\", \"B\"]\n" + 
                "            }\n" + 
                "        }\n" + 
                "    }");
        assertFalse(Files.exists(java15));
        process(args);
        assertTrue(Files.exists(java15));
        assertEquals("class T {\n" + 
                "    public enum MyEnum { A, B }\n" + 
                "}", text(java15));
    }

    @Test
    public void raw() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("raw.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Raw/hello.st"),
            "--output", output.toString(),
            "--raw", "true",
            "--startDelim", "<",
            "--stopDelim", ">",
        };

        assertFalse(Files.exists(output));
        Environment.variable("DATA", "{ \"name\": \"World\" }");
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!\n", text(output));
    }

    private String text(Path file) throws IOException
    {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    @Test
    public void json() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("json.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Json/hello.st"),
            "--output", output.toString(),
            "--json", external("examples/st4/Json/hello.json"),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello World, is anyone there?", text(output));
    }

    @Test
    public void template() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        Environment.variable("DATA", "{ \"name\": \"World\" }");
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!", text(output));
    }

    @Test
    public void missingData() throws Exception
    {
        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", "output",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"NAME\": \"World\" }");
        assertThat(catchThrowable(() -> process(args)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("no such attribute: NAME");
    }

    @Test
    public void nullData() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": null }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, !", text(output));
    }

    @Test
    public void emptyData() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": {} }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, !", text(output));
    }

    @Test
    public void nullArray() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": [null] }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, !", text(output));
    }

    @Test
    public void numberArray() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": [1] }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, 1!", text(output));
    }

    @Test
    public void noData() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, !", text(output));
    }

    @Test
    public void arrayArray() throws Exception
    {
        Path output = temporaryFolder.getRoot().toPath().resolve("template.txt");

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", output.toString(),
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        Environment.variable("DATA", "{ \"name\": [[\"World\"]] }");
        assertFalse(Files.exists(output));
        process(args);
        assertTrue(Files.exists(output));
        assertEquals("Hello, World!", text(output));
    }

    @Test
    public void missingJarFile() throws Exception
    {
        Path jar = temporaryFolder.getRoot().toPath().resolve("path.bin");

        Files.write(jar, new byte[0]);

        String classpath = System.getProperty("java.class.path");
        String separator = System.getProperty("path.separator");
        System.setProperty("java.class.path", classpath + separator + "missing.jar" + separator + jar);

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", "output",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        try
        {
            assertThat(catchThrowable(() -> process(args)))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("Classpath entry could not be found: missing.jar");
        }
        finally
        {
            System.setProperty("java.class.path", classpath);
        }
    }

    @Test
    public void ignoreInvalidClasspathFile() throws Exception
    {
        Path file = temporaryFolder.getRoot().toPath().resolve("path.bin");

        Files.write(file, new byte[0]);

        String classpath = System.getProperty("java.class.path");
        String separator = System.getProperty("path.separator");
        System.setProperty("java.class.path", classpath + separator + file);

        String[] args = {
            "--controller", "",
            "--method", "",
            "--encoding", "UTF-8",
            "--input", external("examples/st4/Template/hello.st"),
            "--output", "output",
            "--startDelim", "<",
            "--stopDelim", ">",
        };
        try
        {
            process(args);
        }
        finally
        {
            System.setProperty("java.class.path", classpath);
        }
    }

    private void process(String[] args) throws Exception
    {
        String classpath = System.getProperty("java.class.path");
//        java.util.Arrays.stream(classpath.split("[:;]")).forEach(e->System.out.println(" --" + e));
//        java.util.Arrays.stream(args).forEach(System.out::println);
//        System.getenv().forEach((k,v)-> System.out.println(k + "=" + v));

        String data = Environment.variable("DATA");

        // the Bazel rule always sets the DATA environment variable so
        // we have to replicate that behavior
        if (data == null)
            Environment.variable("DATA", "");

        Environment.variable("DEPS", classpath.replaceAll("[" + System.getProperty("path.separator") + "]", ","));

        try
        {
            STRules.main(args);
        }
        finally
        {
            Environment.variable("DATA", null);
        }
    }
}
