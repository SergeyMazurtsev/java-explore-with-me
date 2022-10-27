package ru.practicum.ewm.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    default Compilation toCompilationFromCompilationDroIn(CompilationDtoIn compilationDtoIn) {
        if (compilationDtoIn == null) {
            return null;
        }
        Compilation.CompilationBuilder compilation = Compilation.builder();
        compilation.pinned(compilationDtoIn.getPinned());
        compilation.title(compilationDtoIn.getTitle());
        return compilation.build();
    }

    default CompilationDtoOut toCompilationDtoOutFromCompilation(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        CompilationDtoOut.CompilationDtoOutBuilder compilationDtoOut = CompilationDtoOut.builder();
        compilationDtoOut.events(compilation.getEvents().stream()
                .map(EventMapper.INSTANCE::toEventDtoOutShortFromEvent)
                .collect(Collectors.toSet()));
        compilationDtoOut.id(compilation.getId());
        compilationDtoOut.pinned(compilation.getPinned());
        compilationDtoOut.title(compilation.getTitle());
        return compilationDtoOut.build();
    }
}
