package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.exceptions.ProjectNotFound;
import io.agileintelligence.ppmtool.repositories.BacklogRepository;
import io.agileintelligence.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;
    private final ProjectTaskRepository projectTaskRepository;

    @Autowired
    public ProjectTaskService(BacklogRepository backlogRepository,
                              ProjectTaskRepository projectTaskRepository) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        final Optional<Backlog> backlogOptional = backlogRepository.findByProjectIdentifier(projectIdentifier);
        return backlogOptional.map(backlog -> {
            projectTask.setBacklog(backlog);
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            if( projectTask.getPriority() == null || projectTask.getPriority() == 0)
                projectTask.setPriority(3);
            if(projectTask.getStatus() == null || projectTask.getStatus() == "")
                projectTask.setStatus("TO_DO");
            return projectTaskRepository.save(projectTask);
        }).orElseThrow(ProjectNotFound::new);
    }

    public List<ProjectTask> findBacklogById(String backlogId) {
        if(!backlogRepository.existsBacklogByProjectIdentifier(backlogId))
            throw new ProjectNotFound("Project does not exists");
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
    }

    public ProjectTask findByProjectSequence(String backlogId, String ptId) {
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId).orElseThrow(ProjectNotFound::new);
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(ptId).orElseThrow(ProjectNotFound::new);
        if(!projectTask.getProjectIdentifier().equals(backlogId))
            throw new ProjectNotFound(String.format("Project Task %s does not exists in project %s", ptId, backlogId));
        return projectTask;
    }
}
