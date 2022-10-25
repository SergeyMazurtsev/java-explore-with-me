package ru.practicum.ewm.compilations;

import ru.practicum.ewm.compilations.dto.CompilationDtoOut;

import java.util.Collection;

public interface CompilationService {
    Collection<CompilationDtoOut> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDtoOut getCompilation(Long compId);
}
