package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> createCompilation(@Valid @RequestBody CompilationDtoIn compilationDtoIn) {
        log.info("Create admin compilation = {}", compilationDtoIn);
        return new ResponseEntity<>(adminService.createCompilation(compilationDtoIn), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Delete admin compilation = {}", compId);
        adminService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteEventFromCompilation(
            @PathVariable(name = "compId") Long compId,
            @PathVariable(name = "eventId") Long eventId) {
        log.info("Delete admin event = {} from compilation = {}", eventId, compId);
        adminService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    void addEventToCompilation(
            @PathVariable(name = "compId") Long compId,
            @PathVariable(name = "eventId") Long eventId) {
        log.info("Add admin event = {} to compilation = {}", eventId, compId);
        adminService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    void unpinCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Unpin admin compilation = {}", compId);
        adminService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    void pinCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Pin admin compilation = {}", compId);
        adminService.pinCompilation(compId);
    }
}
