package com.project.course_project_1.controller;

import com.project.course_project_1.entity.User;
import com.project.course_project_1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/*
 * Контроллер, предназначенный для регистрации новых пользователей
 */
@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {


    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public RegistrationController(

            PasswordEncoder passwordEncoder,
            UserService userService) {
        this.passwordEncoder = passwordEncoder;

        this.userService = userService;
    }


    @ModelAttribute(name = "registerForm")
    public User registrationForm() {
        return new User();
    }


    @GetMapping
    public String registerForm() {
        return "registration";
    }


    @PostMapping
    public String processRegistration(@ModelAttribute("registerForm") @Valid User newUser,
                                      Errors errors,
                                      Model model,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Ошибка валидации данных, проверьте правильность вводимых полей");
            return "registration";
        }

        if (!userService.existsUserByUsername(newUser.getUsername())) {
            userService.saveNewUser(newUser);
        } else {
            model.addAttribute("error_message", "Пользователь с таким логином уже существует");
            return "registration";
        }

        return "redirect:/login";

    }
}
