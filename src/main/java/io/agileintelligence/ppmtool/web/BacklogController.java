package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.services.MapValidationErrorService;
import io.agileintelligence.ppmtool.services.ProjectTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
                                            BindingResult bindingResult) {
        final ResponseEntity<?> errorMap = validationService.mapValidationService(bindingResult);
        if(errorMap != null) return errorMap;
        final ProjectTask savedProjectTask = projectTaskService.addProjectTask(backlogId, projectTask);
        return new ResponseEntity<>(savedProjectTask, HttpStatus.CREATED);

    }

    @GetMapping("/{backlogId}")
    public ResponseEntity<List<ProjectTask>> getProjectBacklog(@PathVariable String backlogId) {
        return new ResponseEntity<>(projectTaskService.findBacklogById(backlogId), HttpStatus.OK);
    }
}
