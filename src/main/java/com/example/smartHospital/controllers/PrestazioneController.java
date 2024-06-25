package com.example.smartHospital.controllers;

import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.request.PrestazioneRequest;
import com.example.smartHospital.response.PrestazioneResponse;
import com.example.smartHospital.services.PrestazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestazione")
public class PrestazioneController {

    @Autowired
    private PrestazioneService prestazioneService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<?> getPrestazioneById(@PathVariable Long id) {
        try {
            PrestazioneResponse prestazione = prestazioneService.getPrestazioneById(id);
            return new ResponseEntity<>(prestazione, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<List<PrestazioneResponse>> getAllPrestazioni() {
        List<PrestazioneResponse> prestazioni = prestazioneService.getAllPrestazioni();
        return new ResponseEntity<>(prestazioni, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN"})
    public ResponseEntity<?> createPrestazione(@RequestBody PrestazioneRequest request) {
        try {
            PrestazioneResponse prestazione = prestazioneService.createPrestazione(request);
            return new ResponseEntity<>(prestazione, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> updatePrestazione(@PathVariable Long id, @RequestBody PrestazioneRequest updatedRequest) {
        try {
            PrestazioneResponse prestazione = prestazioneService.updatePrestazione(id, updatedRequest);
            return new ResponseEntity<>(prestazione, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deletePrestazione(@PathVariable Long id) {
        try {
            prestazioneService.deletePrestazioneById(id);
            return new ResponseEntity<>("Prestazione eliminata con successo", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
