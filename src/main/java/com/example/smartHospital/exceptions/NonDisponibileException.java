package com.example.smartHospital.exceptions;

public class NonDisponibileException extends Exception{

    @Override
    public String getMessage() {
        return "Il dottore non è disponibile al di fuori degli orari indicati";
    }
}
