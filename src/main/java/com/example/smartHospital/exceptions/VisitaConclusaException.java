package com.example.smartHospital.exceptions;

public class VisitaConclusaException  extends Exception{

    @Override
    public String getMessage() {
        return "La visita è conclusa non è possibile modificarla in alcun modo";
    }
}
