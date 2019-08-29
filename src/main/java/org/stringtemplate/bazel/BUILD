load("@rules_java//java:defs.bzl", "java_binary")

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
    visibility = ["//visibility:public"],
)

java_binary(
    name = "bazel",
    srcs = glob(["*.java"]),
    main_class = "org.stringtemplate.bazel.STRules",
    visibility = ["//visibility:public"],
    deps = ["@javax_json//jar"],
)
