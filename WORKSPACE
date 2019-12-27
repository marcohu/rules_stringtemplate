workspace(name = "rules_stringtemplate")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

local_repository(
    name = "examples",
    path = "./examples",
)

git_repository(
    name = "stardoc_templates",
    remote = "https://github.com/marcohu/stardoc_templates.git",
    tag = "0.1.0",
)

git_repository(
    name = "io_bazel_stardoc",
    remote = "https://github.com/bazelbuild/stardoc.git",
    tag = "0.4.0",
)

load("@io_bazel_stardoc//:setup.bzl", "stardoc_repositories")

stardoc_repositories()

load("@rules_stringtemplate//stringtemplate:repositories.bzl", "rules_stringtemplate_dependencies")

rules_stringtemplate_dependencies("4.2")

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
