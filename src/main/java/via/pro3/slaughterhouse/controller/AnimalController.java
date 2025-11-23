package via.pro3.slaughterhouse.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.AnimalDtos;
import via.pro3.slaughterhouse.service.AnimalService;

import java.time.LocalDate;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    // use ResponseEntity to return correct HTTP status codes (201, 404, 204)
    // ---------- CREATE ----------
    @PostMapping
    public ResponseEntity<AnimalDtos.AnimalDto> createAnimal(
            @RequestBody AnimalDtos.CreateAnimalDto dto) {

        AnimalDtos.AnimalDto created = animalService.createAnimal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- READ ONE ----------
    @GetMapping("/{registrationNumber}")
    public ResponseEntity<AnimalDtos.AnimalDto> getAnimalByRegistrationNumber(
            @PathVariable String registrationNumber) {

        AnimalDtos.AnimalDto found = animalService.getAnimalByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(found);
    }

    // ---------- READ LIST BY DATE ----------
    @GetMapping("/date/{arrivalDate}")
    public ResponseEntity<AnimalDtos.AnimalListDto> getAnimalsByArrivalDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate arrivalDate) {

        return ResponseEntity.ok(animalService.getAnimalsByArrivalDate(arrivalDate));
    }

    // ---------- READ LIST BY ORIGIN ----------
    @GetMapping("/origin/{origin}")
    public ResponseEntity<AnimalDtos.AnimalListDto> getAnimalsByOrigin(
            @PathVariable String origin) {

        return ResponseEntity.ok(animalService.getAnimalsByOrigin(origin));
    }

    // ---------- UPDATE ----------
    @PutMapping("/{registrationNumber}")
    public ResponseEntity<AnimalDtos.AnimalDto> updateAnimal(
            @PathVariable String registrationNumber,
            @RequestBody AnimalDtos.UpdateAnimalDto dto) {

        AnimalDtos.AnimalDto updated = animalService.updateAnimal(registrationNumber, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    @DeleteMapping("/{registrationNumber}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String registrationNumber) {

        animalService.deleteAnimal(registrationNumber);
        return ResponseEntity.noContent().build(); // 204
    }
}
