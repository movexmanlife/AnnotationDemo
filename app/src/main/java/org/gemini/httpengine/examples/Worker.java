package org.gemini.httpengine.examples;

import org.gemini.httpengine.annotation.NetWorkParams;

/**
 *
 */
public class Worker {
    public int age;
    public String name;

    @NetWorkParams("coding")
    public void coding() {

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
