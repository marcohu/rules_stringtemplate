package(default_visibility = ["//visibility:public"])

load("@io_bazel_skydoc//stardoc:stardoc.bzl", "stardoc")
load("@bazel_skylib//:bzl_library.bzl", "bzl_library")

filegroup(
    name = "srcs",
    srcs = glob(["*"]),
)

stardoc(
    name = "st4",
    out = "st4.md",
    input = "st4.bzl",
)

stardoc(
    name = "deps",
    out = "deps.md",
    input = "deps.bzl",
)
