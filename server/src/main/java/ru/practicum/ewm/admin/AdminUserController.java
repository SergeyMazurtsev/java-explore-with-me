package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Valid @RequestBody UserDto userDto) {
        log.info("Create admin new user = {}", userDto);
        return new ResponseEntity<>(adminService.createUser(userDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(
            @NotNull @RequestParam(name = "ids") List<Long> userIds,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get admin users = {}, from = {}, size = {}", userIds, from, size);
        return new ResponseEntity<>(adminService.getUsers(userIds, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(
            @PathVariable(name = "userId") Long userId) {
        log.info("Delete admin user = {}", userId);
        adminService.deleteUser(userId);
    }
}
