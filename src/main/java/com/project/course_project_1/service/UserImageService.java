package com.project.course_project_1.service;


import com.project.course_project_1.entity.UserImage;
import com.project.course_project_1.repository.UserImageRepository;
import com.project.course_project_1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Optional;


@Slf4j
@Service
public class UserImageService {
    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public UserImageService(UserImageRepository userImageRepository, UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
    }

    public void saveUserImage(MultipartFile image, Long userId) {
        if (!image.isEmpty()) {
            try {
                userImageRepository.delete(userImageRepository.findFirstByUserId(userId).get());
            } catch (Exception e) {
                log.info(e.toString());
            }
            try {
                UserImage userImage = new UserImage();
                userImage.setUser(userRepository.findUserById(userId));
                userImage.setImageData(image.getBytes());
                userImageRepository.save(userImage);
            } catch (Exception e) {
                log.info(e.toString());
            }
        }
    }


    public byte[] getUserImage(Long userId) {
        Optional<UserImage> userImageOptional = userImageRepository.findFirstByUserId(userId);
        if (userImageOptional.isPresent()) {
            return userImageOptional.get().getImageData();
        } else {
            return null;
        }
    }

    public String getUserImageBase64(Long userId) {
        String base64ImageData = new String();
        byte[] imageData = getUserImage(userId);
        if (imageData != null) {
            base64ImageData = Base64.getEncoder().encodeToString(imageData);
        }
        return base64ImageData;
    }
}
