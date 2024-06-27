package com.example.smartHospital.controllers;

import com.example.smartHospital.exceptions.*;
import com.example.smartHospital.request.VisitaRequest;
import com.example.smartHospital.response.GenericResponse;
import com.example.smartHospital.response.VisitaResponse;
import com.example.smartHospital.services.VisitaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/visita")
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @GetMapping("/get/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> getVisitaById(@PathVariable Long id) {
        try {
            VisitaResponse visita = visitaService.getVisitaById(id);
            return new ResponseEntity<>(visita, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @Secured({"ADMIN"})
    public ResponseEntity<List<VisitaResponse>> getAllVisite() {
        List<VisitaResponse> visite = visitaService.getAll();
        return new ResponseEntity<>(visite, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> createVisita(@RequestBody VisitaRequest request) {
        try {
            VisitaResponse visita = visitaService.createVisita(request);
            return new ResponseEntity<>(visita, HttpStatus.CREATED);
        } catch (PassatoException | NonDisponibileException | SpecializzazioneException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public ResponseEntity<?> updateVisita(@PathVariable Long id, @RequestBody VisitaRequest updatedRequest) {
        try {
            VisitaResponse visita = visitaService.updateVisita(id, updatedRequest);
            return new ResponseEntity<>(visita, HttpStatus.OK);
        } catch (PassatoException | NonDisponibileException | SpecializzazioneException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (VisitaConclusaException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ADMIN"})
    public ResponseEntity<?> deleteVisita(@PathVariable Long id) {
        try {
            visitaService.deleteVisitaById(id);
            return new ResponseEntity<>("Visita eliminata con successo", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{visitaId}/prestazioni/{prestazioneId}")
    public ResponseEntity<?> aggiungiPrestazione(@PathVariable Long visitaId, @PathVariable Long prestazioneId) throws EntityNotFoundException, VisitaConclusaException {
        try {
            visitaService.aggiungiPrestazione(visitaId, prestazioneId);
            return new ResponseEntity<>("Prestazione aggiunta con successo", HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (VisitaConclusaException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{visitaId}/concludi")
    public ResponseEntity<?> concludiVisita(@PathVariable Long visitaId) {
        try {
            GenericResponse response = visitaService.concludeVisita(visitaId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (VisitaConclusaException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/upload_referto/{id}")
    public ResponseEntity<GenericResponse> uploadReferto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException, EntityNotFoundException {
        Path tempFile = Files.createTempFile(null, null);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        String mimeType = Files.probeContentType(tempFile);

        String fileExtension = "";
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null) {
            int lastDotIndex = originalFileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
                fileExtension = originalFileName.substring(lastDotIndex + 1).toLowerCase();
            }
        }

        if ("application/pdf".equals(mimeType) || "pdf".equals(fileExtension)) {
            String newFileName = id + "_" + originalFileName;
            Path filePath = Paths.get("src/main/resources/referti/" + newFileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            visitaService.uploadReferto(id, filePath.toString());
            return new ResponseEntity<>(new GenericResponse("File caricato con successo"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new GenericResponse("Il file caricato non è un PDF"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download_referto/{id}")
    public ResponseEntity<GenericResponse> downloadReferto(@PathVariable Long id, HttpServletResponse response) throws IOException, EntityNotFoundException {
        String pathFile = visitaService.getPath(id);
        Path filePath = Path.of(pathFile);
        String mimeType = Files.probeContentType(filePath);

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filePath.getFileName().toString() + "\"");
        response.setContentLength((int) Files.size(filePath));
        Files.copy(filePath, response.getOutputStream());

        return new ResponseEntity<>(new GenericResponse("File scaricato con successo"), HttpStatus.OK);
    }
}