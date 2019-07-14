load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "hello",
    src = "hello.st",
    out = "hello.txt",
    data = '{ "name": "World" }',
)
