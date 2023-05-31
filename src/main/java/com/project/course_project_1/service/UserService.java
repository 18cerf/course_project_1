package com.project.course_project_1.service;

import com.project.course_project_1.entity.DateTime;
import com.project.course_project_1.entity.User;
import com.project.course_project_1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveEditedUser(User userOldInfo, User editedUser) {
        userOldInfo.setUsername(editedUser.getUsername());
        userOldInfo.setPassword(passwordEncoder.encode(editedUser.getPassword()));
        userOldInfo.setName(editedUser.getName());
        userOldInfo.setLastname(editedUser.getLastname());
        userOldInfo.setPhoneNumber(editedUser.getPhoneNumber());
        userOldInfo.setEmail(editedUser.getEmail());

        userRepository.save(userOldInfo);
    }

    public Boolean existsUserByUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }


    public void saveUserLoginTimes(Long userId) {
        DateTime dateTime = new DateTime();
        dateTime.setTimestamp();
        User user = userRepository.findUserById(userId);
        user.getLoginTimes().add(dateTime);
        userRepository.save(user);
    }

    public Set<String> getUserLoginTimes(Long userId) {
        User user = userRepository.findUserById(userId);

        Set<String> loginTimes = new TreeSet<String>(Comparator.reverseOrder());
        for (DateTime time : user.getLoginTimes()) {
            loginTimes.add(time.getTimestamp().toString());
        }

        return loginTimes;
    }


    public List<User> findUsersByIdNotIn(List<Long> friendsIds) {
        return userRepository.findUsersByIdNotIn(friendsIds);
    }

    public List<Long> getUsersFriendsIds(Long userId) {
        List<Long> friendsIds = new ArrayList<Long>();
        for (User friends :
                findUserById(userId).getFriends()) {
            friendsIds.add(friends.getId());
        }
        friendsIds.add(userId);
        return friendsIds;
    }

    public void addFriend(Long userId, Long friendId) {

        User newFriend = userRepository.findUserById(friendId);
        User user = userRepository.findUserById(userId);

        if (!(userRepository.findUserById(user.getId()).getFriends().contains(newFriend) || user.getId() == friendId || newFriend.getFriends().contains(user))) {
            try {
                user = userRepository.findUserById(user.getId());
                user.getFriends().add(newFriend);
                userRepository.save(user);

                newFriend.getFriends().add(user);
                userRepository.save(newFriend);
            } catch (Exception e) {
                log.info(e.toString());
            }
        }
    }
    public Set<User> findByParam(String searchParam) {
        Set<User> users = new HashSet();
        users.addAll(findByUsernameContaining(searchParam));
        users.addAll(findByLastnameContaining(searchParam));
        users.addAll(findByNameContaining(searchParam));
        users.addAll(findByPhoneNumberContaining(searchParam));

        return users;
    }

    public Collection<? extends User> findByUsernameContaining(String searchParam) {
        return userRepository.findByUsernameContaining(searchParam);
    }

    public Collection<? extends User> findByLastnameContaining(String searchParam) {
        return userRepository.findByLastnameContaining(searchParam);
    }

    public Collection<? extends User> findByNameContaining(String searchParam) {
        return userRepository.findByNameContaining(searchParam);
    }

    public Collection<? extends User> findByPhoneNumberContaining(String searchParam) {
        return userRepository.findByPhoneNumberContaining(searchParam);
    }
}
