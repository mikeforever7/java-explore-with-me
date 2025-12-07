package ru.practicum.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        if (compilationRepository.findByTitle(newCompilationDto.getTitle()).isPresent()) {
            throw new AlreadyExistsException("Подборка с Title " + newCompilationDto.getTitle() + " уже существует");
        }
        List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto, events);
        return CompilationMapper.mapToDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборки с id=" + compId + " не существует");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compForUpdate = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id=" + compId + " не существует"));
        if (updateRequest.getTitle() != null) {
            if (updateRequest.getTitle().length() > 50) {
                throw new ValidationException("Заголовок должен быть до 50 символов");
            }
            if (!updateRequest.getTitle().equals(compForUpdate.getTitle())
                    && compilationRepository.findByTitle(updateRequest.getTitle()).isPresent()) {
                throw new AlreadyExistsException("Подборка с Title " + updateRequest.getTitle() + " уже существует");
            }
            compForUpdate.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getPinned() != null) {
            compForUpdate.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getEvents() != null) {
            List<Event> newEvents = eventRepository.findByIdIn(updateRequest.getEvents());
            compForUpdate.setEvents(new ArrayList<>(newEvents));
        }
        return CompilationMapper.mapToDto(compilationRepository.save(compForUpdate));
    }

    @Override
    public CompilationDto findCompById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id=" + compId + " не найдено"));
        return CompilationMapper.mapToDto(compilation);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable).getContent();
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        return CompilationMapper.mapToDtoList(compilations);
    }
}
