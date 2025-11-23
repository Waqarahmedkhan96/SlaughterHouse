package via.pro3.slaughterhouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.AnimalDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.exception.AnimalExceptions;
import via.pro3.slaughterhouse.repository.AnimalRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    // ---------- CREATE ----------
    public AnimalDtos.AnimalDto createAnimal(AnimalDtos.CreateAnimalDto dto) {

        animalRepository.findByRegistrationNumber(dto.getRegistrationNumber())
                .ifPresent(a -> {
                    throw new AnimalExceptions.AnimalAlreadyExistsException(
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
    public AnimalDtos.AnimalDto getAnimalByRegistrationNumber(String regNumber) {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalExceptions.AnimalNotFoundException(regNumber));

        return toDto(animal);
    }

    // ---------- READ BY DATE ----------
    public AnimalDtos.AnimalListDto getAnimalsByArrivalDate(LocalDate date) {
        return toListDto(animalRepository.findAllByArrivalDate(date));
    }

    // ---------- READ BY ORIGIN ----------
    public AnimalDtos.AnimalListDto getAnimalsByOrigin(String origin) {
        return toListDto(animalRepository.findAllByOriginIgnoreCase(origin));
    }

    // ---------- UPDATE ----------
    public AnimalDtos.AnimalDto updateAnimal(
            String regNumber,
            AnimalDtos.UpdateAnimalDto dto) {

        Animal animal = animalRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new AnimalExceptions.AnimalNotFoundException(regNumber));

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
                .orElseThrow(() -> new AnimalExceptions.AnimalNotFoundException(regNumber));

        animalRepository.delete(animal);
    }

    // ---------- HELPERS ----------
    private AnimalDtos.AnimalDto toDto(Animal entity) {
        AnimalDtos.AnimalDto dto = new AnimalDtos.AnimalDto();
        dto.setId(entity.getId());
        dto.setRegistrationNumber(entity.getRegistrationNumber());
        dto.setWeight(entity.getWeight());
        dto.setArrivalDate(entity.getArrivalDate());
        dto.setOrigin(entity.getOrigin());
        return dto;
    }

    private AnimalDtos.AnimalListDto toListDto(List<Animal> list) {
        AnimalDtos.AnimalListDto dto = new AnimalDtos.AnimalListDto();
        dto.setAnimals(list.stream().map(this::toDto).toList());
        return dto;
    }
}
