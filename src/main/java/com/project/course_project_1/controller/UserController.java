package com.project.course_project_1.controller;

import com.project.course_project_1.entity.User;
import com.project.course_project_1.repository.DateRepository;
import com.project.course_project_1.repository.UserImageRepository;
import com.project.course_project_1.service.UserImageService;
import com.project.course_project_1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final DateRepository dateRepository;
    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;

    public UserController(UserService userService, DateRepository dateRepository, UserImageService userImageService, UserImageRepository userImageRepository) {
        this.userService = userService;
        this.dateRepository = dateRepository;
        this.userImageService = userImageService;
        this.userImageRepository = userImageRepository;
    }

    @GetMapping("/main")
    public String showMainUser(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", userService.findUserById(user.getId()));
        model.addAttribute("user_friends", userService.findUserById(user.getId()).getFriends());
        model.addAttribute("login_times", userService.getUserLoginTimes(user.getId()));
        model.addAttribute("userImage", userImageService.getUserImageBase64(user.getId()));
        userService.saveUserLoginTimes(user.getId());
        return "main";
    }

    @PostMapping("/image")
    public String uploadUserImage(@RequestParam("file") MultipartFile image,
                                  @AuthenticationPrincipal User user) {
        userImageService.saveUserImage(image, user.getId());
        return "redirect:/users/main";
    }


    @GetMapping("/main/delete/logs")
    public String deleteUsersLogs(@AuthenticationPrincipal User user) {
        user.getLoginTimes().clear();
        userService.saveUser(user);
        return "redirect:/users/main";
    }


    @GetMapping("/main/edit")
    public String editMainUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "edit-main-user";
    }


    @PostMapping("/main/edit")
    public String saveMainEdit(@Valid User editedUser, BindingResult bindingResult,
                               @AuthenticationPrincipal User user, Model model) {
        if (userService.existsUserByUsername(editedUser.getUsername())) {
            if (!editedUser.getUsername().equals(user.getUsername())) {
                model.addAttribute("error_message", "Пользователь с таким логином уже существует");
                return "edit-main-user";
            }
        }
        if (bindingResult.hasErrors()) {
            return "edit-main-user";
        } else {
            userService.saveEditedUser(user, editedUser);
            return "redirect:/users/main";
        }
    }


    @GetMapping("/")
    public String allUsers(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("users", userService.findUsersByIdNotIn(userService.getUsersFriendsIds(user.getId())));
        return "all-users";
    }

    @PostMapping("/{id}")
    public String addFriend(@AuthenticationPrincipal User user, @PathVariable("id") Long newFriendId) {
        userService.addFriend(user.getId(), newFriendId);
        return "redirect:";
    }


    @GetMapping("/search")
    public String getUsersByParam(@RequestParam("searchParam") String searchParam, Model model) {
        Set<User> users = userService.findByParam(searchParam);
        if (users.isEmpty()) {
            model.addAttribute("message", "Нет пользователей удовлетворяющих запросу");
        }
        model.addAttribute("users", users);
        return "all-users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        model.addAttribute("user_friends", userService.findUserById(id).getFriends());
        model.addAttribute("login_times", userService.getUserLoginTimes(id));
        model.addAttribute("userImage", userImageService.getUserImageBase64(id));
        return "user";
    }
}
