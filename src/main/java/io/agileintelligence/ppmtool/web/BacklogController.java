package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.services.MapValidationErrorService;
import io.agileintelligence.ppmtool.services.ProjectTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {
    private final ProjectTaskService projectTaskService;
    private final MapValidationErrorService validationService;

    public BacklogController(ProjectTaskService projectTaskService,
                             MapValidationErrorService validationService) {
        this.projectTaskService = projectTaskService;
        this.validationService = validationService;
    }

    @PostMapping("/{backlogId}")
    public ResponseEntity<?> addPTtoBacklog(@PathVariable String backlogId,
                                            @Valid @RequestBody ProjectTask projectTask,
                                            BindingResult bindingResult,
                                            Principal principal) {
        final ResponseEntity<?> errorMap = validationService.mapValidationService(bindingResult);
        if(errorMap != null) return errorMap;
        final ProjectTask savedProjectTask = projectTaskService.addProjectTask(backlogId, projectTask, principal.getName());
        return new ResponseEntity<>(savedProjectTask, HttpStatus.CREATED);

    }

    @GetMapping("/{backlogId}")
    public ResponseEntity<List<ProjectTask>> getProjectBacklog(@PathVariable String backlogId, Principal principal) {
        return new ResponseEntity<>(projectTaskService.findBacklogById(backlogId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{backlogId}/{ptId}")
    public ResponseEntity<ProjectTask> getProjectTaskByProjectSequence(@PathVariable String backlogId,
                                                                       @PathVariable String ptId,
                                                                       Principal principal) {
        final ProjectTask projectTask = projectTaskService.findByProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> updateProjectTask(@PathVariable String backlogId,
                                               @PathVariable String ptId,
                                               @Valid @RequestBody ProjectTask projectTask,
                                               BindingResult bindingResult,
                                               Principal principal) {
        final ResponseEntity<?> errorMap = validationService.mapValidationService(bindingResult);
        if(errorMap != null) return errorMap;
        final ProjectTask updatedProjectTask = projectTaskService.updateByProjectSequence(projectTask, backlogId, ptId, principal.getName());
        return new ResponseEntity<>(updatedProjectTask, HttpStatus.OK);

    }

    @DeleteMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        projectTaskService.deletePTByProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<>("Project Task "+ptId+" was deleted successfully", HttpStatus.OK);
    }
}
