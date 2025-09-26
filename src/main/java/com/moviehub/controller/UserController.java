package com.moviehub.controller;

import com.moviehub.dto.UserDto;
import com.moviehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "ID로 특정 사용자 정보를 조회합니다.")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 사용자 정보를 조회합니다.")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "사용자명으로 사용자 조회", description = "사용자명으로 사용자 정보를 조회합니다.")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/profile")
    @Operation(summary = "사용자 프로필 조회", description = "사용자의 상세 프로필 정보를 조회합니다.")
    public ResponseEntity<UserDto.ProfileResponse> getUserProfile(@PathVariable UUID id) {
        try {
            UserDto.ProfileResponse profile = userService.getUserProfile(id);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/reviews")
    @Operation(summary = "사용자 리뷰한 영화 조회", description = "특정 영화에 리뷰를 작성한 사용자들을 조회합니다.")
    public ResponseEntity<List<UserDto>> getUsersWhoReviewedMovie(@PathVariable UUID id) {
        List<UserDto> users = userService.getUsersWhoReviewedMovie(id);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}/favorites")
    @Operation(summary = "사용자 즐겨찾기한 영화 조회", description = "특정 영화를 즐겨찾기한 사용자들을 조회합니다.")
    public ResponseEntity<List<UserDto>> getUsersWhoFavoritedMovie(@PathVariable UUID id) {
        List<UserDto> users = userService.getUsersWhoFavoritedMovie(id);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/register")
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto.RegisterRequest request) {
        try {
            UserDto user = userService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        
        return userService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/password")
    @Operation(summary = "비밀번호 변경", description = "사용자 비밀번호를 변경합니다.")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UserDto.ChangePasswordRequest request) {
        
        boolean changed = userService.changePassword(id, request);
        return changed ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
