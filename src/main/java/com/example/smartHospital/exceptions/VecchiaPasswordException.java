package com.example.smartHospital.exceptions;

public class VecchiaPasswordException extends  Exception{

    @Override
    public String getMessage() {
        return "La password inserita non corrisponde alla vecchia password";
    }
}
