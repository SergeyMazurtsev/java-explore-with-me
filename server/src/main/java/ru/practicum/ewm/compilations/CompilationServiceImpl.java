package ru.practicum.ewm.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CommonService commonService;

    @Override
    public Collection<CompilationDtoOut> getCompilations(Boolean pinned, Integer from, Integer size) {
        Collection<Compilation> compilations = compilationRepository.findAll(
                        commonService.getPagination(from, size, null)).stream()
                .collect(Collectors.toList());
        if (pinned != null) {
            compilations = compilations.stream()
                    .filter(i -> i.getPinned().equals(pinned)).collect(Collectors.toList());
        }
        return compilations.stream().map(CompilationMapper.INSTANCE::toCompilationDtoOutFromCompilation)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDtoOut getCompilation(Long compId) {
        return CompilationMapper.INSTANCE
                .toCompilationDtoOutFromCompilation(commonService.getCompilationInDb(compId));
    }
}
