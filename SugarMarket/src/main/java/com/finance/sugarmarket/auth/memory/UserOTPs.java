package com.finance.sugarmarket.auth.memory;

import java.util.HashMap;
import java.util.Map;

import com.finance.sugarmarket.auth.model.OTPDetails;

public class UserOTPs {
    private static UserOTPs instance;

    private Map<String, OTPDetails> otpMap;
    private UserOTPs() {
        otpMap = new HashMap<>();
    }

    public static synchronized UserOTPs getInstance() {
        if (instance == null) {
            instance = new UserOTPs();
        }
        return instance;
    }

    public void addOtp(String userId, OTPDetails token) {
        otpMap.put(userId, token);
    }

    public OTPDetails getOtp(String userId) {
        return otpMap.get(userId);
    }

    public void removeOtp(String userId) {
        otpMap.remove(userId);
    }
}