package com.example.smartHospital.exceptions;

public class PassatoException extends Exception{

    @Override
    public String getMessage() {
        return "Marty, non hai proprio il senso del tempo!";
    }
}
