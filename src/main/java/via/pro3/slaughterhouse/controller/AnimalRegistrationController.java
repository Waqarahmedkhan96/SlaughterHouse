package via.pro3.slaughterhouse.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.rest.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.service.AnimalRegistrationService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/animals")
public class AnimalRegistrationController {

    private final AnimalRegistrationService animalService;

    public AnimalRegistrationController(AnimalRegistrationService animalService) {
        this.animalService = animalService;
    }

    // ---------- CREATE ----------
    @PostMapping
    public ResponseEntity<?> createAnimal( // generic body
                                           @RequestBody AnimalRegistrationDtos.CreateAnimalDto dto) {

        AnimalRegistrationDtos.AnimalDto created = animalService.createAnimal(dto);

        if (created == null) {
            // queued (db down)
            Map<String, Object> body = Map.of(
                    "status", "QUEUED",                                // queued flag
                    "message", "Database unavailable. Animal stored in queue.",
                    "registrationNumber", dto.getRegistrationNumber()  // echo reg
            );
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED) // 202
                    .body(body);
        }

        // saved in db
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(created);
    }

    // ---------- READ ONE ----------
    @GetMapping("/{registrationNumber}")
    public ResponseEntity<AnimalRegistrationDtos.AnimalDto> getAnimalByRegistrationNumber(
            @PathVariable String registrationNumber) {

        AnimalRegistrationDtos.AnimalDto found = animalService.getAnimalByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(found);
    }

    // ---------- READ LIST BY DATE ----------
    @GetMapping("/date/{arrivalDate}")
    public ResponseEntity<AnimalRegistrationDtos.AnimalListDto> getAnimalsByArrivalDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate arrivalDate) {

        return ResponseEntity.ok(animalService.getAnimalsByArrivalDate(arrivalDate));
    }

    // ---------- READ LIST BY ORIGIN ----------
    @GetMapping("/origin/{origin}")
    public ResponseEntity<AnimalRegistrationDtos.AnimalListDto> getAnimalsByOrigin(
            @PathVariable String origin) {

        return ResponseEntity.ok(animalService.getAnimalsByOrigin(origin));
    }

    // ---------- UPDATE ----------
    @PutMapping("/{registrationNumber}")
    public ResponseEntity<AnimalRegistrationDtos.AnimalDto> updateAnimal(
            @PathVariable String registrationNumber,
            @RequestBody AnimalRegistrationDtos.UpdateAnimalDto dto) {

        AnimalRegistrationDtos.AnimalDto updated = animalService.updateAnimal(registrationNumber, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    @DeleteMapping("/{registrationNumber}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String registrationNumber) {

        animalService.deleteAnimal(registrationNumber);
        return ResponseEntity.noContent().build(); // 204
    }

}
