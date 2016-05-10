package org.gemini.httpengine.examples;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/10.
 */
public class Student extends Worker {
    public String FIELD_AGE = "age";
    public String FIELD_NAME = "name";
    public String[] FIELD_ARRAY = new String[]{
            FIELD_AGE, FIELD_NAME
    };

    public static Map<String, String> args = new HashMap<String, String>();
    static {
        args.put("age", "0");
        args.put("name", "jack");
    }

    public static final Map<String, String> getArgsMap(Worker worker) {
        if (worker == null) {
            throw new IllegalArgumentException("The args not be null!");
        }

        args.put("age", String.valueOf(getAge(worker)));
        args.put("name", String.valueOf(getName(worker)));

        return args;
    }

    public static int getAge(Worker worker) {
        return worker.getAge();
    }

    public static String getName(Worker worker) {
        return worker.getName();
    }
}
