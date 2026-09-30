package resume_x_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import resume_x_backend.entity.Resume;
import resume_x_backend.entity.User;
import resume_x_backend.repository.ResumeRepository;
import resume_x_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // Current user find karo using JWT email
    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Current user ke saare resumes
    public List<Resume> getUserResumes(String email) {

        User user = getUserByEmail(email);

        return resumeRepository.findByUser(user);
    }

    // New resume create
    public Resume createResume(
            String email,
            String resumeName,
            Object resumeData,
            String template) {

        User user = getUserByEmail(email);

        Resume resume = new Resume();

        resume.setResumeName(resumeName);
        resume.setResumeData(convertToJson(resumeData));
        resume.setTemplate(template);
        resume.setUser(user);

        return resumeRepository.save(resume);
    }

    // Resume update
    public Resume updateResume(
            String email,
            Long resumeId,
            String resumeName,
            Object resumeData,
            String template) {

        User user = getUserByEmail(email);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        // Security: doosre user ka resume update nahi ho sakta
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        resume.setResumeName(resumeName);
        resume.setResumeData(convertToJson(resumeData));
        resume.setTemplate(template);

        return resumeRepository.save(resume);
    }

    // Resume delete
    public void deleteResume(
            String email,
            Long resumeId) {

        User user = getUserByEmail(email);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        // Security check
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        resumeRepository.delete(resume);
    }

    // Resume duplicate
    public Resume duplicateResume(
            String email,
            Long resumeId) {

        User user = getUserByEmail(email);

        Resume original = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        // Security check
        if (!original.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        Resume copy = new Resume();

        copy.setResumeName(
                original.getResumeName() + " Copy"
        );

        copy.setResumeData(
                original.getResumeData()
        );

        copy.setTemplate(
                original.getTemplate()
        );

        copy.setUser(user);

        return resumeRepository.save(copy);
    }

    // Object → JSON String
    private String convertToJson(Object data) {

        try {
            return objectMapper.writeValueAsString(data);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Unable to convert resume data to JSON",
                    e
            );
        }
    }
}