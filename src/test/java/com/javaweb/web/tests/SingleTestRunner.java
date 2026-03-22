package com.javaweb.web.tests;

import java.lang.reflect.Method;

public class SingleTestRunner {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("ERROR");
            return;
        }

        String methodName = args[0];

        RegisterTest test = new RegisterTest();

        try {
            test.setUp();

            Method method = RegisterTest.class.getMethod(methodName);
            method.invoke(test);

            System.out.println("PASS");

        } catch (Exception e) {

            System.out.println("FAIL");

        } finally {
            try {
                test.tearDown();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}