package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> getCategories(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get public categories, from = {}, size = {}", from, size);
        return new ResponseEntity<>(categoryService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategory(@PathVariable Long catId) {
        log.info("Get public category = {}", catId);
        return new ResponseEntity<>(categoryService.getCategory(catId), HttpStatus.OK);
    }
}
