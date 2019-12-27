package(default_visibility = ["//visibility:public"])

load("@io_bazel_stardoc//stardoc:stardoc.bzl", "stardoc")
load("@bazel_skylib//:bzl_library.bzl", "bzl_library")

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
)

bzl_library(
    name = "refs",
    srcs = ["@bazel_tools//tools:bzl_srcs"],
)

stardoc(
    name = "st4",
    out = "st4.md",
    input = "st4.bzl",
    rule_template = "@stardoc_templates//:rule.vm",
)

stardoc(
    name = "repositories",
    out = "repositories.md",
    input = "repositories.bzl",
    func_template = "@stardoc_templates//:func.vm",
    deps = [
        ":refs",
    ],
)
