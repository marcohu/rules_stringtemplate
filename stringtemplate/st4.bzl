"""Rules for StringTemplate 4."""

def _impl(ctx):
    if not ctx.attr.controller and not ctx.attr.data and not ctx.attr.json:
        fail("Missing template data. Provide at least one of \"controller\", \"data\" or \"json\" attributes.")

    output = ctx.actions.declare_file(ctx.attr.out)
    deps = depset(transitive = [dep.files for dep in ctx.attr.deps])
    inputs = ctx.files.src + ctx.files.imports + ctx.files.json

    tool_inputs, input_manifests = ctx.resolve_tools(tools = ctx.attr.deps + [ctx.attr._tool] + ctx.attr._deps)

    args = ctx.actions.args()
    args.add("--adaptor")
    args.add(ctx.attr.adaptor)
    args.add("--controller")
    args.add(ctx.attr.controller)
    args.add("--failOnError")
    args.add("true" if ctx.attr.failOnError else "false")
    args.add("--method")
    args.add(ctx.attr.method)
    args.add("--encoding")
    args.add(ctx.attr.encoding)
    args.add("--input")
    args.add(ctx.file.src.path)
    args.add("--output")
    args.add(output.path)
    args.add("--raw")
    args.add("true" if ctx.attr.raw else "false")
    args.add("--startDelim")
    args.add(ctx.attr.startDelim)
    args.add("--stopDelim")
    args.add(ctx.attr.stopDelim)
    args.add("--verbose")
    args.add("true" if ctx.attr.verbose else "false")

    for f in ctx.files.json:
        args.add("--json")
        args.add(f.path)

    for f in ctx.files.imports:
        args.add("--import")
        args.add(f.path)

    # TODO: it would be nice if we could create a custom FilesToRunProvider and use that
    # as the executable in order to promote the provided dependencies directly, but
    # there is no API for that (see https://github.com/bazelbuild/bazel/issues/7187)
    ctx.actions.run(
        arguments = [args],
        inputs = inputs,
        outputs = [output],
        mnemonic = "ST4",
        executable = ctx.executable._tool,
        input_manifests = input_manifests,
        progress_message = "Processing ST4 template {}".format(ctx.file.src.path),
        tools = tool_inputs,
        env = {
            "DATA": ctx.attr.data,
            "DEPS": ",".join([f.path for f in deps.to_list()] + [f.path for f in ctx.files._deps]),
        },
    )

    return [DefaultInfo(files = depset([output]))]

stringtemplate = rule(
    implementation = _impl,
    doc = """Runs [StringTemplate 4](https://www.stringtemplate.org/) on a set of grammars.
The template attributes must be provided by at least one of the [`controller`](#stringtemplate-controller),
[`data`](#stringtemplate-data) or [`json`](#stringtemplate-json) attributes.
""",
    attrs = {
        "adaptor": attr.string(
            doc = """The fully-qualified Java class name of the
                                model adaptor factory to use.
                                The class must have a public no-args constructor and a
                                public no-args method named &quot;adaptors&quot; that
                                returns the mappings between attribute types and model
                                adaptors as
                                <code>java.util.Map&lt;Class&lt;?&gt;, org.stringtemplate.v4.misc.ModelAdaptor&gt;</code>.
                                """,
        ),
        "controller": attr.string(
            doc = """The fully-qualified Java class name of the
                                controller to use for attribute injection.
                                The class must have a public no-args constructor and a
                                public method that returns the attributes as
                                <code>java.util.Map&lt;String, Object&gt;</code>. If no
                                method name is specified via the &quot;method&quot;
                                attribute, the method name &quot;attributes&quot; will be
                                assumed.
                                """,
        ),
        "deps": attr.label_list(
            default = [],
            doc = """The dependencies to use. Either to just provide
                                the necessary dependencies for the controller. But can
                                also be used for overriding the default
                                dependencies for StringTemplate.
                                """,
        ),
        "encoding": attr.string(doc = "The encoding to use for input and output.", default = "UTF-8"),
        "failOnError": attr.bool(
            default = True,
            doc = """Sets whether processing should fail on all errors.
                            Enabled by default. When disabled, missing or inaccessible
                            properties don't cause failure.
                        """,
        ),
        "imports": attr.label_list(
            allow_files = True,
            doc = """The templates imported by the template to process. Must
                                include all nested imports as well.
                                """,
        ),
        "json": attr.label_list(
            allow_files = True,
            doc = """
                                The JSON data files to use for attribute injection.
                                The data is installed into the template after
                                the results of the controller, if any. If
                                there are name collisions then the value type
                                will be automatically converted into a list type by
                                StringTemplate.
                                """,
        ),
        "method": attr.string(
            doc = """The name of the controller method to invoke.
                                Can be a static or instance method, the type will
                                be automatically detected at invocation time. The return type
                                of the specified method must be of type
                                <code>java.util.Map&lt;String, Object&gt;</code>.
                                """,
        ),
        "out": attr.string(doc = "The relative path of the resulting file.", mandatory = True),
        "data": attr.string(
            doc = """The data (in JSON format) to use for attribute injection.
                                This data is installed last into the template. If
                                there are name collisions then the value type
                                will be automatically converted into a list type by
                                StringTemplate.
                                """,
        ),
        "raw": attr.bool(doc = """Use raw template file format (without headers, similar to v3)."""),
        "src": attr.label(allow_single_file = True, doc = "The template to process.", mandatory = True),
        "startDelim": attr.string(doc = "The character to use as start delimiter in templates.", default = "<"),
        "stopDelim": attr.string(doc = "The character to use as stop delimiter in templates.", default = ">"),
        "verbose": attr.bool(doc = """Enable verbose output for template construction."""),
        "_deps": attr.label_list(
            default = [
                "@stringtemplate4//jar",
                "@antlr3_runtime//jar",
                "@javax_json//jar",
            ],
        ),
        "_tool": attr.label(
            executable = True,
            cfg = "host",
            default = Label("@rules_stringtemplate//src/main/java/org/stringtemplate/bazel"),
        ),
    },
)
