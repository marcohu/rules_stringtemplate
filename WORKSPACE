workspace(name = "rules_stringtemplate")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

local_repository(
    name = "examples",
    path = "./examples",
)

# TODO workaround for https://github.com/bazelbuild/skydoc/issues/166
"""
git_repository(
    name = "io_bazel_skydoc",
    remote = "https://github.com/bazelbuild/skydoc.git",
    tag = "0.3.0",
)
"""

git_repository(
    name = "io_bazel_skydoc",
    commit = "01ec165b8f26905746404e7e58d13ab5e5e36154",
    remote = "https://github.com/graknlabs/skydoc.git",
    shallow_since = "1559753887 +0300",
)

load("@io_bazel_skydoc//:setup.bzl", "skydoc_repositories")

skydoc_repositories()

load("@io_bazel_rules_sass//:package.bzl", "rules_sass_dependencies")

rules_sass_dependencies()

load("@build_bazel_rules_nodejs//:defs.bzl", "node_repositories")

node_repositories()

load("@io_bazel_rules_sass//:defs.bzl", "sass_repositories")

sass_repositories()

load("@rules_stringtemplate//stringtemplate:deps.bzl", "stringtemplate_dependencies")

stringtemplate_dependencies("4.1")

http_jar(
    name = "javax_json",
    sha256 = "17fdeb7e22375a7fb40bb0551306f6dcf2b5743078668adcdf6c642c9a9ec955",
    url = "https://repo1.maven.org/maven2/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar",
)

http_jar(
    name = "junit4",
    sha256 = "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a",
    url = "https://jcenter.bintray.com/junit/junit/4.12/junit-4.12.jar",
)

http_jar(
    name = "hamcrest_core",
    sha256 = "66fdef91e9739348df7a096aa384a5685f4e875584cce89386a7a47251c4d8e9",
    url = "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar",
)

http_jar(
    name = "assertj",
    sha256 = "f0d4eace1208744a61f021c580ae954a6c31b5884616e079bf6a84feff49397c",
    url = "https://repo1.maven.org/maven2/org/assertj/assertj-core/3.12.2/assertj-core-3.12.2.jar",
)
