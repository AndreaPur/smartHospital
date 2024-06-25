package com.example.smartHospital.exceptions;

public class PasswordCortaException extends Exception{

    @Override
    public String getMessage() {
        return "La password deve essere minimo di 8 caratteri";
    }
}
