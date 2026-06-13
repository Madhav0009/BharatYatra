package com.Madhav.bharatYatra.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final JavaMailSender javaMailSender;

    private final SecureRandom secureRandom = new SecureRandom();

    private final Map<String, OtpData> otpStorage =
            new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String fromEmail;

    public OtpService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // SEND OTP
    public void sendOtp(String toEmail) {

        String otp = generateOtp();

        otpStorage.put(
                toEmail,
                new OtpData(
                        otp,
                        LocalDateTime.now().plusMinutes(5)
                )
        );

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kadam409409@gmail.com");
        message.setTo(toEmail);

        message.setSubject("BharatYatra Password Reset OTP");

        message.setText(
                "Hello,\n\n" +
                "Your OTP is: " + otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "Thank you,\nBharatYatra Team"
        );

        try {
            System.out.println("FROM EMAIL = " + fromEmail);
            System.out.println("TO EMAIL = " + toEmail);

            javaMailSender.send(message);

            System.out.println("OTP sent successfully");

        } catch (Exception e) {

            System.out.println("MAIL ERROR START");
            e.printStackTrace();
            System.out.println("ERROR MESSAGE = " + e.getMessage());
            System.out.println("MAIL ERROR END");

            throw e;
        }
    }
    // GENERATE OTP
    private String generateOtp() {

        int otp = 100000 + secureRandom.nextInt(900000);

        return String.valueOf(otp);
    }

    // VERIFY OTP
    public boolean verifyOtp(String email, String enteredOtp) {

        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            return false;
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(otpData.expiryTime())) {

            otpStorage.remove(email);

            return false;
        }

        return otpData.otp().equals(enteredOtp);
    }
    
    

    // CLEAR OTP AFTER SUCCESS
    public void clearOtp(String email) {
        otpStorage.remove(email);
    }

    // OTP RECORD
    private record OtpData(
            String otp,
            LocalDateTime expiryTime
    ) {
    }
}