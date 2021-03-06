package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.services.MapValidationErrorService;
import io.agileintelligence.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ProjectController(ProjectService projectService, MapValidationErrorService mapValidationErrorService) {
        this.projectService = projectService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result,
                                              Principal principal) {
        final ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationService(result);
        if(responseEntity != null) return responseEntity;
        return new ResponseEntity<>(projectService.createProject(project, principal.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable String projectId, @Valid @RequestBody Project project,
                                           BindingResult result, Principal principal) {
        final ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationService(result);
        if(responseEntity != null) return responseEntity;
        return new ResponseEntity<>(projectService.updateProject(project, principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId, Principal principal) {
        final Project project = projectService.findByProjectIdentifier(projectId, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal) {
        return projectService.findAllProjects(principal.getName());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProjectByIdentifier(@PathVariable String projectId, Principal principal) {
        projectService.deleteProjectIdentifier(projectId, principal.getName());
        return new ResponseEntity<>("Project with ID '"+projectId+"' deleted", HttpStatus.OK);
    }
}
