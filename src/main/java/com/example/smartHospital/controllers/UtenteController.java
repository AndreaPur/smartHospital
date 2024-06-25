package com.example.smartHospital.controllers;

import com.example.smartHospital.entities.Utente;
import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.request.UtenteRequest;
import com.example.smartHospital.response.MedicoResponse;
import com.example.smartHospital.response.UtenteResponse;
import com.example.smartHospital.services.MedicoService;
import com.example.smartHospital.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private MedicoService medicoService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> getUtenteById (@PathVariable Long id){
        try {
            UtenteResponse utente = utenteService.getUtenteById(id);
            return new ResponseEntity<>(utente, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN"})
    public ResponseEntity<List<UtenteResponse>> getAllUtenti() {
        List<UtenteResponse> utenti = utenteService.getAll();
        return new ResponseEntity<>(utenti, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN"})
    public ResponseEntity<UtenteResponse> createUtente(@RequestBody UtenteRequest request) {
        try {
            UtenteResponse utente = utenteService.createUtente(request);
            return new ResponseEntity<>(utente, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<UtenteResponse> updateUtente(@PathVariable Long id, @RequestBody UtenteRequest updatedRequest) {
        try {
            UtenteResponse utente = utenteService.updateUtente(id, updatedRequest);
            return new ResponseEntity<>(utente, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deleteUtente(@PathVariable Long id) {
        try {
            utenteService.deleteUtenteById(id);
            return new ResponseEntity<>("Utente eliminato con successo", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/specialisti")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> findMediciBySpecializzazione(@RequestParam String specializzazione) {
        try {
            List<MedicoResponse> medici = medicoService.findByRoleAndSpecializzazione(specializzazione);
            return new ResponseEntity<>(medici, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
