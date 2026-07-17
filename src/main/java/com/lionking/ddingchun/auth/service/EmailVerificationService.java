package com.lionking.ddingchun.auth.service;

import com.lionking.ddingchun.auth.entity.EmailVerification;
import com.lionking.ddingchun.auth.exception.EmailCodeExpiredException;
import com.lionking.ddingchun.auth.exception.EmailCodeMismatchException;
import com.lionking.ddingchun.auth.exception.EmailVerificationNotFoundException;
import com.lionking.ddingchun.auth.exception.InvalidSchoolEmailException;
import com.lionking.ddingchun.auth.repository.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String SCHOOL_EMAIL_DOMAIN = "@mju.ac.kr";
    private static final long CODE_EXPIRE_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String EMAIL_TEMPLATE_PATH = "templates/verification-email.html";
    private static final String EMAIL_LOGO_PATH = "static/images/ddingchun_logo.png";

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
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[띵춘] 이메일 인증번호");
            helper.setText(buildHtmlBody(code), true);
            helper.addInline("logo", new ClassPathResource(EMAIL_LOGO_PATH), "image/png");
        } catch (MessagingException exception) {
            throw new IllegalStateException("인증 메일을 만드는 중 오류가 발생했습니다.", exception);
        }

        mailSender.send(message);

        log.info("[학교 이메일 인증] {} 로 인증 메일 발송 완료", email);
    }

    private String buildHtmlBody(String code) {
        String template;

        try {
            template = StreamUtils.copyToString(
                    new ClassPathResource(EMAIL_TEMPLATE_PATH).getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException("인증 메일 템플릿을 읽지 못했습니다.", exception);
        }

        return template
                .replace("{{CODE}}", code)
                .replace("{{MINUTES}}", String.valueOf(CODE_EXPIRE_MINUTES));
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