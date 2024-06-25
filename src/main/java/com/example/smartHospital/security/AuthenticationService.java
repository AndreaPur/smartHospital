package com.example.smartHospital.security;

import com.example.smartHospital.entities.Medico;
import com.example.smartHospital.entities.Utente;
import com.example.smartHospital.entities.TokenBlackList;
import com.example.smartHospital.entities.Utente;
import com.example.smartHospital.exceptions.*;
import com.example.smartHospital.repositories.MedicoRepository;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.repositories.UtenteRepository;
import com.example.smartHospital.request.AuthenticationRequest;
import com.example.smartHospital.request.ChangePwRequest;
import com.example.smartHospital.request.RegistrationMedRequest;
import com.example.smartHospital.request.RegistrationRequest;
import com.example.smartHospital.response.AuthenticationResponse;
import com.example.smartHospital.services.TokenBlackListService;
import com.example.smartHospital.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenBlackListService tokenBlackListService;
    @Autowired
    private JavaMailSender javaMailSender;

    private Map<Long, String> temporaryPasswords = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse register (RegistrationRequest request) throws FuturoException {
        if (request.getDataNascita().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
            throw new FuturoException();
        }
        var user = Utente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .codiceFiscale(request.getCodiceFiscale())
                .email(request.getEmail())
                .comune(request.getComune())
                .indirizzo(request.getIndirizzo())
                .telefono(request.getTelefono())
                .dataNascita(request.getDataNascita())
                .role(Role.TOCONFIRM)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        var jwtToken = jwtService.generateToken(user);
        user.setRegistrationToken(jwtToken);
        utenteRepository.saveAndFlush(user);
        javaMailSender.send(createConfirmationMail(user));
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse registerMedico(RegistrationMedRequest request) throws FuturoException {
        if (request.getDataNascita().isAfter(LocalDate.now())) {
            throw new FuturoException();
        }

        Medico medico = Medico.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .codiceFiscale(request.getCodiceFiscale())
                .email(request.getEmail())
                .comune(request.getComune())
                .indirizzo(request.getIndirizzo())
                .telefono(request.getTelefono())
                .dataNascita(request.getDataNascita())
                .password(passwordEncoder.encode(request.getPassword()))
                .specializzazione(request.getSpecializzazione())
                .orarioInizio(LocalDateTime.of(LocalDate.now(), LocalTime.of(request.getOrarioInizio(), 0)))
                .orarioFine(LocalDateTime.of(LocalDate.now(), LocalTime.of(request.getOrarioFine(), 0)))
                .role(Role.TOCONFIRM)
                .build();

        String jwtToken = jwtService.generateToken(medico);
        medico.setRegistrationToken(jwtToken);
        medicoRepository.saveAndFlush(medico);
        javaMailSender.send(createConfirmationMailMedico(medico));
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws UserNotConformedException, EntityNotFoundException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        ));
        var user = utenteRepository.findUtenteByEmail(authenticationRequest.getEmail());
        if (user == null) {
            throw new EntityNotFoundException(-1L, "Utente");
        }
        if (user.getRole().equals(Role.TOCONFIRM)) {
            throw new UserNotConformedException();
        }
        var jwtToken = jwtService.generateToken(user);
        if (tokenBlackListService.tokenNotValidFromUtenteById(user.getId()).contains(jwtToken)) {
            String email = jwtService.extractUsername(jwtToken);
            UserDetails userDetails = utenteRepository.findUtenteByEmail(email);
            String newToken = jwtService.generateToken(userDetails);
            return AuthenticationResponse.builder().token(newToken).build();
        }
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public void logout(HttpServletRequest httpRequest, Long id) {
        String token = extractTokenFromRequest(httpRequest);
        TokenBlackList tokenBlackList = TokenBlackList.builder()
                .utente(utenteRepository.getReferenceById(id))
                .token(token)
                .build();
        tokenBlackListService.createTokenBlackList(tokenBlackList);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String autherizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(autherizationHeader) && autherizationHeader.startsWith("Bearer ")) {
            return autherizationHeader.substring(7);
        }
        return null;
    }

    public boolean confirmRegistration(Long id, String token){
        Utente utente = utenteRepository.getReferenceById(id);
        if (utente.getRegistrationToken().equals(token)) {
            utente.setRole(Role.PAZIENTE);
            utenteRepository.saveAndFlush(utente);
            return true;
        }
        return false;
    }

    public boolean confirmRegistrationMed(Long id, String token) {
        Medico medico = medicoRepository.getReferenceById(id);
        logger.info("Confermando registrazione per Medico ID: {}, Token: {}", id, token);
        if (medico.getRegistrationToken().equals(token)) {
            medico.setRole(Role.MEDICO);
            medicoRepository.saveAndFlush(medico);
            logger.info("Registrazione confermata per Medico ID: {}, Ruolo assegnato: MEDICO", id);
            return true;
        }
        logger.error("Token non valido per Medico ID: {}", id);
        return false;
    }

    public SimpleMailMessage createConfirmationMail(Utente utente) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utente.getEmail());
        message.setSubject("CONFERMA REGISTRAZIONE");
        String url = "http://localhost:8080/auth/confirm?id=" + utente.getId() + "&token=" + utente.getRegistrationToken();
        message.setText("Clicca qui per confermare la registrazione: " + url);
        return message;
    }

    public SimpleMailMessage createConfirmationMailMedico(Medico medico) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(medico.getEmail());
        message.setSubject("CONFERMA REGISTRAZIONE MEDICO");
        String url = "http://localhost:8080/auth/confirmMed?id=" + medico.getId() + "&token=" + medico.getRegistrationToken();
        message.setText("Clicca qui per confermare la registrazione come medico: " + url);
        logger.info("Email di conferma inviata a: {}, URL: {}", medico.getEmail(), url);
        return message;
    }

    public boolean changePassword(ChangePwRequest request) throws VecchiaPasswordException, CarattereSpecialeNotFoundException, MaiuscolaNotFoundException, NumeroNotFoundException, PasswordCortaException {
        Utente utente = utenteRepository.getReferenceById(request.getId_utente());
        if (!passwordEncoder.matches(request.getOldPassword(), utente.getPassword())) {
            throw new VecchiaPasswordException();
        }
        verificaFormatoPw(request.getNewPassword());
        utente.setPassword(passwordEncoder.encode(request.getNewPassword()));
        utenteRepository.save(utente);
        return true;
    }

    public void sendRecoveryEmail(String email, String newPassword) throws PasswordCortaException, CarattereSpecialeNotFoundException, MaiuscolaNotFoundException, NumeroNotFoundException, EntityNotFoundException {
        Utente utente = utenteRepository.findUtenteByEmail(email);
        if (utente == null) {
            throw new EntityNotFoundException(-1L, "Utente");
        }
        storeTemporaryPassword(utente.getId(), newPassword);
        javaMailSender.send(createRecoverPwMail(utente));
    }

    public boolean recoverPassword(Long id, String token) {
        Utente utente = utenteRepository.getReferenceById(id);
        if (utente.getRegistrationToken().equals(token) && temporaryPasswords.containsKey(id)) {
            utente.setPassword(passwordEncoder.encode(temporaryPasswords.get(id)));
            utenteRepository.save(utente);
            temporaryPasswords.remove(id);
            return true;
        }
        return false;
    }

    public SimpleMailMessage createRecoverPwMail(Utente utente) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utente.getEmail());
        message.setSubject("Recupero Password");
        String url = "http://localhost:8080/auth/recoverpw?id=" + utente.getId() + "&token=" + utente.getRegistrationToken();
        message.setText("Clicca qui per recuperare la password: " + url);
        return message;
    }

    public void storeTemporaryPassword(Long id, String newPassword) throws PasswordCortaException, CarattereSpecialeNotFoundException, MaiuscolaNotFoundException, NumeroNotFoundException {
        verificaFormatoPw(newPassword);
        temporaryPasswords.put(id, newPassword);
    }

    public void verificaFormatoPw(String newPassword) throws PasswordCortaException, CarattereSpecialeNotFoundException, MaiuscolaNotFoundException, NumeroNotFoundException {
        if (newPassword.length() < 8) {
            throw new PasswordCortaException();
        }
        boolean hasSpecialChar = false;
        boolean hasUpperCase = false;
        boolean hasDigit = false;
        String specialCharacters = "!#£$%/()=?@*+-*:;_^<>";
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : newPassword.toCharArray()) {
            if (specialCharacters.indexOf(c) != -1) {
                hasSpecialChar = true;
            }
            if (upperCaseLetters.indexOf(c) != -1) {
                hasUpperCase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasSpecialChar && hasUpperCase && hasDigit) {
                break;
            }
        }
        if (!hasSpecialChar) {
            throw new CarattereSpecialeNotFoundException();
        }
        if (!hasUpperCase) {
            throw new MaiuscolaNotFoundException();
        }
        if (!hasDigit) {
            throw new NumeroNotFoundException();
        }
    }
}
