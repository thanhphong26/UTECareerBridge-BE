package com.pn.career.services;

import com.pn.career.components.JWTTokenUtil;
import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.*;
import com.pn.career.exceptions.*;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.UserResponse;
import com.pn.career.utils.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizationUtils localizationUtils;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtDecoder jwtDecoder;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.password-reset.expirations}")
    private int passwordResetExpirations;
    @Override
    @Transactional
    public User registerUser(RegistrationDTO registrationDTO, String roleName) throws DataNotFoundException {
        validateUserRegistration(registrationDTO);
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        User user=registrationDTO.createUser(role);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        return userRepository.save(user);
    }

    private void validateUserRegistration(RegistrationDTO registrationDTO) {
        if (userRepository.existsByPhoneNumber(registrationDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
    }
    @Override
    public TokenDTO userLogin(LoginDTO loginDTO, String... allowedRoles) throws Exception{
        UserDetailsImpl userDetails=null;
        try {
            // Sử dụng AuthenticationManager để xác thực người dùng dựa trên email hoặc phone
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail() != null ? loginDTO.getEmail() : loginDTO.getPhoneNumber(),
                            loginDTO.getPassword()
                    )
            );
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            // Lấy đối tượng UserDetails sau khi xác thực thành công
            userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Kiểm tra quyền hạn của người dùng (nếu có)
            validateUserForLogin(userDetails.getUser(), allowedRoles);

            // Tạo cặp token JWT
            TokenDTO tokenDTO = jwtTokenUtil.generateTokenPair(authentication);

            return tokenDTO;
        } catch (InternalAuthenticationServiceException e) {
            if (e.getCause() instanceof LockedException) {
                LockedException lockedException = (LockedException) e.getCause();
                String reason = lockedException.getMessage();
                throw new LockedException(reason);
            }
            throw e;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không đúng. Vui lòng thử lại");
        } catch (AuthenticationException e) {
            throw new AuthenticationException(localizationUtils.getLocalizedMessage(MessageKeys.AUTHENTICATION_FAILED)) {};
        }
    }
    private void validateUserForLogin(User user, String... allowedRoles) throws Exception {
        if (!user.isActive()) {
            throw new PermissionDenyException("Tài khoản của bạn đã bị khóa");
        }
        if (allowedRoles.length > 0 && !isUserRoleAllowed(user, allowedRoles)) {
            throw new PermissionDenyException("Không được phép đăng nhập với vai trò này");
        }
    }
    private boolean isUserRoleAllowed(User user, String... allowedRoles) {
        return Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(user.getRole().getRoleName()));
    }
    @Override
    public User getUserDetailsFromToken(String token) throws Exception{
        Jwt jwt = jwtDecoder.decode(token); // Đảm bảo rằng jwtDecoder được cấu hình đúng
        logger.info("Token subject: {}", jwt.getSubject());
        String subject = jwt.getSubject();
        return userRepository.findUserByEmailOrPhoneNumber(subject, subject)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }
    @Override
    public void initiatePasswordReset(String email) throws Exception {
        User user=userRepository.findUserByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        String resetToken=generateToken();
        tokenRepository.revokeAllUserTokens(user.getUserId(),Token.RESET_PASSWORD);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(passwordResetExpirations);
        Token token=Token.builder()
                .user(user)
                .token(resetToken)
                .tokenType(Token.RESET_PASSWORD)
                .expirationDate(expiryDate)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
        //send email
        String url=frontendUrl+"/reset-password?token="+resetToken+"&email="+email;
        logger.info("Reset password URL: {}",url);
        emailService.sendForgotPasswordEmail(email,user.getFirstName(),url);
    }

    @Override
    @Transactional
    public void resetPassword(String resetToken, String newPassword) throws Exception {
        Token token=tokenRepository.findByTokenAndTokenType(resetToken,Token.RESET_PASSWORD)
                .orElseThrow(() -> new DataNotFoundException("Có lỗi xảy ra trong quá trình xác thực vui lòng thực hiện lại"));
        if (token.getExpirationDate().isBefore(LocalDateTime.now()) || token.isExpired() || token.isRevoked()){
            throw new ExpiredTokenException("Có lỗi xảy ra trong quá trình xác thực vui lòng thực hiện lại");
        }
        User user=token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        //invalidate token after use
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void blockOrEnable(Integer userId, boolean active) throws Exception {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(String keyword, String roleName, String sorting, PageRequest pageRequest) {
        Page<User> users=userRepository.getAllUsersByRole(keyword,roleName, sorting, pageRequest);
        return users.map(UserResponse::fromUser);
    }

    @Override
    public UserResponse getUserBydId(Integer userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        return UserResponse.fromUser(user);

    }

    @Override
    public UserResponse updateUser(Integer userId, UpdateUserDTO user) {
        User userToUpdate=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setPhoneNumber(user.getPhone());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setAddress(user.getAddress());
        userToUpdate.setDob(user.getDob());
        userToUpdate.setActive(user.getActive());
        userRepository.save(userToUpdate);
        return UserResponse.fromUser(userToUpdate);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        List<Token> tokens=tokenRepository.findByUser(user);
        tokenRepository.deleteAll(tokens);
        userRepository.delete(user);
    }

    @Override
    public boolean updatePassword(Integer userId, UpdatePasswordDTO updatePasswordDTO) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin người dùng"));
        if (!passwordEncoder.matches(updatePasswordDTO.oldPassword(),user.getPassword())){
            throw new BadCredentialsException("Mật khẩu cũ không chính xác");
        }
        if(!updatePasswordDTO.newPassword().equals(updatePasswordDTO.confirmPassword())){
            throw new DataNotFoundException("Mật khẩu mới không khớp");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.newPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public TokenDTO loginSocial(LoginDTO userLoginDTO, String roleName) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        logger.info("Role: {}", role);
        logger.info("Rolename: {}", roleName);
        Optional<User> existingUserByEmail = userRepository.findUserByEmail(userLoginDTO.getEmail());

        if (existingUserByEmail.isPresent()) {
            User existingUser = existingUserByEmail.get();
            // If user exists but doesn't have Google ID, update it
            if (userLoginDTO.getGoogleAccountId() != null &&
                    (existingUser.getGoogleAccountId() == null || existingUser.getGoogleAccountId().isEmpty())) {
                throw new DataIntegrityViolationException("Email đã được sử dụng bởi cách đăng nhập khác");
            }
        }
        // Kiểm tra Google Account ID
        if ( userLoginDTO.getGoogleAccountId() != null && !userLoginDTO.getGoogleAccountId().isEmpty()) {
            optionalUser = userRepository.findByGoogleAccountId(userLoginDTO.getGoogleAccountId());
            // Tạo người dùng mới nếu không tìm thấy
            if(optionalUser.isEmpty()){
                switch (roleName) {
                    case "student":
                        Student student = Student.builder()
                                .firstName(Optional.ofNullable(userLoginDTO.getFullname()).orElse(""))
                                .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                                .googleAccountId(userLoginDTO.getGoogleAccountId())
                                .universityEmail(userLoginDTO.getEmail())
                                .profileImage(userLoginDTO.getProfileImage())
                                .password("") // Mật khẩu trống cho đăng nhập mạng xã hội
                                .role(role)
                                .active(true)
                                .build();
                        student = studentRepository.save(student);
                        optionalUser = Optional.of(student);
                        break;
                    case "employer":
                        Employer employer = Employer.builder()
                                .firstName(Optional.ofNullable(userLoginDTO.getFullname()).orElse(""))
                                .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                                .googleAccountId(userLoginDTO.getGoogleAccountId())
                                .password("") // Mật khẩu trống cho đăng nhập mạng xã hội
                                .role(role)
                                .active(true)
                                .build();
                        employer = employerRepository.save(employer);
                        optionalUser = Optional.of(employer);
                        break;
                    default:
                        break;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Invalid social account information.");
        }

        User user = optionalUser.get();
        logger.info("User: {}", user);
        // Kiểm tra nếu tài khoản bị khóa
        if (!user.isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // Tạo Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // Credentials null vì là social login
                userDetails.getAuthorities()
        );

        // Tạo cặp token (access token và refresh token)
        return jwtTokenUtil.generateTokenPair(authentication);
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
