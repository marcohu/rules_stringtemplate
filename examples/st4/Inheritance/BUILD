load("@rules_stringtemplate//stringtemplate:st4.bzl", "stringtemplate")

stringtemplate(
    name = "java14",
    src = "Java1_4.stg",
    out = "Java14.java",
    data = """{
        "name": "T",
        "members": {
            "constants": {
                "typename": "MyEnum",
                "names": ["A", "B"]
            }
        }
    }""",
)

stringtemplate(
    name = "java15",
    src = "Java1_5.stg",
    out = "Java15.java",
    data = """{
        "name": "T",
        "members": {
            "constants": {
                "typename": "MyEnum",
                "names": ["A", "B"]
            }
        }
    }""",
    imports = ["Java1_4.stg"],
)
