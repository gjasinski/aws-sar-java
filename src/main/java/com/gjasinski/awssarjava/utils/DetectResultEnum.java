package com.gjasinski.awssarjava.utils;

public enum DetectResultEnum {
    NO_TEMPLATE,
    NO_FUNCTION_FOUND,
    NODE_JS_6_10,
    NODE_JS_8_10,
    NODE_JS_10,
    NODE_JS_12,
    NODE_JS_14,
    PYTHON_2_7,
    PYTHON_3_6,
    PYTHON_3_7,
    PYTHON_3_8,
    GO_1,
    PROVIDED_AL2,
    JAVA8,
    JAVA11,
    RUBY2_5,
    RUBY2_7,
    PROVIDED,
    DOT_NET_CORE3_1,
    OK,
    ERROR;

    static public DetectResultEnum fromRuntime(String runtime) {
        if (runtime.equals("nodejs6.10")) {
            return NODE_JS_6_10;
        }
        if (runtime.equals("nodejs8.10")) {
            return NODE_JS_8_10;
        }
        if (runtime.equals("nodejs10.x")) {
            return NODE_JS_10;
        }
        if (runtime.equals("nodejs12.x")) {
            return NODE_JS_12;
        }
        if (runtime.equals("nodejs14.x")) {
            return NODE_JS_14;
        }
        if (runtime.equals("python2.7")) {
            return PYTHON_2_7;
        }
        if (runtime.equals("python3.6")) {
            return PYTHON_3_6;
        }
        if (runtime.equals("python3.7")) {
            return PYTHON_3_7;
        }
        if (runtime.equals("python3.8")) {
            return PYTHON_3_8;
        }
        if (runtime.equals("go1.x")) {
            return GO_1;
        }
        if (runtime.equals("provided.al2")) {
            //https://aws.amazon.com/blogs/compute/migrating-aws-lambda-functions-to-al2/
            return PROVIDED_AL2;
        }
        if (runtime.equals("java8")) {
            return JAVA8;
        }
        if (runtime.equals("java11")) {
            return JAVA11;
        }
        if (runtime.equals("ruby2.5")) {
            return RUBY2_5;
        }
        if (runtime.equals("ruby2.7")) {
            return RUBY2_7;
        }
        if (runtime.equals("provided")) {
            return PROVIDED;
        }
        if (runtime.equals("dotnetcore3.1")) {
            return DOT_NET_CORE3_1;
        }
        System.out.println(runtime);
        throw new IllegalArgumentException(runtime);
    }
}
