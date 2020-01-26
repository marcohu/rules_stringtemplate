"""Loads StringTemplate dependencies."""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

_v4 = ["4", "4.0", "4.0.2", "4.0.3", "4.0.4", "4.0.7", "4.0.8", "4.1", "4.2", "4.3"]

PACKAGES = {
    "stringtemplate4": {
        "4.3": {
            "path": "org/antlr/ST4/4.3/ST4-4.3.jar",
            "sha256": "28547dba48cfceb77b6efbfe069aebe9ed3324ae60dbd52093d13a1d636ed069",
        },
        "4.2": {
            "path": "org/antlr/ST4/4.2/ST4-4.2.jar",
            "sha256": "c0ebf2d9d31e5ad22f667410e9ea51d383a9cbdfb4fa77268ce6ff5aa74abd21",
        },
        "4.1": {
            "path": "org/antlr/ST4/4.1/ST4-4.1.jar",
            "sha256": "8b1ccaed9edc55cd255d9c19c4d8da4756d9b6fcb435671292b43470b16d75d8",
        },
        "4.0.8": {
            "path": "org/antlr/ST4/4.0.8/ST4-4.0.8.jar",
            "sha256": "58caabc40c9f74b0b5993fd868e0f64a50c0759094e6a251aaafad98edfc7a3b",
        },
        "4.0.7": {
            "path": "org/antlr/ST4/4.0.7/ST4-4.0.7.jar",
            "sha256": "29bfb0d6a4531644378b68d2c11a51450949bd640fd40e15f169efbeb8afa425",
        },
        "4.0.4": {
            "path": "org/antlr/ST4/4.0.4/ST4-4.0.4.jar",
            "sha256": "17cc49dc535a0fbe58c3a8634e774572bed31eb73415e9ce9d2703b977bf356f",
        },
        "4.0.3": {
            "path": "org/antlr/ST4/4.0.3/ST4-4.0.3.jar",
            "sha256": "45f95ee87350453ac6f08119554adaca1e7ce6860f5c99da60214dc3928347d3",
        },
        "4.0.2": {
            "path": "org/antlr/stringtemplate/4.0.2/stringtemplate-4.0.2.jar",
            "sha256": "8056d5586e1b18d3def6347b5d020a85722d850bb9f4d7a9aafe4f842c651ef9",
        },
        "4.0": {
            "path": "org/antlr/stringtemplate/4.0/stringtemplate-4.0.jar",
            "sha256": "",
        },
    },
    "antlr3_runtime": {
        "3.5.2": {
            "path": "org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2.jar",
            "sha256": "ce3fc8ecb10f39e9a3cddcbb2ce350d272d9cd3d0b1e18e6fe73c3b9389c8734",
        },
        "3.5": {
            "path": "org/antlr/antlr-runtime/3.5/antlr-runtime-3.5.jar",
            "sha256": "7ef52a4e25ea2472a0ae62ae1d5ccaa7ef23be188289ad225fcb8a452a1b738d",
        },
        "3.3": {
            "path": "org/antlr/antlr-runtime/3.3/antlr-runtime-3.3.jar",
            "sha256": "36c03c8e08be041a0f112073b1d83cc3b3a1b7ca801b79249521cbf4ebae4591",
        },
    },
    "javax_json": {
        "1.1.4": {
            "path": "org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar",
            "sha256": "17fdeb7e22375a7fb40bb0551306f6dcf2b5743078668adcdf6c642c9a9ec955",
        },
    },
}

def rules_stringtemplate_dependencies(version = "4"):
    """Loads the dependencies for the specified StringTemplate release.

    Args:
        version: The release version to make available.
                 If nothing is specified, loads the most recent bundled release.
    """
    if (version == "4" or version == "4.3"):
        _dependencies({"stringtemplate4": "4.3", "antlr3_runtime": "3.5.2"})
    elif (version == "4.2"):
        _dependencies({"stringtemplate4": "4.2", "antlr3_runtime": "3.5.2"})
    elif (version == "4.1"):
        _dependencies({"stringtemplate4": "4.1", "antlr3_runtime": "3.5.2"})
    elif (version == "4.0.8"):
        _dependencies({"stringtemplate4": "4.0.8", "antlr3_runtime": "3.5.2"})
    elif (version == "4.0.7"):
        _dependencies({"stringtemplate4": "4.0.7", "antlr3_runtime": "3.5"})
    elif (version == "4.0.4"):
        _dependencies({"stringtemplate4": "4.0.4", "antlr3_runtime": "3.3"})
    elif (version == "4.0.3"):
        _dependencies({"stringtemplate4": "4.0.3", "antlr3_runtime": "3.3"})
    elif (version == "4.0.2"):
        _dependencies({"stringtemplate4": "4.0.2", "antlr3_runtime": "3.3"})
    elif (version == "4.0"):
        _dependencies({"stringtemplate4": "4.0", "antlr3_runtime": "3.3"})
    else:
        fail(
            "Invalid StringTemplate version provided: {0}. Currently supported are: {1}".format(version, _v4),
            attr = "version",
        )

def _dependencies(deps):
    for key in deps:
        version = deps[key]
        _download(
            name = key,
            path = PACKAGES[key][version]["path"],
            sha256 = PACKAGES[key][version]["sha256"],
        )

    shared = {
        "javax_json": "1.1.4",
    }

    for key in shared:
        version = shared[key]
        _download(
            name = key,
            path = PACKAGES[key][version]["path"],
            sha256 = PACKAGES[key][version]["sha256"],
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
