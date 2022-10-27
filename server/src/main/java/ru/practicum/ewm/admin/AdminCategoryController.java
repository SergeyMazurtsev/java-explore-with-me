package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoryController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Create admin new category = {}", categoryDto);
        return new ResponseEntity<>(adminService.createCategory(categoryDto), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<Object> patchCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Patching admin category = {}", categoryDto);
        return new ResponseEntity<>(adminService.patchCategory(categoryDto), HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(
            @PathVariable(name = "catId") Long catId) {
        log.info("Delete admin category = {}", catId);
        adminService.deleteCategory(catId);
    }
}
