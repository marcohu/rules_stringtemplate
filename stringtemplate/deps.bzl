"""Loads StringTemplate dependencies."""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

_v4 = ["4", "4.0.2", "4.0.7", "4.0.8", "4.1"]

def stringtemplate_dependencies(version = "4.1"):
    """Loads the dependencies for the specified StringTemplate release.

    Args:
        version: The release version to make available.
                 If nothing is specified, loads the most recent bundled release.
    """
    if (version == "4" or version == "4.1" or version == "4.1.0"):
        _dependencies("org/antlr/ST4/4.1/ST4-4.1.jar", "8b1ccaed9edc55cd255d9c19c4d8da4756d9b6fcb435671292b43470b16d75d8")
    elif (version == "4.0.8"):
        _dependencies("org/antlr/ST4/4.0.8/ST4-4.0.8.jar", "58caabc40c9f74b0b5993fd868e0f64a50c0759094e6a251aaafad98edfc7a3b")
    elif (version == "4.0.7"):
        _dependencies("org/antlr/ST4/4.0.7/ST4-4.0.7.jar", "29bfb0d6a4531644378b68d2c11a51450949bd640fd40e15f169efbeb8afa425")
    elif (version == "4.0.2"):
        _dependencies("org/antlr/stringtemplate/4.0.2/stringtemplate-4.0.2.jar", "8056d5586e1b18d3def6347b5d020a85722d850bb9f4d7a9aafe4f842c651ef9")
    else:
        fail(
            "Invalid StringTemplate version provided: {0}. Currently supported are: {1}".format(version, _v4),
            attr = "version",
        )

def _dependencies(path, sha256):
    _download(
        name = "stringtemplate4",
        path = path,
        sha256 = sha256,
    )
    _download(
        name = "antlr3_runtime",
        path = "org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2.jar",
        sha256 = "ce3fc8ecb10f39e9a3cddcbb2ce350d272d9cd3d0b1e18e6fe73c3b9389c8734",
    )
    http_jar(
        name = "javax_json",
        url = "https://repo1.maven.org/maven2/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar",
        sha256 = "17fdeb7e22375a7fb40bb0551306f6dcf2b5743078668adcdf6c642c9a9ec955",
    )

def _download(name, path, sha256):
    http_jar(
        name = name,
        urls = [
            "https://jcenter.bintray.com/" + path,
            "https://repo1.maven.org/maven2/" + path,
        ],
        sha256 = sha256,
    )
