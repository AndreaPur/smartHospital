package com.example.smartHospital.security;

import com.example.smartHospital.exceptions.*;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.request.AuthenticationRequest;
import com.example.smartHospital.request.ChangePwRequest;
import com.example.smartHospital.request.RegistrationRequest;
import com.example.smartHospital.request.RegistrationMedRequest;
import com.example.smartHospital.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UtenteRepository utenteRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (FuturoException e) {
            return new ResponseEntity<>(new ErrorResponse("FuturoException", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/registerMedico")
    public ResponseEntity<?> registerMedico(@RequestBody RegistrationMedRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.registerMedico(request));
        } catch (FuturoException e) {
            return new ResponseEntity<>(new ErrorResponse("FuturoException", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
        } catch (UserNotConformedException e){
            return new ResponseEntity<>(new ErrorResponse("UserNotConfirmedException", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout/{id}")
    @Secured({"ADMIN", "MEDICO", "PAZIENTE"})
    public void logout(HttpServletRequest httpRequest, @PathVariable Long id){
        authenticationService.logout(httpRequest, id);
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam Long id, @RequestParam String token){
        if (authenticationService.confirmRegistration(id, token)){
            return new ResponseEntity<>(new GenericResponse("Conferma avvenuta con successo"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("NotConfirmedException", "OPS! Qualcosa è andato storto con la conferma del tuo account!"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/confirmMed")
    public ResponseEntity<?> confirmRegistrationMed(@RequestParam Long id, @RequestParam String token){
        logger.info("Ricevuta richiesta di conferma per Medico ID: {}, Token: {}", id, token);
        if (authenticationService.confirmRegistrationMed(id, token)){
            logger.info("Conferma avvenuta con successo per Medico ID: {}", id);
            return new ResponseEntity<>(new GenericResponse("Conferma avvenuta con successo"), HttpStatus.OK);
        }
        logger.error("Errore durante la conferma della registrazione per Medico ID: {}", id);
        return new ResponseEntity<>(new ErrorResponse("NotConfirmedException", "OPS! Qualcosa è andato storto con la conferma del tuo account!"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/changepw")
    public ResponseEntity<?> changePassword(HttpServletRequest httpRequest, @RequestBody ChangePwRequest request) {
        try {
            if (authenticationService.changePassword(request)) {
                authenticationService.logout(httpRequest, request.getId_utente());
                return new ResponseEntity<>(new GenericResponse("Password cambiata con successo"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new GenericResponse("Errore con il cambio password"), HttpStatus.BAD_REQUEST);
        } catch (VecchiaPasswordException | PasswordCortaException | CarattereSpecialeNotFoundException | MaiuscolaNotFoundException |
                 NumeroNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getClass().getSimpleName(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Exception", "Errore durante il cambio della password"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/invia-email-recupero")
    public ResponseEntity<?> sendRecoveryEmail(@RequestParam String email, @RequestParam String newPassword) {
        try {
            authenticationService.sendRecoveryEmail(email, newPassword);
            return new ResponseEntity<>(new GenericResponse("Email inviata con successo"), HttpStatus.OK);
        } catch (PasswordCortaException | CarattereSpecialeNotFoundException | MaiuscolaNotFoundException | NumeroNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getClass().getSimpleName(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getClass().getSimpleName(), e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Exception", "Errore durante l'invio dell'email"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recoverpw")
    public ResponseEntity<?> recoverPassword(@RequestParam Long id, @RequestParam String token) {
        if (authenticationService.recoverPassword(id, token)) {
            return new ResponseEntity<>(new GenericResponse("Cambio password avvenuto con successo"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("InvalidTokenException", "Token non valido o scaduto"), HttpStatus.BAD_REQUEST);
    }
}