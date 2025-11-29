package via.pro3.slaughterhouse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.rest.ButcheringDtos;
import via.pro3.slaughterhouse.service.ButcheringService;

import java.util.Map;

@RestController
@RequestMapping("/cutting")
public class ButcheringController {

    private final ButcheringService cuttingService;

    public ButcheringController(ButcheringService cuttingService) {
        this.cuttingService = cuttingService;
    }

    // ---------- Create TRAYS ----------
    @PostMapping("/trays")
    public ResponseEntity<?> createTray( // generic body
                                         @RequestBody ButcheringDtos.CreateTrayDto dto) {

        ButcheringDtos.TrayDto created = cuttingService.createTray(dto);

        if (created == null) {
            // queued (db down)
            Map<String, Object> body = Map.of(
                    "status", "QUEUED",                  // queued flag
                    "message", "Database unavailable. Tray stored in queue.",
                    "type", dto.getType(),              // tray type
                    "maxWeight", dto.getMaxWeight()     // capacity
            );
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED) // 202
                    .body(body);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(created);
    }

    @GetMapping("/trays/{id}")
    public ResponseEntity<ButcheringDtos.TrayDto> getTray(@PathVariable Long id) {
        ButcheringDtos.TrayDto tray = cuttingService.getTrayById(id);
        return ResponseEntity.ok(tray);
    }

    @GetMapping("/trays")
    public ResponseEntity<ButcheringDtos.TrayListDto> getAllTrays() {
        ButcheringDtos.TrayListDto trays = cuttingService.getAllTrays();
        return ResponseEntity.ok(trays);
    }

    // ---------- PARTS ----------
    @PostMapping("/parts")
    public ResponseEntity<?> createPart( // generic body
                                         @RequestBody ButcheringDtos.CreatePartDto dto) {

        ButcheringDtos.PartDto created = cuttingService.createPart(dto);

        if (created == null) {
            // queued (db down)
            Map<String, Object> body = Map.of(
                    "status", "QUEUED",                            // queued flag
                    "message", "Database unavailable. Part stored in queue.",
                    "type", dto.getType(),                        // part type
                    "weight", dto.getWeight(),                    // part weight
                    "animalRegistrationNumber", dto.getAnimalRegistrationNumber(),
                    "trayId", dto.getTrayId()                     // optional tray
            );
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED) // 202
                    .body(body);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(created);
    }

    @GetMapping("/parts/{id}")
    public ResponseEntity<ButcheringDtos.PartDto> getPart(@PathVariable Long id) {
        ButcheringDtos.PartDto part = cuttingService.getPartById(id);
        return ResponseEntity.ok(part);
    }

    @GetMapping("/parts")
    public ResponseEntity<ButcheringDtos.PartListDto> getAllParts() {
        ButcheringDtos.PartListDto parts = cuttingService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    // Assign existing part to an existing tray (enforces type + capacity rules)
    @PostMapping("/trays/{trayId}/parts/{partId}")
    public ResponseEntity<ButcheringDtos.PartDto> assignPartToTray(
            @PathVariable Long trayId,
            @PathVariable Long partId) {

        ButcheringDtos.PartDto updated = cuttingService.assignPartToTray(partId, trayId);
        return ResponseEntity.ok(updated);
    }
}
