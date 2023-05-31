package com.project.course_project_1.repository;


import com.project.course_project_1.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    Optional<UserImage> findFirstByUserId(Long userId);

    void deleteById(Long id);

    void deleteAllByUserId(Long userId);
}