package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.exceptions.ProjectNotFound;
import io.agileintelligence.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectService projectService;

    @Autowired
    public ProjectTaskService(ProjectTaskRepository projectTaskRepository,
                              ProjectService projectService) {

        this.projectTaskRepository = projectTaskRepository;
        this.projectService = projectService;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
        final Optional<Backlog> backlogOptional = Optional.ofNullable(projectService.findByProjectIdentifier(projectIdentifier, username).getBacklog());
        return backlogOptional.map(backlog -> {
            projectTask.setBacklog(backlog);
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            if( projectTask.getPriority() == null || projectTask.getPriority() == 0)
                projectTask.setPriority(3);
            if(projectTask.getStatus() == null || projectTask.getStatus().equals(""))
                projectTask.setStatus("TO_DO");
            return projectTaskRepository.save(projectTask);
        }).orElseThrow(ProjectNotFound::new);
    }

    public List<ProjectTask> findBacklogById(String backlogId, String username) {
        projectService.findByProjectIdentifier(backlogId, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
    }

    public ProjectTask findByProjectSequence(String backlogId, String ptId, String username) {
        projectService.findByProjectIdentifier(backlogId, username);
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(ptId).orElseThrow(ProjectNotFound::new);
        if(!projectTask.getProjectIdentifier().equals(backlogId))
            throw new ProjectNotFound(String.format("Project Task %s does not exists in project %s", ptId, backlogId));
        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlogId, String ptId, String username) {
        findByProjectSequence(backlogId, ptId, username);
        return projectTaskRepository.save(updatedTask);
    }

    public void deletePTByProjectSequence(String backlogId, String ptId, String username) {
        final ProjectTask projectTask = findByProjectSequence(backlogId, ptId, username);
        projectTaskRepository.delete(projectTask);
    }
}
