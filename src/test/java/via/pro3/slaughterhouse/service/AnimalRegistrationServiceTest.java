package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.dto.rest.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnimalRegistrationServiceTest {

    @Autowired
    private AnimalRegistrationService animalService; // real service

    // ---------- CREATE ----------

    @Test
    void createAnimal_success() {
        String reg = "T-UNIT-1";                  // test reg no

        try {
            animalService.deleteAnimal(reg);      // cleanup old
        } catch (AnimalRegistrationExceptions.AnimalNotFoundException ignored) {
            // ignore if missing                     // ignore missing
        }

        AnimalRegistrationDtos.CreateAnimalDto dto =
                new AnimalRegistrationDtos.CreateAnimalDto(); // create DTO
        dto.setRegistrationNumber(reg);            // set reg
        dto.setWeight(400.5);                      // set weight
        dto.setArrivalDate(LocalDate.now());      // set date
        dto.setOrigin("TestFarm");                // set origin

        AnimalRegistrationDtos.AnimalDto created =
                animalService.createAnimal(dto);  // call service

        assertEquals(reg, created.getRegistrationNumber()); // same reg
        assertEquals("TestFarm", created.getOrigin());      // same origin
        assertEquals(400.5, created.getWeight());           // same weight
    }

    @Test
    void createAnimal_duplicate_throwsException() {
        String reg = "T-UNIT-2";                  // test reg no

        // ensure one exists
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (Exception e) {                               // if missing
            AnimalRegistrationDtos.CreateAnimalDto dto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // new DTO
            dto.setRegistrationNumber(reg);                 // reg
            dto.setWeight(500.0);                           // weight
            dto.setArrivalDate(LocalDate.now());            // date
            dto.setOrigin("Farm X");                        // origin
            animalService.createAnimal(dto);                // create once
        }

        AnimalRegistrationDtos.CreateAnimalDto duplicate =
                new AnimalRegistrationDtos.CreateAnimalDto();   // duplicate dto
        duplicate.setRegistrationNumber(reg);                 // same reg
        duplicate.setWeight(900.0);                           // diff weight
        duplicate.setArrivalDate(LocalDate.now());            // date
        duplicate.setOrigin("Farm Y");                        // origin

        assertThrows(
                AnimalRegistrationExceptions.AnimalAlreadyExistsException.class, // expected
                () -> animalService.createAnimal(duplicate)                      // call again
        );
    }

    // ---------- READ ONE ----------

    @Test
    void getAnimalByRegistrationNumber_found() {
        String reg = "T-READ-1";                // test reg

        // ensure exists
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (Exception e) {                               // if missing
            AnimalRegistrationDtos.CreateAnimalDto dto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // build dto
            dto.setRegistrationNumber(reg);                 // reg
            dto.setWeight(333.3);                           // weight
            dto.setArrivalDate(LocalDate.now());            // date
            dto.setOrigin("ReadFarm");                      // origin
            animalService.createAnimal(dto);                // create
        }

        AnimalRegistrationDtos.AnimalDto dto =
                animalService.getAnimalByRegistrationNumber(reg); // call read

        assertEquals(reg, dto.getRegistrationNumber());  // correct reg
        assertNotNull(dto.getWeight());                  // weight present
    }

    @Test
    void getAnimalByRegistrationNumber_notFound() {
        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class, // expected
                () -> animalService.getAnimalByRegistrationNumber("DOES-NOT-EXIST") // invalid reg
        );
    }

    // ---------- LIST BY DATE ----------

    @Test
    void getAnimalsByArrivalDate_listReturned() {
        LocalDate today = LocalDate.now();              // today date

        // create one for today
        String reg = "T-DATE-1";                        // test reg
        AnimalRegistrationDtos.CreateAnimalDto dto =
                new AnimalRegistrationDtos.CreateAnimalDto(); // create dto
        dto.setRegistrationNumber(reg);                 // reg
        dto.setWeight(200.0);                           // weight
        dto.setArrivalDate(today);                      // today
        dto.setOrigin("DateFarm");                      // origin
        try {
            animalService.createAnimal(dto);            // try create
        } catch (AnimalRegistrationExceptions.AnimalAlreadyExistsException ignored) {
            // ignore duplicates                          // ignore
        }

        AnimalRegistrationDtos.AnimalListDto list =
                animalService.getAnimalsByArrivalDate(today); // query

        assertNotNull(list);                             // list not null
        assertNotNull(list.getAnimals());                // animals not null
        assertTrue(list.getAnimals().size() >= 1);       // at least one
    }

    // ---------- LIST BY ORIGIN ----------

    @Test
    void getAnimalsByOrigin_listReturned() {
        String origin = "Farm A";                       // test origin

        // create sample
        String reg = "T-ORIGIN-1";                      // test reg
        AnimalRegistrationDtos.CreateAnimalDto dto =
                new AnimalRegistrationDtos.CreateAnimalDto(); // dto
        dto.setRegistrationNumber(reg);                 // reg
        dto.setWeight(250.0);                           // weight
        dto.setArrivalDate(LocalDate.now());            // date
        dto.setOrigin(origin);                          // origin
        try {
            animalService.createAnimal(dto);            // create
        } catch (AnimalRegistrationExceptions.AnimalAlreadyExistsException ignored) {
            // ignore duplicate                           // ignore
        }

        AnimalRegistrationDtos.AnimalListDto list =
                animalService.getAnimalsByOrigin(origin); // query

        assertNotNull(list.getAnimals());               // not null
        assertTrue(list.getAnimals().size() >= 1);      // at least one
    }

    // ---------- UPDATE ----------

    @Test
    void updateAnimal_success() {
        String reg = "T-UPDATE-1";                     // test reg

        // ensure exists
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (Exception e) {                               // if missing
            AnimalRegistrationDtos.CreateAnimalDto dto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // dto
            dto.setRegistrationNumber(reg);                 // reg
            dto.setWeight(300.0);                           // weight
            dto.setArrivalDate(LocalDate.now());            // date
            dto.setOrigin("OldFarm");                       // origin
            animalService.createAnimal(dto);                // create
        }

        AnimalRegistrationDtos.UpdateAnimalDto updateDto =
                new AnimalRegistrationDtos.UpdateAnimalDto(); // update dto
        updateDto.setWeight(777.7);                         // new weight
        updateDto.setOrigin("NewFarm");                     // new origin

        AnimalRegistrationDtos.AnimalDto updated =
                animalService.updateAnimal(reg, updateDto); // call update

        assertEquals(777.7, updated.getWeight());           // updated weight
        assertEquals("NewFarm", updated.getOrigin());       // updated origin
    }

    @Test
    void updateAnimal_notFound() {
        AnimalRegistrationDtos.UpdateAnimalDto update =
                new AnimalRegistrationDtos.UpdateAnimalDto(); // dto
        update.setWeight(999.9);                           // any weight

        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class, // expected
                () -> animalService.updateAnimal("NO-ANIMAL", update)      // invalid reg
        );
    }

    // ---------- DELETE ----------

    @Test
    void deleteAnimal_success() {
        String reg = "T-DELETE-1";                     // test reg

        // ensure exists
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (Exception e) {                               // if missing
            AnimalRegistrationDtos.CreateAnimalDto dto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // dto
            dto.setRegistrationNumber(reg);                 // reg
            dto.setWeight(300.0);                           // weight
            dto.setArrivalDate(LocalDate.now());            // date
            dto.setOrigin("ForDelete");                     // origin
            animalService.createAnimal(dto);                // create
        }

        animalService.deleteAnimal(reg);                   // call delete

        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class, // expected
                () -> animalService.getAnimalByRegistrationNumber(reg)      // fetch again
        );
    }

    @Test
    void deleteAnimal_notFound() {
        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class, // expected
                () -> animalService.deleteAnimal("DOES-NOT-EXIST")          // invalid reg
        );
    }
}
