package ru.practicum.ewm.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
