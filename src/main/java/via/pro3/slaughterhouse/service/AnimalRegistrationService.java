// 1. Station 1, the animals arrive, are weighed and registered.
package via.pro3.slaughterhouse.service;

import org.springframework.dao.DataAccessException; // DB errors
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.rest.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;
import via.pro3.slaughterhouse.rabbitmq.publisher.AnimalRegistrationMessagePublisher;
import via.pro3.slaughterhouse.repository.AnimalRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnimalRegistrationService {

    private final AnimalRepository animalRepository;
    private final AnimalRegistrationMessagePublisher messagePublisher; // rabbit mq injection

    public AnimalRegistrationService(AnimalRepository animalRepository,
                                     AnimalRegistrationMessagePublisher messagePublisher) {
        this.animalRepository = animalRepository;
        this.messagePublisher = messagePublisher;
    }

    // ---------- CREATE/ Register Animal ----------
    // NO @Transactional here (offline fallback)
    public AnimalRegistrationDtos.AnimalDto createAnimal(AnimalRegistrationDtos.CreateAnimalDto dto) {

        try {
            animalRepository.findByRegistrationNumber(dto.getRegistrationNumber())
                    .ifPresent(a -> {
                        throw new AnimalRegistrationExceptions.AnimalAlreadyExistsException(
                                dto.getRegistrationNumber());
                    });

            Animal animal = new Animal();
            animal.setRegistrationNumber(dto.getRegistrationNumber());
            animal.setWeight(dto.getWeight());
            animal.setArrivalDate(dto.getArrivalDate());
            animal.setOrigin(dto.getOrigin());

            return toDto(animalRepository.save(animal));
        } catch (DataAccessException ex) {
            // DB offline
            messagePublisher.sendAnimalRegistration(dto); // queue fallback
            AnimalRegistrationDtos.AnimalDto buffered = new AnimalRegistrationDtos.AnimalDto();
            buffered.setId(null); // id pending
            buffered.setRegistrationNumber(dto.getRegistrationNumber());
            buffered.setWeight(dto.getWeight());
            buffered.setArrivalDate(dto.getArrivalDate());
            buffered.setOrigin(dto.getOrigin());
            return buffered; // immediate offline response
        }
    }

    // ---------- READ ONE ----------
    @Transactional(readOnly = true)
    public AnimalRegistrationDtos.AnimalDto getAnimalByRegistrationNumber(String regNumber) {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalRegistrationExceptions.AnimalNotFoundException(regNumber));

        return toDto(animal);
    }

    // ---------- READ BY DATE ----------
    @Transactional(readOnly = true)
    public AnimalRegistrationDtos.AnimalListDto getAnimalsByArrivalDate(LocalDate date) {
        return toListDto(animalRepository.findAllByArrivalDate(date));
    }

    // ---------- READ BY ORIGIN ----------
    @Transactional(readOnly = true)
    public AnimalRegistrationDtos.AnimalListDto getAnimalsByOrigin(String origin) {
        return toListDto(animalRepository.findAllByOriginIgnoreCase(origin));
    }

    // ---------- UPDATE ----------
    @Transactional // write transaction
    public AnimalRegistrationDtos.AnimalDto updateAnimal(
            String regNumber,
            AnimalRegistrationDtos.UpdateAnimalDto dto) {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalRegistrationExceptions.AnimalNotFoundException(regNumber));

        if (dto.getWeight() != null)
            animal.setWeight(dto.getWeight());

        if (dto.getArrivalDate() != null)
            animal.setArrivalDate(dto.getArrivalDate());

        if (dto.getOrigin() != null)
            animal.setOrigin(dto.getOrigin());

        return toDto(animalRepository.save(animal));
    }

    // ---------- DELETE ----------
    @Transactional // write transaction
    public void deleteAnimal(String regNumber)
    {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalRegistrationExceptions.AnimalNotFoundException(regNumber));

        animalRepository.delete(animal);
    }

    // ---------- HELPERS ----------
    private AnimalRegistrationDtos.AnimalDto toDto(Animal entity) {
        AnimalRegistrationDtos.AnimalDto dto = new AnimalRegistrationDtos.AnimalDto();
        dto.setId(entity.getId());
        dto.setRegistrationNumber(entity.getRegistrationNumber());
        dto.setWeight(entity.getWeight());
        dto.setArrivalDate(entity.getArrivalDate());
        dto.setOrigin(entity.getOrigin());
        return dto;
    }

    private AnimalRegistrationDtos.AnimalListDto toListDto(List<Animal> list) {
        AnimalRegistrationDtos.AnimalListDto dto = new AnimalRegistrationDtos.AnimalListDto();
        dto.setAnimals(list.stream().map(this::toDto).toList());
        return dto;
    }
}
