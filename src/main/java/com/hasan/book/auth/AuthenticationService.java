package com.hasan.book.auth;

import com.hasan.book.email.EmailService;
import com.hasan.book.email.EmailTemplateName;
import com.hasan.book.role.RoleRepository;
import com.hasan.book.user.Token;
import com.hasan.book.user.TokenRepository;
import com.hasan.book.user.User;
import com.hasan.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));

        var user = User.builder() // We can use .builder() because we have @Builder in User class
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // We encode the password before saving it
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user); // Save the user to the database
        sendValidationEmail(user); // Send a validation email to the user, so it can enable the account
    }

    // Sends a validation email to the user
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Activate your account"
        );
    }

    // Generates an activation token and saves it to the database
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);

        var token = Token.builder() // We can use .builder() because we have @Builder in our Token class
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // Token expires in 15 minutes
                .user(user)
                .build();

        tokenRepository.save(token); // Save the token to the database
        return generatedToken;
    }

    // Generates a random code of a given length
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom(); // SecureRandom is a cryptographically strong random number generator
        for (int i=0; i<length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

}
