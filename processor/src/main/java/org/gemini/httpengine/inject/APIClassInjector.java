package org.gemini.httpengine.inject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by geminiwen on 15/5/21.
 */
public class APIClassInjector {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final boolean isInterface;

    public APIClassInjector(String classPackage, String className, String targetClass, boolean isInterface) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.isInterface = isInterface;
    }

    public String getFqcn() {
        return classPackage + "." + className;
    }

    public String brewJava() throws Exception{
        StringBuilder builder = new StringBuilder("package " + this.classPackage + ";\n");

        String action = this.isInterface ? "implements" : "extends";

        builder.append("public class " + this.className + " " + action + " " + this.targetClass + " { \n");
        /**
         * add this line, success to create constructor
         */
        builder.append(" public " + this.className + "()" + " { \n" + " }\n");
        builder.append(" public void " + "show()" + " { \n" + " }\n");
//        for (APIMethodInjector methodInjector : methods) {
//            builder.append(methodInjector.brewJava());
//        }
        builder.append(" }\n");
        return builder.toString();
    }

    /**
     * @param str
     */
    private void writeLog(String str) {
        try {
            FileWriter fw = new FileWriter(new File("D://process.txt"), true);
            fw.write(str + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
