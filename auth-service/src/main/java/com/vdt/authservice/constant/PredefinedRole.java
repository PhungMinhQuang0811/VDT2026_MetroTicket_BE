package com.vdt.authservice.constant;

public class PredefinedRole {
    // Internal - Only for management, can not register
    public static final String ADMIN = "ADMIN";
    public static final String ADMIN_1 = "ADMIN_1";
    public static final String ADMIN_2 = "ADMIN_2";

    // External - Can select when register
    public static final String USER_1 = "USER_1";
    public static final String USER_2 = "USER_2";

    private PredefinedRole() {}
}
