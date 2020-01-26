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


**ATTRIBUTES**


| Name  | Description | Type | Mandatory | Default |
| --------------- | --------------- | --------------- | --------------- | --------------- |
| <a name="stringtemplate-name"></a>name |  A unique name for this target.   | <a href="https://bazel.build/docs/build-ref.html#name">Name</a> | required |  |
| <a name="stringtemplate-adaptor"></a>adaptor |  The fully-qualified Java class name of the                                 model adaptor factory to use.                                 The class must have a public no-args constructor and a                                 public no-args method named &quot;adaptors&quot; that                                 returns the mappings between attribute types and model                                 adaptors as                                 <code>java.util.Map&lt;Class&lt;?&gt;, org.stringtemplate.v4.misc.ModelAdaptor&gt;</code>   | String | optional | "" |
| <a name="stringtemplate-controller"></a>controller |  The fully-qualified Java class name of the                                 controller to use for attribute injection.                                 The class must have a public no-args constructor and a                                 public method that returns the attributes as                                 <code>java.util.Map&lt;String, Object&gt;</code>. If no                                 method name is specified via the &quot;method&quot;                                 attribute, the method name &quot;attributes&quot; will be                                 assumed.   | String | optional | "" |
| <a name="stringtemplate-data"></a>data |  The data (in JSON format) to use for attribute injection.                                 This data is installed last into the template. If                                 there are name collisions then the value type                                 will be automatically converted into a list type by                                 StringTemplate.   | String | optional | "" |
| <a name="stringtemplate-deps"></a>deps |  The dependencies to use. Either to just provide                                 the necessary dependencies for the controller. But can                                 also be used for overriding the default                                 dependencies for StringTemplate.   | <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a> | optional | [] |
| <a name="stringtemplate-encoding"></a>encoding |  The encoding to use for input and output.   | String | optional | "UTF-8" |
| <a name="stringtemplate-failOnError"></a>failOnError |  Sets whether processing should fail on all errors.                             Enabled by default. When disabled, missing or inaccessible                             properties don't cause failure.   | Boolean | optional | True |
| <a name="stringtemplate-imports"></a>imports |  The templates imported by the template to process. Must                                 include all nested imports as well.   | <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a> | optional | [] |
| <a name="stringtemplate-json"></a>json |  The JSON data files to use for attribute injection.                                 The data is installed into the template after                                 the results of the controller, if any. If                                 there are name collisions then the value type                                 will be automatically converted into a list type by                                 StringTemplate.   | <a href="https://bazel.build/docs/build-ref.html#labels">List of labels</a> | optional | [] |
| <a name="stringtemplate-method"></a>method |  The name of the controller method to invoke.                                 Can be a static or instance method, the type will                                 be automatically detected at invocation time. The return type                                 of the specified method must be of type                                 <code>java.util.Map&lt;String, Object&gt;</code>.   | String | optional | "" |
| <a name="stringtemplate-out"></a>out |  The relative path of the resulting file.   | String | required |  |
| <a name="stringtemplate-raw"></a>raw |  Use raw template file format (without headers, similar to v3).                 Requires StringTemplate 4.0.7 or later.   | Boolean | optional | False |
| <a name="stringtemplate-src"></a>src |  The template to process.   | <a href="https://bazel.build/docs/build-ref.html#labels">Label</a> | required |  |
| <a name="stringtemplate-startDelim"></a>startDelim |  The character to use as start delimiter in templates.   | String | optional | "<" |
| <a name="stringtemplate-stopDelim"></a>stopDelim |  The character to use as stop delimiter in templates.   | String | optional | ">" |
| <a name="stringtemplate-verbose"></a>verbose |  Enable verbose output for template construction.   | Boolean | optional | False |


