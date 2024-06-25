package com.example.smartHospital.controllers;

import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.exceptions.FuturoException;
import com.example.smartHospital.request.MedicoRequest;
import com.example.smartHospital.response.MedicoResponse;
import com.example.smartHospital.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> getMedicoById(@PathVariable Long id) {
        try {
            MedicoResponse medico = medicoService.getMedicoById(id);
            return new ResponseEntity<>(medico, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN"})
    public ResponseEntity<List<MedicoResponse>> getAllMedici() {
        List<MedicoResponse> medici = medicoService.getAllMedici();
        return new ResponseEntity<>(medici, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN"})
    public ResponseEntity<?> createMedico(@RequestBody MedicoRequest request) {
        try {
            MedicoResponse medico = medicoService.createMedico(request);
            return new ResponseEntity<>(medico, HttpStatus.CREATED);
        } catch (FuturoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<?> updateMedico(@PathVariable Long id, @RequestBody MedicoRequest updatedRequest) {
        try {
            MedicoResponse medico = medicoService.updateMedico(id, updatedRequest);
            if (medico != null) {
                return new ResponseEntity<>(medico, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (FuturoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deleteMedico(@PathVariable Long id) {
        try {
            medicoService.deleteMedicoById(id);
            return new ResponseEntity<>("Medico eliminato con successo", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}