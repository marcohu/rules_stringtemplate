# Setup

To use the rules you must first configure your
[`WORKSPACE`](https://docs.bazel.build/versions/master/build-ref.html#workspace)
file. To include the repository you add:

```python
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "rules_stringtemplate",
    urls = ["https://github.com/marcohu/rules_stringtemplate/archive/0.1.0.tar.gz"],
    strip_prefix = "rules_stringtemplate-0.1.0",
    sha256 = "",
)
```

You also have to pull in the necessary dependencies for the build rules. For
convenience, a helper function is provided to simplify this process. Add the
following to make the dependencies for StringTemplate 4.1 available:

```python
load("@rules_stringtemplate//stringtemplate:deps.bzl", "stringtemplate_dependencies")

stringtemplate_dependencies("4.1")
```

The currently bundled StringTemplate releases are:

| Release Stream | Bundled Versions         |
|----------------|--------------------------|
| 4              | 4.0.2, 4.0.7, 4.0.8, 4.1 |

While not recommended, it is possible to specify just the major version to load
the most recent bundled release for a stream. With rules_stringtemplate 0.1.0 the
following loads the StringTemplate 4.1 release, but might cause a different release to
be imported with a future version:

```python
load("@rules_stringtemplate//stringtemplate:deps.bzl", "stringtemplate_dependencies")

stringtemplate_dependencies("4")
```

For the most recent bundled StringTemplate release it is also possible to omit the
version number altogether, but again use with caution:

```python
load("@rules_stringtemplate//stringtemplate:deps.bzl", "stringtemplate_dependencies")

stringtemplate_dependencies()
```

If your preferred StringTemplate release is not bundled or you want to download the
dependencies from a specific host, you can pull the necessary dependencies
yourself. E.g. for StringTemplate 4.0.4:

```python
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

http_jar(
    name = "stringtemplate4",
    url = "https://oss.sonatype.org/content/repositories/releases/org/antlr/ST4/4.0.4/ST4-4.0.4.jar",
    sha256 = "17cc49dc535a0fbe58c3a8634e774572bed31eb73415e9ce9d2703b977bf356f",
)
http_jar(
    name = "antlr3_runtime",
    url = "https://oss.sonatype.org/content/repositories/releases/org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2.jar",
    sha256 = "ce3fc8ecb10f39e9a3cddcbb2ce350d272d9cd3d0b1e18e6fe73c3b9389c8734",
)
http_jar(
    name = "javax_json",
    url = "https://repo1.maven.org/maven2/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar",
    sha256 = "17fdeb7e22375a7fb40bb0551306f6dcf2b5743078668adcdf6c642c9a9ec955",
)
```

You are not required to use these exact names. It's also possible to provide the
necessary dependencies to the [`stringtemplate`](st4.md#stringtemplate) rule manually
via its [`deps`](st4.md#user-content-stringtemplate-deps) attribute.


