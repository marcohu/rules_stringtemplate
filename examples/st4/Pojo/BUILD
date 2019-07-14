load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "hello",
    src = "hello.st",
    out = "hello.txt",
    controller = "hello.World",
    method = "attributes",
    raw = True,
    deps = ["//st4/Pojo/src/main/java/hello:world"],
)
