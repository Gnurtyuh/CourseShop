package com.javaweb.web.util;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordUtil {
    String currentPassword;
    String newPassword;
}
