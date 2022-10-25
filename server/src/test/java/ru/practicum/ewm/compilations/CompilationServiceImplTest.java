package ru.practicum.ewm.compilations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.model.Event;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompilationServiceImplTest {
    private final CompilationRepository compilationRepository = Mockito.mock(CompilationRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final CompilationService compilationService = new CompilationServiceImpl(compilationRepository, commonService);
    private Compilation compilation;
    private CompilationDtoOut compilationDtoOut;
    private Long eventId;

    @BeforeEach
    void setUp() {
        eventId = 1L;
        compilation = Compilation.builder()
                .id(1L)
                .pinned(true)
                .title("Testing compilation")
                .events(new HashSet<>(Arrays.asList(Event.builder()
                        .id(eventId).build())))
                .build();
        compilationDtoOut = CompilationDtoOut.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(new HashSet<>(Arrays.asList(eventId)))
                .build();
    }

    @Test
    void getCompilations() {
        List<Compilation> compilations = new ArrayList<>();
        compilations.add(compilation);
        Pageable pageable = PageRequest.of(0, 10);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(compilationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(compilations));
        List<CompilationDtoOut> testComp = compilationService.getCompilations(true, 0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testComp.size(), equalTo(1));
        assertThat(testComp.get(0), equalTo(compilationDtoOut));

        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(compilationRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    void getCompilation() {
        when(commonService.getCompilationInDb(anyLong()))
                .thenReturn(compilation);
        CompilationDtoOut testComp = compilationService.getCompilation(compilation.getId());
        assertThat(testComp, equalTo(compilationDtoOut));

        verify(commonService, times(1))
                .getCompilationInDb(anyLong());
    }
}
