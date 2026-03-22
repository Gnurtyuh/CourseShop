package com.javaweb.web.tests;

public class ExcelTestRunner {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("ERROR");
            System.exit(1); // ❗ thêm dòng này
        }

        String methodName = args[0];
        Object test;

        if (methodName.startsWith("TC_REG")) {
            test = new RegisterTest();
        } else if (methodName.startsWith("TC_LOG")) {
            test = new LoginTest();
        } else if (methodName.startsWith("TC_ENR")) {
            test = new EnrollmentTest();
        } else if (methodName.startsWith("TC_VIEW")) {
            test = new ViewCourseTest();
        } else {
            System.out.println("ERROR");
            System.exit(1);
            return;
        }

        boolean isPass = false;

        try {
            test.getClass().getMethod("setUp").invoke(test);
            test.getClass().getMethod(methodName).invoke(test);

            isPass = true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                test.getClass().getMethod("tearDown").invoke(test);
            } catch (Exception ignored) {}
        }

        if (isPass) {
            System.out.println("PASS");
            System.exit(0);
        } else {
            System.out.println("FAIL");
            System.exit(1);
        }
    }
}