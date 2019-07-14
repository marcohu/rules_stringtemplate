[![Build Status](https://travis-ci.org/marcohu/rules_stringtemplate.png?branch=master)](https://travis-ci.org/marcohu/rules_stringtemplate)
[![Java 8+](https://img.shields.io/badge/java-8+-4c7e9f.svg)](https://java.oracle.com)
[![License](https://img.shields.io/badge/license-Apache2-blue.svg)](https://github.com/marcohu/rules_stringtemplate/blob/master/LICENSE)

# StringTemplate Rules for Bazel

These build rules are used for processing [StringTemplate](https://www.stringtemplate.org)
templates with [Bazel](https://bazel.build/).

  * [Workspace Setup](#setup)
    + [Details](docs/setup.md)
  * [Build Rules](#build_rules)
    - [Example](#example)

<a name="setup"></a>
## Workspace Setup

Add the following to your [`WORKSPACE`](https://docs.bazel.build/versions/master/build-ref.html#workspace)
file to include the external repository:

```python
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "rules_stringtemplate",
    urls = ["https://github.com/marcohu/rules_stringtemplate/archive/0.1.0.tar.gz"],
    strip_prefix = "rules_stringtemplate-0.1.0",
    sha256 = "e8d7f9e7ff20e73a8c7e9af0b884a4afac15af26c7cb13d1cf916f634252c489",
)
```

To load the necessary dependencies, you can use the provided convenience function:

```python
load("@rules_stringtemplate//stringtemplate:deps.bzl", "stringtemplate_dependencies")

stringtemplate_dependencies("4.1")
```

More detailed instructions can be found in the [Setup](docs/setup.md#setup) document.


<a name="build_rules"></a>
## Build Rules

To add StringTemplate code generation to your
[BUILD](https://docs.bazel.build/versions/master/build-ref.html#BUILD_files)
files, you first have to load the StringTemplate extension:

```python
load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")
```

You can then invoke the rule:

```python
stringtemplate(
    name = "hello",
    src = "hello.st",
    out = "messages/hello.txt",
    data = '{ "name": "World" }',
)
```

The necessary template attributes can be provided in different ways. You can use JSON files
or inline data as shown above. Or you can employ a more code centric approach and specify
a Java class that returns the attributes as a `java.util.Map<String, Object>`. This way it's
possible to hook in existing POJOs, maybe with the help of
[model adaptors](https://github.com/antlr/stringtemplate4/blob/master/doc/adaptors.md#model-adaptors)
if objects don't follow the Java Bean naming convention and StringTemplate can't access
their properties directly.

Refer to the rule reference documentation for the available attributes:

* <a href="docs/st4.md">StringTemplate 4 reference</a>


<a name="example"></a>
### Example

Suppose you have the following project structure:

```
st4/Controller
├── BUILD
├── hello.st
└── src
    └── main
        └── java
            └── hello
                ├── BUILD
                └── World.java
WORKSPACE
```

To provide the StringTemplate attributes through a controller, you have to add
the target that builds the controller class as a dependency:

`st4/Controller/BUILD`
```python
load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "hello",
    src = "hello.st",
    out = "hello.txt",
    controller = "hello.World",
    deps = ["//st4/Controller/src/main/java/hello:world"],
)
```

To build the example you use:

```bash
$ bazel build //st4/Controller/...
```

The output would be something like this:

```bash
INFO: Analyzed 2 targets (22 packages loaded, 385 targets configured).
INFO: Found 2 targets...
INFO: Elapsed time: 12.869s, Critical Path: 7.60s
INFO: 7 processes: 5 processwrapper-sandbox, 2 worker.
INFO: Build completed successfully, 11 total actions
```

You can find this and other examples in the [examples](examples) folder.
