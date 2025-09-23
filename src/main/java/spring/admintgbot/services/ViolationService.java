package spring.admintgbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.admintgbot.entity.ViolatorEntity;
import spring.admintgbot.repository.ViolatorRepository;

@Service
@RequiredArgsConstructor
public class ViolationService {
    private final ViolatorRepository repository;

    @Transactional
    public int recordViolation(ViolatorEntity user) {
        return repository.upsertAndReturnCounter(
                user.getUserId(),
                user.getUserName(),
                user.getFirstName()
        );
    }
}
