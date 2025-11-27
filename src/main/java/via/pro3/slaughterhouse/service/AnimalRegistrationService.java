// 1. Station 1, the animals arrive, are weighed and registered.
package via.pro3.slaughterhouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;
import via.pro3.slaughterhouse.repository.AnimalRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AnimalRegistrationService {

    private final AnimalRepository animalRepository;

    public AnimalRegistrationService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    // ---------- CREATE/ Register Animal ----------
    public AnimalRegistrationDtos.AnimalDto createAnimal(AnimalRegistrationDtos.CreateAnimalDto dto) {

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
    }

    // ---------- READ ONE ----------
    public AnimalRegistrationDtos.AnimalDto getAnimalByRegistrationNumber(String regNumber) {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalRegistrationExceptions.AnimalNotFoundException(regNumber));

        return toDto(animal);
    }

    // ---------- READ BY DATE ----------
    public AnimalRegistrationDtos.AnimalListDto getAnimalsByArrivalDate(LocalDate date) {
        return toListDto(animalRepository.findAllByArrivalDate(date));
    }

    // ---------- READ BY ORIGIN ----------
    public AnimalRegistrationDtos.AnimalListDto getAnimalsByOrigin(String origin) {
        return toListDto(animalRepository.findAllByOriginIgnoreCase(origin));
    }

    // ---------- UPDATE ----------
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
    public void deleteAnimal(String regNumber) {

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
