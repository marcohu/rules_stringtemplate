load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "hello",
    src = "hello.stg",
    out = "hello.txt",
    controller = "hello.World",
    method = "attributes",
    deps = ["//st4/Controller/src/main/java/hello:world"],
)
