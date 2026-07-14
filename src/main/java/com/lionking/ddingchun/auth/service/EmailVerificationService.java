package com.lionking.ddingchun.auth.service;

import com.lionking.ddingchun.auth.entity.EmailVerification;
import com.lionking.ddingchun.auth.exception.EmailCodeExpiredException;
import com.lionking.ddingchun.auth.exception.EmailCodeMismatchException;
import com.lionking.ddingchun.auth.exception.EmailVerificationNotFoundException;
import com.lionking.ddingchun.auth.exception.InvalidSchoolEmailException;
import com.lionking.ddingchun.auth.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String SCHOOL_EMAIL_DOMAIN = "@mju.ac.kr";
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendVerificationCode(String rawEmail) {
        String email = normalize(rawEmail);
        validateSchoolEmail(email);

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES);

        emailVerificationRepository.findByEmail(email)
                .ifPresentOrElse(
                        verification -> verification.renew(code, expiresAt),
                        () -> emailVerificationRepository.save(new EmailVerification(email, code, expiresAt))
                );

        sendMail(email, code);
    }

    private void sendMail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[띵춘] 이메일 인증번호");
        message.setText("인증번호는 [%s] 입니다. %d분 이내에 입력해주세요.".formatted(code, CODE_EXPIRE_MINUTES));

        mailSender.send(message);

        log.info("[학교 이메일 인증] {} 로 인증 메일 발송 완료", email);
    }

    @Transactional
    public void verifyCode(String rawEmail, String code) {
        String email = normalize(rawEmail);

        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(EmailVerificationNotFoundException::new);

        if (verification.isExpired(LocalDateTime.now())) {
            throw new EmailCodeExpiredException();
        }

        if (!verification.matchesCode(code)) {
            throw new EmailCodeMismatchException();
        }

        verification.verify();
    }

    @Transactional(readOnly = true)
    public boolean isVerified(String rawEmail) {
        String email = normalize(rawEmail);
        return emailVerificationRepository.findByEmail(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    private void validateSchoolEmail(String email) {
        if (!email.endsWith(SCHOOL_EMAIL_DOMAIN)) {
            throw new InvalidSchoolEmailException();
        }
    }

    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}