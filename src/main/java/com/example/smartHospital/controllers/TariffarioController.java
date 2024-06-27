package com.example.smartHospital.controllers;

import com.example.smartHospital.exceptions.EntityNotFoundException;
import com.example.smartHospital.request.TariffarioRequest;
import com.example.smartHospital.response.TariffarioResponse;
import com.example.smartHospital.services.TariffarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tariffario")
public class TariffarioController {

    @Autowired
    private TariffarioService tariffarioService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<?> getTariffarioById(@PathVariable Long id) {
        try {
            TariffarioResponse tariffario = tariffarioService.getTariffarioById(id);
            return new ResponseEntity<>(tariffario, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN", "MEDICO"})
    public ResponseEntity<List<TariffarioResponse>> getAllTariffari() {
        List<TariffarioResponse> tariffari = tariffarioService.getAllTariffari();
        return new ResponseEntity<>(tariffari, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN"})
    public ResponseEntity<?> createTariffario(@RequestBody TariffarioRequest request) {
        try {
            TariffarioResponse tariffario = tariffarioService.createTariffario(request);
            return new ResponseEntity<>(tariffario, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> updateTariffario(@PathVariable Long id, @RequestBody TariffarioRequest updatedRequest) throws EntityNotFoundException {
//        try {
            TariffarioResponse tariffario = tariffarioService.updateTariffario(id, updatedRequest);
            return new ResponseEntity<>(tariffario, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deleteTariffario(@PathVariable Long id) {
        try {
            tariffarioService.deleteTariffarioById(id);
            return new ResponseEntity<>("Tariffario eliminato con successo", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}