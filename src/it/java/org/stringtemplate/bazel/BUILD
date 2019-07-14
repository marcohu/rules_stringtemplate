java_library(
    name = "bazel",
    srcs = glob(
        ["*.java"],
        exclude = ["*Test.java"],
    ),
    data = [
        "//src/main/java/org/stringtemplate/bazel:srcs",
        "//stringtemplate:srcs",
        "@examples//st4:srcs",
    ],
    resources = [
        "//src/it/resources/META-INF/services:srcs",
    ],
    exports = [
        "@assertj//jar",
    ],
    deps = [
        "//src/main/java/org/stringtemplate/bazel",
        "@junit4//jar",
        "@stringtemplate4//jar",
    ],
)

java_test(
    name = "RulesTest",
    size = "small",
    srcs = ["RulesTest.java"],
    data = [
        "//src/it/resources/templates:srcs",
        "@examples//:srcs",
    ],
    deps = [
        ":bazel",
        "//src/main/java/org/stringtemplate/bazel",
        "@antlr3_runtime//jar",
        "@javax_json//jar",
        "@stringtemplate4//jar",
    ],
)

java_test(
    name = "BazelTest",
    size = "enormous",
    srcs = ["BazelTest.java"],
    data = [
        "//:srcs",
        "//src/it/resources/templates:srcs",
        "//src/main/java/org/stringtemplate/bazel:srcs",
        "//stringtemplate:srcs",
        "@examples//:srcs",
    ],
    deps = [":bazel"],
)
