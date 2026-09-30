package resume_x_backend.controller;

import resume_x_backend.entity.Resume;
import resume_x_backend.service.ResumeService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:5174")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // GET all resumes of logged-in user
    @GetMapping
    public ResponseEntity<List<Resume>> getResumes(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                resumeService.getUserResumes(email)
        );
    }

    // CREATE resume
    @PostMapping
    public ResponseEntity<Resume> createResume(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {

        String email = authentication.getName();

        String resumeName =
                (String) request.get("resumeName");

        Object resumeData =
                request.get("resumeData");

        String template =
                (String) request.get("template");

        Resume resume = resumeService.createResume(
                email,
                resumeName,
                resumeData,
                template
        );

        return ResponseEntity.ok(resume);
    }

    // UPDATE resume
    @PutMapping("/{id}")
    public ResponseEntity<Resume> updateResume(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        String email = authentication.getName();

        String resumeName =
                (String) request.get("resumeName");

        Object resumeData =
                request.get("resumeData");

        String template =
                (String) request.get("template");

        Resume resume = resumeService.updateResume(
                email,
                id,
                resumeName,
                resumeData,
                template
        );

        return ResponseEntity.ok(resume);
    }

    // DELETE resume
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResume(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        resumeService.deleteResume(email, id);

        return ResponseEntity.ok(
                "Resume deleted successfully"
        );
    }

    // DUPLICATE resume
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Resume> duplicateResume(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        Resume copy =
                resumeService.duplicateResume(email, id);

        return ResponseEntity.ok(copy);
    }
}