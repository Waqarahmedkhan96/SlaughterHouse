package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.dto.rest.AnimalRegistrationDtos;
import via.pro3.slaughterhouse.dto.rest.ButcheringDtos;
import via.pro3.slaughterhouse.entity.Tray;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;
import via.pro3.slaughterhouse.exception.ButcheringExceptions;
import via.pro3.slaughterhouse.repository.TrayRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ButcheringServiceTest {

    @Autowired
    private ButcheringService butcheringService;          // station 2 service

    @Autowired
    private AnimalRegistrationService animalService;      // station 1 service

    @Autowired
    private TrayRepository trayRepository;                // direct tray repo

    // ---------- TRAY TESTS ----------

    @Test
    void createTray_success() {
        ButcheringDtos.CreateTrayDto dto =
                new ButcheringDtos.CreateTrayDto();        // new tray dto
        dto.setType("leg");                               // tray type
        dto.setMaxWeight(100.0);                          // max weight

        ButcheringDtos.TrayDto created =
                butcheringService.createTray(dto);        // call service

        assertNotNull(created.getId());                   // id exists
        assertEquals("leg", created.getType());           // correct type
        assertEquals(100.0, created.getMaxWeight());      // max weight ok
        assertEquals(0.0, created.getCurrentWeight());    // empty tray
    }

    @Test
    void updateTray_changeTypeWhenEmpty_success() {
        // create tray in DB
        Tray tray = new Tray();                           // new entity
        tray.setType("oldType");                          // old type
        tray.setMaxWeight(50.0);                          // max weight
        tray = trayRepository.save(tray);

        ButcheringDtos.UpdateTrayDto dto =
                new ButcheringDtos.UpdateTrayDto();       // update dto
        dto.setType("newType");                           // new type

        ButcheringDtos.TrayDto updated =
                butcheringService.updateTray(tray.getId(), dto); // call update

        assertEquals("newType", updated.getType());       // type updated
    }

    @Test
    void deleteTray_withParts_throws() {
        // here we simulate a tray with parts by using service flow

        // Step 1: ensure animal exists
        String reg = "B-DEL-ANIMAL";                      // test reg
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (AnimalRegistrationExceptions.AnimalNotFoundException e) {
            AnimalRegistrationDtos.CreateAnimalDto animalDto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // animal dto
            animalDto.setRegistrationNumber(reg);          // reg
            animalDto.setWeight(300.0);                    // weight
            animalDto.setArrivalDate(LocalDate.now());     // date
            animalDto.setOrigin("ButcherFarm");            // origin
            animalService.createAnimal(animalDto);         // create animal
        }

        // Step 2: create tray
        ButcheringDtos.CreateTrayDto trayDto =
                new ButcheringDtos.CreateTrayDto();       // tray dto
        trayDto.setType("rib");                           // type
        trayDto.setMaxWeight(50.0);                       // max weight
        ButcheringDtos.TrayDto tray =
                butcheringService.createTray(trayDto);    // create tray

        // Step 3: put part on tray
        ButcheringDtos.CreatePartDto partDto =
                new ButcheringDtos.CreatePartDto();       // part dto
        partDto.setAnimalRegistrationNumber(reg);         // reg
        partDto.setType("rib");                           // part type
        partDto.setWeight(10.0);                          // weight
        partDto.setTrayId(tray.getId());                  // assign tray
        butcheringService.createPart(partDto);            // create part

        // Step 4: delete should fail
        assertThrows(
                ButcheringExceptions.InvalidTrayForPartException.class, // expected
                () -> butcheringService.deleteTray(tray.getId())        // delete call
        );
    }

    // ---------- PART TESTS ----------

    @Test
    void createPart_success() {
        String reg = "B-PART-ANIMAL";                     // test reg

        // ensure animal exists
        try {
            animalService.getAnimalByRegistrationNumber(reg); // try get
        } catch (AnimalRegistrationExceptions.AnimalNotFoundException e) {
            AnimalRegistrationDtos.CreateAnimalDto animalDto =
                    new AnimalRegistrationDtos.CreateAnimalDto(); // animal dto
            animalDto.setRegistrationNumber(reg);          // reg
            animalDto.setWeight(350.0);                    // weight
            animalDto.setArrivalDate(LocalDate.now());     // date
            animalDto.setOrigin("PartFarm");               // origin
            animalService.createAnimal(animalDto);         // create
        }

        // create tray
        ButcheringDtos.CreateTrayDto trayDto =
                new ButcheringDtos.CreateTrayDto();       // tray dto
        trayDto.setType("leg");                           // type
        trayDto.setMaxWeight(80.0);                       // max weight
        ButcheringDtos.TrayDto tray =
                butcheringService.createTray(trayDto);    // create tray

        // create part
        ButcheringDtos.CreatePartDto partDto =
                new ButcheringDtos.CreatePartDto();       // part dto
        partDto.setAnimalRegistrationNumber(reg);         // reg
        partDto.setType("leg");                           // same type
        partDto.setWeight(20.0);                          // weight
        partDto.setTrayId(tray.getId());                  // tray id

        ButcheringDtos.PartDto created =
                butcheringService.createPart(partDto);    // create part

        assertNotNull(created.getId());                   // id exists
        assertEquals("leg", created.getType());           // correct type
        assertEquals(reg, created.getAnimalRegistrationNumber()); // correct animal
        assertEquals(tray.getId(), created.getTrayId());  // correct tray
    }

    @Test
    void createPart_unknownAnimal_throws() {
        ButcheringDtos.CreatePartDto partDto =
                new ButcheringDtos.CreatePartDto();       // part dto
        partDto.setAnimalRegistrationNumber("NO-SUCH-ANIMAL"); // invalid reg
        partDto.setType("rib");                           // type
        partDto.setWeight(10.0);                          // weight

        assertThrows(
                AnimalRegistrationExceptions.AnimalNotFoundException.class, // expected
                () -> butcheringService.createPart(partDto)                // call create
        );
    }
}
