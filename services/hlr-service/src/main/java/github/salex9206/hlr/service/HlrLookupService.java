package github.salex9206.hlr.service;

import github.salex9206.hlr.entity.HlrSubscriber;
import github.salex9206.hlr.api.dto.LookupRequest;
import github.salex9206.hlr.api.dto.LookupResponse;
import github.salex9206.hlr.mapper.HlrMapper;
import github.salex9206.hlr.repository.HlrSubscriberRepository;
import github.salex9206.hlr.simulation.HlrProfileGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HlrLookupService {
    private final HlrSubscriberRepository repository;
    private final HlrProfileGenerator generator;
    // private final HlrStateSimulation simulator;
    private final HlrMapper mapper;

    @Transactional
    public LookupResponse lookup(LookupRequest request) {
        final String e164Number = request.e164();
        final boolean forceRefresh = request.forceRefresh();

        // 1. Найдем или сгенерируем объект
        HlrSubscriber subscriber = repository.findById(e164Number)
                .orElseGet(() -> {
                    HlrSubscriber generated = generator.generate(e164Number);
                    return repository.save(generated);
                });

        // 2. Выполняем симуляцию изменений
        boolean changed = false;
        if (!forceRefresh)
            changed = false; //simulator.applyChanges(subscriber);
        subscriber.setSimulatedAt(Instant.now());
        repository.save(subscriber);

        return mapper.toLookupResponse(subscriber, changed);
    }
}
