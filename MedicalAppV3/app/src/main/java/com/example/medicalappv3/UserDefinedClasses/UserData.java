package com.example.medicalappv3.UserDefinedClasses;

import java.io.Serializable;

public class UserData implements Serializable {
    String USERNAME;
    String PASSWORD;
    String NAME;
    String OCCUPATION;
    String MOBILE;
    String AGE;
    String GENDER;
    String EMAIL;
    String OTP;
    String timeis;

    public UserData(String USERNAME, String PASSWORD, String NAME, String OCCUPATION, String MOBILE, String AGE,
                    String GENDER, String email) {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.NAME = NAME;
        this.OCCUPATION = OCCUPATION;
        this.MOBILE = MOBILE;
        this.AGE = AGE;
        this.GENDER = GENDER;
        this.EMAIL = email;
        this.OTP = null;
        this.timeis = null;
    }

    public UserData(UserData userData) {
        this.USERNAME = userData.getUSERNAME();
        this.PASSWORD = userData.getPASSWORD();
        this.NAME = userData.getNAME();
        this.OCCUPATION = userData.getOCCUPATION();
        this.MOBILE = userData.getMOBILE();
        this.AGE = userData.getAGE();
        this.GENDER = userData.getGENDER();
        this.EMAIL = userData.getEMAIL();
        this.OTP = null;
        this.timeis = null;
    }

    public UserData() {
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getOCCUPATION() {
        return OCCUPATION;
    }

    public void setOCCUPATION(String OCCUPATION) {
        this.OCCUPATION = OCCUPATION;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getAGE() {
        return AGE;
    }

    public void setAGE(String AGE) {
        this.AGE = AGE;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getTimeis() {
        return timeis;
    }

    public void setTimeis(String timeis) {
        this.timeis = timeis;
    }
}
