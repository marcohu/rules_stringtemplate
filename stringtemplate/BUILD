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
)

stardoc(
    name = "repositories",
    out = "repositories.md",
    input = "repositories.bzl",
    deps = [
        ":refs",
    ],
)
