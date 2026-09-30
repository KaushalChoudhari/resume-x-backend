package resume_x_backend.repository;

import resume_x_backend.entity.Resume;
import resume_x_backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);

    void deleteByUser(User user);
}