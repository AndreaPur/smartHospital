package com.example.smartHospital.controllers;

import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.exceptions.FuturoException;
import com.example.smartHospital.request.PrenotazioneRequest;
import com.example.smartHospital.response.PrenotazioneResponse;
import com.example.smartHospital.services.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prenotazione")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> getPrenotazioneById(@PathVariable Long id) {
        try {
            PrenotazioneResponse prenotazione = prenotazioneService.getPrenotazioneById(id);
            return new ResponseEntity<>(prenotazione, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<List<PrenotazioneResponse>> getAllPrenotazioni() {
        List<PrenotazioneResponse> prenotazioni = prenotazioneService.getAllPrenotazioni();
        return new ResponseEntity<>(prenotazioni, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> createPrenotazione(@RequestBody PrenotazioneRequest request) {
        try {
            PrenotazioneResponse prenotazione = prenotazioneService.createPrenotazione(request);
            return new ResponseEntity<>(prenotazione, HttpStatus.CREATED);
        } catch (EntityNotFoundException | FuturoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> updatePrenotazione(@PathVariable Long id, @RequestBody PrenotazioneRequest updatedRequest) {
        try {
            PrenotazioneResponse prenotazione = prenotazioneService.updatePrenotazione(id, updatedRequest);
            return new ResponseEntity<>(prenotazione, HttpStatus.OK);
        } catch (EntityNotFoundException | FuturoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deletePrenotazione(@PathVariable Long id) {
        try {
            prenotazioneService.deletePrenotazioneById(id);
            return new ResponseEntity<>("Prenotazione eliminata con successo", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}