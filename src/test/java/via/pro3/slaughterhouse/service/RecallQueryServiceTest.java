package via.pro3.slaughterhouse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Product;
import via.pro3.slaughterhouse.entity.ProductKind;
import via.pro3.slaughterhouse.entity.Tray;
import via.pro3.slaughterhouse.repository.AnimalRepository;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.ProductRepository;
import via.pro3.slaughterhouse.repository.TrayRepository;

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

    // simple counter for unique reg numbers
    private static long regSuffix = System.currentTimeMillis();

    private String nextReg(String base) {
        // e.g. "DK-TEST-UNIT-1732920..."
        return base + "-" + (regSuffix++);
    }

    @Test
    void animalsByProductId_returnsExpectedRegistration() {
        String regNumber = nextReg("DK-TEST-UNIT");   // unique reg no.

        // Arrange: minimal graph
        Animal animal = new Animal(regNumber, 450.0, LocalDate.now(), "TestFarm");
        animal = animalRepo.save(animal);

        Tray tray = new Tray("leg", 40.0);
        tray = trayRepo.save(tray);

        Part part = new Part(3.0, "leg", animal, tray);
        part = partRepo.save(part);

        // use enum SAME_TYPE
        Product product = new Product(ProductKind.SAME_TYPE);
        product.setParts(Set.of(part));
        product = productRepo.save(product);

        // Act
        List<String> regs =
                recallService.getAnimalRegistrationNumbersByProductId(product.getId());

        // Assert
        assertThat(regs).contains(regNumber);
    }

    @Test
    void productsByAnimalReg_returnsExpectedProductId() {
        String regNumber = nextReg("DK-TEST2");       // unique reg no.

        // Arrange: minimal graph
        Animal animal = new Animal(regNumber, 480.0, LocalDate.now(), "TestFarm2");
        animal = animalRepo.save(animal);

        Tray tray = new Tray("rib", 60.0);
        tray = trayRepo.save(tray);

        Part part = new Part(4.0, "rib", animal, tray);
        part = partRepo.save(part);

        // use enum HALF_ANIMAL
        Product product = new Product(ProductKind.HALF_ANIMAL);
        product.setParts(Set.of(part));
        product = productRepo.save(product);

        // Act
        List<Long> productIds =
                recallService.getProductIdsByAnimalRegistrationNumber(regNumber);

        // Assert
        assertThat(productIds).contains(product.getId());
    }
}
