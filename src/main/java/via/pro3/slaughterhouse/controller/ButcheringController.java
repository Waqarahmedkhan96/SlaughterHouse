package via.pro3.slaughterhouse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import via.pro3.slaughterhouse.dto.ButcheringDtos;
import via.pro3.slaughterhouse.service.ButcheringService;

@RestController
@RequestMapping("/cutting")
public class ButcheringController {

    private final ButcheringService cuttingService;

    public ButcheringController(ButcheringService cuttingService) {
        this.cuttingService = cuttingService;
    }

    // ---------- Create/Post TRAYS ----------

    @PostMapping("/trays")
    public ResponseEntity<ButcheringDtos.TrayDto> createTray(
            @RequestBody ButcheringDtos.CreateTrayDto dto) {

        ButcheringDtos.TrayDto created = cuttingService.createTray(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
    public ResponseEntity<ButcheringDtos.PartDto> createPart(
            @RequestBody ButcheringDtos.CreatePartDto dto) {

        ButcheringDtos.PartDto created = cuttingService.createPart(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
