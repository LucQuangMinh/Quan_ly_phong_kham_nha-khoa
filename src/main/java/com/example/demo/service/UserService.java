package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        String input = request.getUsername();
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(input, input);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(request.getPassword())) {
                if ("Hoạt động".equalsIgnoreCase(user.getStatus())) {
                    return new LoginResponse(true, "Đăng nhập thành công", user.getFullname(), user.getRole(), user.getId());
                } else {
                    return new LoginResponse(false, "Tài khoản đang bị khóa", null, null, null);
                }
            }
        }
        return new LoginResponse(false, "Sai tài khoản hoặc mật khẩu", null, null, null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUnassignedDoctors() {
        return userRepository.findUsersByRoleAndNotAssignedToDoctor("Bác sĩ");
    }

    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập không được để trống!");
        }
        if (userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu phải có tối thiểu 8 ký tự!");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn vai trò!");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Email không đúng định dạng!");
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !user.getPhone().matches("^\\\\d{10,11}$")) {
            throw new RuntimeException("Số điện thoại không đúng định dạng!");
        }
        if (user.getStatus() == null) user.setStatus("Hoạt động");
        return userRepository.save(user);
    }

    public User updateUser(Long id, User details) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        if (details.getRole() == null || details.getRole().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn vai trò!");
        }
        if (details.getEmail() != null && !details.getEmail().isEmpty() && !details.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Email không đúng định dạng!");
        }
        if (details.getPhone() != null && !details.getPhone().isEmpty() && !details.getPhone().matches("^\\\\d{10,11}$")) {
            throw new RuntimeException("Số điện thoại không đúng định dạng!");
        }
        user.setFullname(details.getFullname());
        user.setEmail(details.getEmail());
        user.setPhone(details.getPhone());
        user.setRole(details.getRole());
        return userRepository.save(user);
    }

    public User changeStatus(Long id, String newStatus) {
        if (id == 1L && "Bị khóa".equalsIgnoreCase(newStatus)) {
            throw new RuntimeException("Không được phép khóa Quản trị viên hệ thống!");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    public User resetPassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Mật khẩu phải có tối thiểu 8 ký tự!");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
