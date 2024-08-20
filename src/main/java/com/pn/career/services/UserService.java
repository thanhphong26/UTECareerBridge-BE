package com.pn.career.services;

import com.pn.career.components.JWTTokenUtil;
import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.StudentLoginDTO;
import com.pn.career.dtos.StudentRegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.Role;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.repositories.RoleRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.ValidationUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService{
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LocalizationUtils localizationUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public User studentRegister(StudentRegistrationDTO studentRegistrationDTO) throws Exception {
        if(!studentRegistrationDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(studentRegistrationDTO.getPhoneNumber())){
            throw new DataIntegrityViolationException("Phone number already exist!");
        }
        if(!studentRegistrationDTO.getEmail().isBlank() && userRepository.existsByEmail(studentRegistrationDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exist!");
        }
        Role role=roleRepository.findByRoleName("student").orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        Student student =Student.builder()
                .firstName(studentRegistrationDTO.getFirsName())
                .lastName(studentRegistrationDTO.getLastName())
                .phoneNumber(studentRegistrationDTO.getPhoneNumber())
                .universityEmail(studentRegistrationDTO.getEmail())
                .email(studentRegistrationDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(studentRegistrationDTO.getPassword()))
                .build();
        student.setRole(role);
        return userRepository.save(student);

    }
    @Override
    public TokenDTO login(StudentLoginDTO studentLoginDTO) throws Exception {
        Optional<User> optionalStudent = Optional.empty();
        String subject = null;
        if (studentLoginDTO.getPhoneNumber() != null && !studentLoginDTO.getPhoneNumber().isBlank()) {
            optionalStudent = userRepository.findUserByPhoneNumber(studentLoginDTO.getPhoneNumber());
            subject = studentLoginDTO.getPhoneNumber();
        }
        // If the user is not found by phone number, check by email
        if (optionalStudent.isEmpty() && studentLoginDTO.getEmail() != null) {
            optionalStudent = userRepository.findUserByEmail(studentLoginDTO.getEmail());
            subject = studentLoginDTO.getEmail();
        }
        // If user is not found, throw an exception
        if (optionalStudent.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        //return optionalUser.get();//muốn trả JWT token ?
        Student existingUser= (Student) optionalStudent.get();
        //check password
        if(!bCryptPasswordEncoder.matches(studentLoginDTO.getPassword(), existingUser.getPassword())) {
            throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        if(!optionalStudent.get().isActive()){
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }
        // Create authentication token using the found subject and granted authorities
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject,
                studentLoginDTO.isPasswordBlank()  ? "" : studentLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );
        //authentication with java spring
        logger.info("Authenticating user with subject: {}", subject);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
            logger.info("Authentication successful for user: {}", subject);
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}. Reason: {}", subject, e.getMessage());
            throw e;
        }        return jwtTokenUtil.generateTokenPair(authentication);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubjectFromToken(token);
        Optional<User> user;
        if (ValidationUtils.isValidPhoneNumber(subject)) {
            user = userRepository.findUserByPhoneNumber(subject);
        } else if (ValidationUtils.isValidEmail(subject)) {
            user = userRepository.findUserByEmail(subject);
        } else {
            throw new Exception("Invalid token subject");
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }

}
