package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.dto.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnimalRegistrationServiceTest {

    @Autowired
    private AnimalRegistrationService animalService;

    // ---------- CREATE ----------

    @Test
    void createAnimal_success() {
        String reg = "T-UNIT-1";

        // if exists → delete first (avoid duplicate error)
        try {
            animalService.deleteAnimal(reg);
        } catch (AnimalRegistrationExceptions.AnimalNotFoundException ignored) {}

        AnimalRegistrationDtos.CreateAnimalDto dto = new AnimalRegistrationDtos.CreateAnimalDto();
        dto.setRegistrationNumber(reg);
        dto.setWeight(400.5);
        dto.setArrivalDate(LocalDate.now());
        dto.setOrigin("TestFarm");

        AnimalRegistrationDtos.AnimalDto created = animalService.createAnimal(dto);

        assertEquals(reg, created.getRegistrationNumber());
        assertEquals("TestFarm", created.getOrigin());
    }

    @Test
    void createAnimal_duplicate_throwsException() {
        String reg = "T-UNIT-2";

        // ensure one exists
        try {
            animalService.getAnimalByRegistrationNumber(reg);
        } catch (Exception e) {
            // create if missing
            AnimalRegistrationDtos.CreateAnimalDto dto = new AnimalRegistrationDtos.CreateAnimalDto();
            dto.setRegistrationNumber(reg);
            dto.setWeight(500.0);
            dto.setArrivalDate(LocalDate.now());
            dto.setOrigin("Farm X");
            animalService.createAnimal(dto);
        }

        AnimalRegistrationDtos.CreateAnimalDto duplicate = new AnimalRegistrationDtos.CreateAnimalDto();
        duplicate.setRegistrationNumber(reg);
        duplicate.setWeight(900.0);
        duplicate.setArrivalDate(LocalDate.now());
        duplicate.setOrigin("Farm Y");

        assertThrows(
                AnimalRegistrationExceptions.AnimalAlreadyExistsException.class,
                () -> animalService.createAnimal(duplicate)
        );
    }

    // ---------- READ ONE ----------

    @Test
    void getAnimalByRegistrationNumber_found() {
        AnimalRegistrationDtos.AnimalDto dto =
                animalService.getAnimalByRegistrationNumber("DK-0001");

        assertEquals("DK-0001", dto.getRegistrationNumber());
        assertNotNull(dto.getWeight());
    }

    @Test
    void getAnimalByRegistrationNumber_notFound() {
        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class,
                () -> animalService.getAnimalByRegistrationNumber("DOES-NOT-EXIST")
        );
    }

    // ---------- LIST BY DATE ----------

    @Test
    void getAnimalsByArrivalDate_listReturned() {
        LocalDate today = LocalDate.now();

        var list = animalService.getAnimalsByArrivalDate(today);

        assertNotNull(list);
        assertTrue(list.getAnimals().size() >= 0);
    }

    // ---------- LIST BY ORIGIN ----------

    @Test
    void getAnimalsByOrigin_listReturned() {
        var list = animalService.getAnimalsByOrigin("Farm A");

        assertNotNull(list.getAnimals());
    }

    // ---------- UPDATE ----------

    @Test
    void updateAnimal_success() {
        String reg = "T-UPDATE-1";

        // ensure exists
        try {
            animalService.getAnimalByRegistrationNumber(reg);
        } catch (Exception e) {
            AnimalRegistrationDtos.CreateAnimalDto dto = new AnimalRegistrationDtos.CreateAnimalDto();
            dto.setRegistrationNumber(reg);
            dto.setWeight(300.0);
            dto.setArrivalDate(LocalDate.now());
            dto.setOrigin("OldFarm");
            animalService.createAnimal(dto);
        }

        AnimalRegistrationDtos.UpdateAnimalDto updateDto = new AnimalRegistrationDtos.UpdateAnimalDto();
        updateDto.setWeight(777.7);
        updateDto.setOrigin("NewFarm");

        AnimalRegistrationDtos.AnimalDto updated =
                animalService.updateAnimal(reg, updateDto);

        assertEquals(777.7, updated.getWeight());
        assertEquals("NewFarm", updated.getOrigin());
    }

    @Test
    void updateAnimal_notFound() {
        AnimalRegistrationDtos.UpdateAnimalDto update = new AnimalRegistrationDtos.UpdateAnimalDto();
        update.setWeight(999.9);

        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class,
                () -> animalService.updateAnimal("NO-ANIMAL", update)
        );
    }

    // ---------- DELETE ----------

    @Test
    void deleteAnimal_success() {
        String reg = "T-DELETE-1";

        // create if not exists
        try {
            animalService.getAnimalByRegistrationNumber(reg);
        } catch (Exception e) {
            AnimalRegistrationDtos.CreateAnimalDto dto = new AnimalRegistrationDtos.CreateAnimalDto();
            dto.setRegistrationNumber(reg);
            dto.setWeight(300.0);
            dto.setArrivalDate(LocalDate.now());
            dto.setOrigin("ForDelete");
            animalService.createAnimal(dto);
        }

        animalService.deleteAnimal(reg);

        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class,
                () -> animalService.getAnimalByRegistrationNumber(reg)
        );
    }

    @Test
    void deleteAnimal_notFound() {
        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class,
                () -> animalService.deleteAnimal("DOES-NOT-EXIST")
        );
    }
}
