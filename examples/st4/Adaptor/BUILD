load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "adaptor",
    src = "users.stg",
    out = "users.txt",
    adaptor = "hello.UserAdaptor",
    controller = "hello.UserController",
    deps = ["//st4/Adaptor/src/main/java/hello:world"],
)
