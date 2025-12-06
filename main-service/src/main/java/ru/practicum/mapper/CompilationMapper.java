package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation mapToCompilation(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public static CompilationDto mapToDto(Compilation compilation) {
        return new CompilationDto(EventMapper.mapToShortDtoList(compilation.getEvents()),
                compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> mapToDtoList(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::mapToDto).toList();
    }
}
