package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.entity.Certificate;
import com.arabsoft.elearning.entity.Course;
import com.arabsoft.elearning.entity.User;
import com.arabsoft.elearning.repository.CertificateRepository;
import com.arabsoft.elearning.repository.CourseRepository;
import com.arabsoft.elearning.repository.UserRepository;
import com.arabsoft.elearning.service.CertificateGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CertificateGeneratorService certificateGeneratorService;

    @GetMapping("/my")
    public ResponseEntity<List<Certificate>> getMyCertificates(Authentication authentication) {
        User student = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return ResponseEntity.ok(certificateRepository.findByStudentId(student.getId()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long id,
            Authentication authentication) {

        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificat non trouvé"));

        User student = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Course course = cert.getCourse();

        byte[] pdfBytes = certificateGeneratorService.generateCertificate(cert, student, course);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"certificat_" + cert.getUniqueNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}