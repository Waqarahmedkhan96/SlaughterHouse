package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.dto.rest.PackagingDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.ProductKind;
import via.pro3.slaughterhouse.entity.Tray;
import via.pro3.slaughterhouse.exception.PackagingExceptions;
import via.pro3.slaughterhouse.repository.AnimalRepository;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.TrayRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PackagingServiceTest {

    @Autowired
    private PackagingService packagingService;      // station 3 service

    @Autowired
    private AnimalRepository animalRepository;      // animal repo
    @Autowired
    private PartRepository partRepository;          // part repo
    @Autowired
    private TrayRepository trayRepository;          // tray repo

    // helper: create tray
    private Tray createTestTray() {
        Tray tray = new Tray();            // new tray
        tray.setType("TEST_TRAY");         // simple type
        tray.setMaxWeight(200.0);          // some capacity
        return trayRepository.save(tray);  // save tray
    }

    // helper: create animal (unique reg no)
    private Animal createTestAnimal(String prefix) {
        Animal animal = new Animal();                         // new animal
        String reg = prefix + "-" + UUID.randomUUID();        // unique reg
        animal.setRegistrationNumber(reg);                    // reg
        animal.setWeight(400.0);                              // weight
        animal.setArrivalDate(LocalDate.now());               // date
        animal.setOrigin("PackFarm");                         // origin
        return animalRepository.save(animal);                 // save
    }

    // helper: create part
    private Part createPart(String type, Animal animal) {
        Part part = new Part();                    // new part
        part.setType(type);                        // part type
        part.setWeight(10.0);                      // weight
        part.setAnimal(animal);                    // link animal
        part.setTray(createTestTray());            // link tray (not null)
        return partRepository.save(part);          // save
    }

    @Test
    void createProduct_sameType_success() {
        // create animal
        Animal animal = createTestAnimal("P-SAME-ANIMAL"); // helper

        // create 2 same-type parts
        Part p1 = createPart("leg", animal);       // part 1
        Part p2 = createPart("leg", animal);       // part 2

        PackagingDtos.CreateProductDto dto =
                new PackagingDtos.CreateProductDto(); // product dto
        dto.setKind(ProductKind.SAME_TYPE);        // kind
        dto.setPartIds(List.of(p1.getId(), p2.getId())); // parts

        PackagingDtos.ProductDto created =
                packagingService.createProduct(dto); // call service

        assertNotNull(created.getId());             // id exists
        assertEquals(ProductKind.SAME_TYPE, created.getKind()); // kind ok
        assertEquals(2, created.getPartIds().size());           // 2 parts
    }

    @Test
    void createProduct_sameType_mixedTypes_throws() {
        // create animal
        Animal animal = createTestAnimal("P-MIX-ANIMAL"); // helper

        // mixed-type parts
        Part p1 = createPart("leg", animal);       // leg
        Part p2 = createPart("rib", animal);       // rib

        PackagingDtos.CreateProductDto dto =
                new PackagingDtos.CreateProductDto(); // product dto
        dto.setKind(ProductKind.SAME_TYPE);        // SAME_TYPE
        dto.setPartIds(List.of(p1.getId(), p2.getId())); // part ids

        assertThrows(
                PackagingExceptions.InvalidProductCompositionException.class, // expected
                () -> packagingService.createProduct(dto)                    // call
        );
    }

    @Test
    void createProduct_halfAnimal_success() {
        // create animal
        Animal animal = createTestAnimal("P-HALF-ANIMAL"); // helper

        // 3 different types
        Part p1 = createPart("leg", animal);       // leg
        Part p2 = createPart("rib", animal);       // rib
        Part p3 = createPart("shoulder", animal);  // shoulder

        PackagingDtos.CreateProductDto dto =
                new PackagingDtos.CreateProductDto(); // product dto
        dto.setKind(ProductKind.HALF_ANIMAL);      // HALF_ANIMAL
        dto.setPartIds(List.of(p1.getId(), p2.getId(), p3.getId())); // ids

        PackagingDtos.ProductDto created =
                packagingService.createProduct(dto); // call service

        assertNotNull(created.getId());             // id exists
        assertEquals(ProductKind.HALF_ANIMAL, created.getKind()); // kind ok
        assertEquals(3, created.getPartIds().size());             // 3 parts
    }

    @Test
    void createProduct_halfAnimal_notEnoughTypes_throws() {
        // create animal
        Animal animal = createTestAnimal("P-HALF-FAIL"); // helper

        // only 1 type
        Part p1 = createPart("leg", animal);       // leg
        Part p2 = createPart("leg", animal);       // leg again

        PackagingDtos.CreateProductDto dto =
                new PackagingDtos.CreateProductDto(); // product dto
        dto.setKind(ProductKind.HALF_ANIMAL);      // HALF_ANIMAL
        dto.setPartIds(List.of(p1.getId(), p2.getId())); // only leg

        assertThrows(
                PackagingExceptions.InvalidProductCompositionException.class, // expected
                () -> packagingService.createProduct(dto)                    // call
        );
    }
}
