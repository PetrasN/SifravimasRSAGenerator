package com.petrasn.almantoprojektas.utils;

public class Toolzz {
    static public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    static public boolean isStrengthPassword(String password){
        // tikrina ar yra didziosios, mazosios, skaičiai, specialus zenklai ir ilgis 12 zenklai
        String ePattern = "^(?=.*[A-Z])(?=.*[<>!@#$&*()_=+])(?=.*[0-9])(?=.*[a-z]).{12,}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(password);
        return m.matches();
    }
}
