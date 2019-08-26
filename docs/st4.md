<!-- Generated with Stardoc: http://skydoc.bazel.build -->

<a name="#stringtemplate"></a>

## stringtemplate

<pre>
stringtemplate(<a href="#stringtemplate-name">name</a>, <a href="#stringtemplate-adaptor">adaptor</a>, <a href="#stringtemplate-controller">controller</a>, <a href="#stringtemplate-data">data</a>, <a href="#stringtemplate-deps">deps</a>, <a href="#stringtemplate-encoding">encoding</a>, <a href="#stringtemplate-failOnError">failOnError</a>, <a href="#stringtemplate-imports">imports</a>, <a href="#stringtemplate-json">json</a>, <a href="#stringtemplate-method">method</a>,
               <a href="#stringtemplate-out">out</a>, <a href="#stringtemplate-raw">raw</a>, <a href="#stringtemplate-src">src</a>, <a href="#stringtemplate-startDelim">startDelim</a>, <a href="#stringtemplate-stopDelim">stopDelim</a>, <a href="#stringtemplate-verbose">verbose</a>)
</pre>

Runs [StringTemplate 4](https://www.stringtemplate.org/) on a set of grammars.
The template attributes must be provided by at least one of the [`controller`](#stringtemplate-controller),
[`data`](#stringtemplate-data) or [`json`](#stringtemplate-json) attributes.


### Attributes

<table class="params-table">
  <colgroup>
    <col class="col-param" />
    <col class="col-description" />
  </colgroup>
  <tbody>
    <tr id="stringtemplate-name">
      <td><code>name</code></td>
      <td>
        <a href="https://bazel.build/docs/build-ref.html#name">Name</a>; required
        <p>
          A unique name for this target.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-adaptor">
      <td><code>adaptor</code></td>
      <td>
        String; optional
        <p>
          The fully-qualified Java class name of the
                                model adaptor factory to use.
                                The class must have a public no-args constructor and a
                                public no-args method named &quot;adaptors&quot; that
                                returns the mappings between attribute types and model
                                adaptors as
                                <code>java.util.Map&lt;Class&lt;?&gt;, org.stringtemplate.v4.misc.ModelAdaptor&gt;</code>.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-controller">
      <td><code>controller</code></td>
      <td>
        String; optional
        <p>
          The fully-qualified Java class name of the
                                controller to use for attribute injection.
                                The class must have a public no-args constructor and a
                                public method that returns the attributes as
                                <code>java.util.Map&lt;String, Object&gt;</code>. If no
                                method name is specified via the &quot;method&quot;
                                attribute, the method name &quot;attributes&quot; will be
                                assumed.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-data">
      <td><code>data</code></td>
      <td>
        String; optional
        <p>
          The data (in JSON format) to use for attribute injection.
                                This data is installed last into the template. If
                                there are name collisions then the value type
                                will be automatically converted into a list type by
                                StringTemplate.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-deps">
      <td><code>deps</code></td>
      <td>
        <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a>; optional
        <p>
          The dependencies to use. Either to just provide
                                the necessary dependencies for the controller. But can
                                also be used for overriding the default
                                dependencies for StringTemplate.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-encoding">
      <td><code>encoding</code></td>
      <td>
        String; optional
        <p>
          The encoding to use for input and output.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-failOnError">
      <td><code>failOnError</code></td>
      <td>
        Boolean; optional
        <p>
          Sets whether processing should fail on all errors.
                            Enabled by default. When disabled, missing or inaccessible
                            properties don't cause failure.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-imports">
      <td><code>imports</code></td>
      <td>
        <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a>; optional
        <p>
          The templates imported by the template to process. Must
                                include all nested imports as well.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-json">
      <td><code>json</code></td>
      <td>
        <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a>; optional
        <p>
          The JSON data files to use for attribute injection.
                                The data is installed into the template after
                                the results of the controller, if any. If
                                there are name collisions then the value type
                                will be automatically converted into a list type by
                                StringTemplate.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-method">
      <td><code>method</code></td>
      <td>
        String; optional
        <p>
          The name of the controller method to invoke.
                                Can be a static or instance method, the type will
                                be automatically detected at invocation time. The return type
                                of the specified method must be of type
                                <code>java.util.Map&lt;String, Object&gt;</code>.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-out">
      <td><code>out</code></td>
      <td>
        String; required
        <p>
          The relative path of the resulting file.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-raw">
      <td><code>raw</code></td>
      <td>
        Boolean; optional
        <p>
          Use raw template file format (without headers, similar to v3).
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-src">
      <td><code>src</code></td>
      <td>
        <a href="https://bazel.build/docs/build-ref.html#labels">Label</a>; required
        <p>
          The template to process.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-startDelim">
      <td><code>startDelim</code></td>
      <td>
        String; optional
        <p>
          The character to use as start delimiter in templates.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-stopDelim">
      <td><code>stopDelim</code></td>
      <td>
        String; optional
        <p>
          The character to use as stop delimiter in templates.
        </p>
      </td>
    </tr>
    <tr id="stringtemplate-verbose">
      <td><code>verbose</code></td>
      <td>
        Boolean; optional
        <p>
          Enable verbose output for template construction.
        </p>
      </td>
    </tr>
  </tbody>
</table>


