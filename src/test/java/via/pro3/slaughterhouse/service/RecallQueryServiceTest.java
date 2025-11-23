package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.entity.*;
import via.pro3.slaughterhouse.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecallQueryServiceTest {

    @Autowired
    private RecallQueryService recallService;

    @Autowired
    private AnimalRepository animalRepo;
    @Autowired
    private TrayRepository trayRepo;
    @Autowired
    private PartRepository partRepo;
    @Autowired
    private ProductRepository productRepo;

    @Test
    void animalsByProductId_returnsExpectedRegistration()
    {
        // Arrange: save minimal graph
        Animal animal = new Animal("DK-TEST-UNIT", 450.0, LocalDate.now(), "TestFarm");
        animal = animalRepo.save(animal);

        Tray tray = new Tray("leg", 40.0);
        tray = trayRepo.save(tray);

        Part part = new Part(3.0, "leg", animal, tray);
        part = partRepo.save(part);

        Product product = new Product("Test product");
        product.setParts(Set.of(part));
        product = productRepo.save(product);

        // Act
        List<String> regs = recallService.getAnimalRegistrationNumbersByProductId(product.getId());

        // Assert
        assertThat(regs).contains("DK-TEST-UNIT");
    }

    @Test
    void productsByAnimalReg_returnsExpectedProductId() {
        // Arrange: save minimal graph for this test
        Animal animal = new Animal("DK-TEST2", 480.0, LocalDate.now(), "TestFarm2");
        animal = animalRepo.save(animal);

        Tray tray = new Tray("rib", 60.0);
        tray = trayRepo.save(tray);

        Part part = new Part(4.0, "rib", animal, tray);
        part = partRepo.save(part);

        Product product = new Product("Test ribs");
        product.setParts(Set.of(part));
        product = productRepo.save(product);

        // Act: call the other service method
        List<Long> productIds =
                recallService.getProductIdsByAnimalRegistrationNumber("DK-TEST2");

        // Assert: returned list contains our product's ID
        assertThat(productIds)
                .contains(product.getId());
    }

}
