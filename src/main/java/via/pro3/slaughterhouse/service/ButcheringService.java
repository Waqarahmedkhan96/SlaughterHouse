// 2. Station 2, animals are cut into parts and placed on trays.
package via.pro3.slaughterhouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import via.pro3.slaughterhouse.dto.ButcheringDtos;
import via.pro3.slaughterhouse.entity.Animal;
import via.pro3.slaughterhouse.entity.Part;
import via.pro3.slaughterhouse.entity.Tray;
import via.pro3.slaughterhouse.exception.AnimalRegistrationExceptions;
import via.pro3.slaughterhouse.exception.CuttingAndTrayExceptions;
import via.pro3.slaughterhouse.repository.AnimalRepository;
import via.pro3.slaughterhouse.repository.PartRepository;
import via.pro3.slaughterhouse.repository.TrayRepository;

import java.util.List;

@Service
@Transactional
public class ButcheringService {

    private final PartRepository partRepository;
    private final TrayRepository trayRepository;
    private final AnimalRepository animalRepository;

    public ButcheringService(PartRepository partRepository,
                             TrayRepository trayRepository,
                             AnimalRepository animalRepository) {
        this.partRepository = partRepository;
        this.trayRepository = trayRepository;
        this.animalRepository = animalRepository;
    }

    // ---------------- TRAYS ----------------

    // create new tray
    public ButcheringDtos.TrayDto createTray(ButcheringDtos.CreateTrayDto dto) {
        Tray tray = new Tray();
        tray.setType(dto.getType());
        tray.setMaxWeight(dto.getMaxWeight());

        Tray saved = trayRepository.save(tray);
        return toTrayDto(saved);
    }

    // get tray by id
    public ButcheringDtos.TrayDto getTrayById(Long id) {
        Tray tray = trayRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(id));
        return toTrayDto(tray);
    }

    // get all trays
    public ButcheringDtos.TrayListDto getAllTrays() {
        List<Tray> trays = trayRepository.findAll();
        ButcheringDtos.TrayListDto listDto = new ButcheringDtos.TrayListDto();
        listDto.setTrays(trays.stream().map(this::toTrayDto).toList());
        return listDto;
    }

    // update tray data
    public ButcheringDtos.TrayDto updateTray(Long id, ButcheringDtos.UpdateTrayDto dto) {
        Tray tray = trayRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(id));

        // update type only if provided and tray is empty
        if (dto.getType() != null) {
            if (!tray.getParts().isEmpty()) {
                throw new CuttingAndTrayExceptions.InvalidTrayForPartException(
                        "Cannot change type of non-empty tray");
            }
            tray.setType(dto.getType());
        }

        // update max weight if provided and not below current weight
        if (dto.getMaxWeight() != null) {
            double currentWeight = tray.getParts()
                    .stream()
                    .mapToDouble(Part::getWeight)
                    .sum();
            if (dto.getMaxWeight() < currentWeight) {
                throw new CuttingAndTrayExceptions.TrayCapacityExceededException(id);
            }
            tray.setMaxWeight(dto.getMaxWeight());
        }

        Tray saved = trayRepository.save(tray);
        return toTrayDto(saved);
    }

    // delete tray by id
    public void deleteTray(Long id) {
        Tray tray = trayRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(id));

        if (!tray.getParts().isEmpty()) {
            throw new CuttingAndTrayExceptions.InvalidTrayForPartException(
                    "Cannot delete tray with existing parts");
        }

        trayRepository.delete(tray);
    }

    // ---------------- PARTS ----------------

    // create new part
    public ButcheringDtos.PartDto createPart(ButcheringDtos.CreatePartDto dto) {
        // find origin animal
        Animal animal = animalRepository.findByRegistrationNumber(dto.getAnimalRegistrationNumber())
                .orElseThrow(() ->
                        new AnimalRegistrationExceptions.AnimalNotFoundException(dto.getAnimalRegistrationNumber()));

        Part part = new Part();
        part.setWeight(dto.getWeight());
        part.setType(dto.getType());
        part.setAnimal(animal);

        // optional tray assignment
        if (dto.getTrayId() != null) {
            Tray tray = trayRepository.findById(dto.getTrayId())
                    .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(dto.getTrayId()));
            placePartOnTray(part, tray);
        }

        Part saved = partRepository.save(part);
        return toPartDto(saved);
    }

    // get part by id
    public ButcheringDtos.PartDto getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.PartNotFoundException(id));
        return toPartDto(part);
    }

    // get all parts
    public ButcheringDtos.PartListDto getAllParts() {
        List<Part> parts = partRepository.findAll();
        ButcheringDtos.PartListDto listDto = new ButcheringDtos.PartListDto();
        listDto.setParts(parts.stream().map(this::toPartDto).toList());
        return listDto;
    }

    // update part data
    public ButcheringDtos.PartDto updatePart(Long id, ButcheringDtos.UpdatePartDto dto) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.PartNotFoundException(id));

        // update weight if provided
        if (dto.getWeight() != null) {
            double oldWeight = part.getWeight();
            double newWeight = dto.getWeight();

            Tray tray = part.getTray();
            if (tray != null) {
                double currentWeight = tray.getParts()
                        .stream()
                        .mapToDouble(Part::getWeight)
                        .sum();
                double adjusted = currentWeight - oldWeight + newWeight;
                if (adjusted > tray.getMaxWeight()) {
                    throw new CuttingAndTrayExceptions.TrayCapacityExceededException(tray.getId());
                }
            }

            part.setWeight(newWeight);
        }

        // update type if provided
        if (dto.getType() != null) {
            Tray tray = part.getTray();
            if (tray != null && tray.getType() != null && !tray.getType().equals(dto.getType())) {
                throw new CuttingAndTrayExceptions.InvalidTrayForPartException(
                        "Part type does not match tray type");
            }
            part.setType(dto.getType());
        }

        // update animal if provided
        if (dto.getAnimalRegistrationNumber() != null) {
            Animal animal = animalRepository.findByRegistrationNumber(dto.getAnimalRegistrationNumber())
                    .orElseThrow(() ->
                            new AnimalRegistrationExceptions.AnimalNotFoundException(dto.getAnimalRegistrationNumber()));
            part.setAnimal(animal);
        }

        // update tray if provided (can also remove tray if null)
        if (dto.getTrayId() != null) {
            Tray tray = trayRepository.findById(dto.getTrayId())
                    .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(dto.getTrayId()));
            placePartOnNewTray(part, tray);
        } else if (dto.getTrayId() == null && dto.getWeight() == null && dto.getType() == null
                && dto.getAnimalRegistrationNumber() == null) {
            // nothing specific requested – leave tray as is
        }

        Part saved = partRepository.save(part);
        return toPartDto(saved);
    }

    // delete part by id
    public void deletePart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new CuttingAndTrayExceptions.PartNotFoundException(id));

        Tray tray = part.getTray();
        if (tray != null) {
            tray.getParts().remove(part);
        }

        partRepository.delete(part);
    }

    // assign part to tray
    public ButcheringDtos.PartDto assignPartToTray(Long partId, Long trayId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new CuttingAndTrayExceptions.PartNotFoundException(partId));
        Tray tray = trayRepository.findById(trayId)
                .orElseThrow(() -> new CuttingAndTrayExceptions.TrayNotFoundException(trayId));

        placePartOnNewTray(part, tray);
        Part saved = partRepository.save(part);
        return toPartDto(saved);
    }

    // ---------------- HELPERS ----------------

    // place part on tray (new part)
    private void placePartOnTray(Part part, Tray tray) {
        // Check type match
        if (tray.getType() != null && !tray.getType().equals(part.getType())) {
            throw new CuttingAndTrayExceptions.InvalidTrayForPartException(
                    "Tray type " + tray.getType() + " does not match part type " + part.getType());
        }

        // Compute current weight
        double currentWeight = tray.getParts()
                .stream()
                .mapToDouble(Part::getWeight)
                .sum();

        if (currentWeight + part.getWeight() > tray.getMaxWeight()) {
            throw new CuttingAndTrayExceptions.TrayCapacityExceededException(tray.getId());
        }

        // Update both sides of relation
        part.setTray(tray);
        tray.getParts().add(part);
    }

    // move existing part to new tray
    private void placePartOnNewTray(Part part, Tray newTray) {
        // remove from old tray if present
        Tray oldTray = part.getTray();
        if (oldTray != null) {
            oldTray.getParts().remove(part);
        }

        // Check type match
        if (newTray.getType() != null && !newTray.getType().equals(part.getType())) {
            throw new CuttingAndTrayExceptions.InvalidTrayForPartException(
                    "Tray type " + newTray.getType() + " does not match part type " + part.getType());
        }

        // Compute current weight
        double currentWeight = newTray.getParts()
                .stream()
                .mapToDouble(Part::getWeight)
                .sum();

        if (currentWeight + part.getWeight() > newTray.getMaxWeight()) {
            throw new CuttingAndTrayExceptions.TrayCapacityExceededException(newTray.getId());
        }

        part.setTray(newTray);
        newTray.getParts().add(part);
    }

    // convert tray entity to dto
    private ButcheringDtos.TrayDto toTrayDto(Tray tray) {
        ButcheringDtos.TrayDto dto = new ButcheringDtos.TrayDto();
        dto.setId(tray.getId());
        dto.setType(tray.getType());
        dto.setMaxWeight(tray.getMaxWeight());

        double currentWeight = tray.getParts()
                .stream()
                .mapToDouble(Part::getWeight)
                .sum();
        dto.setCurrentWeight(currentWeight);

        dto.setPartIds(tray.getParts().stream()
                .map(Part::getId)
                .toList());

        return dto;
    }

    // convert part entity to dto
    private ButcheringDtos.PartDto toPartDto(Part part) {
        ButcheringDtos.PartDto dto = new ButcheringDtos.PartDto();
        dto.setId(part.getId());
        dto.setWeight(part.getWeight());
        dto.setType(part.getType());
        dto.setAnimalRegistrationNumber(
                part.getAnimal() != null ? part.getAnimal().getRegistrationNumber() : null);
        dto.setTrayId(part.getTray() != null ? part.getTray().getId() : null);
        return dto;
    }
}
