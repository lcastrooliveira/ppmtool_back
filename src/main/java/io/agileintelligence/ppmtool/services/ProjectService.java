package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.exceptions.ProjectIdException;
import io.agileintelligence.ppmtool.exceptions.ProjectNotFound;
import io.agileintelligence.ppmtool.repositories.BacklogRepository;
import io.agileintelligence.ppmtool.repositories.ProjectRepository;
import io.agileintelligence.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final BacklogRepository backlogRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          BacklogRepository backlogRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Project createProject(Project project, String username) {
        try {
            final User user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());

            final String projectIdentifier = project.getProjectIdentifier().toUpperCase();
            project.setProjectIdentifier(projectIdentifier);

            final Backlog backlog = new Backlog();
            project.setBacklog(backlog);
            backlog.setProject(project);
            backlog.setProjectIdentifier(projectIdentifier);

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier()+"' already exists");
        }
    }

    @Transactional
    public Project updateProject(Project project, String username) {
        final Project savedProject = findByProjectIdentifier(project.getProjectIdentifier(), username);
        savedProject.setDescription(project.getDescription());
        savedProject.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier()).orElse(null));
        savedProject.setProjectName(project.getProjectName());
        savedProject.setStartDate(project.getStartDate());
        savedProject.setEndDate(project.getEndDate());
        return projectRepository.save(savedProject);
    }

    @Transactional(readOnly = true)
    public Project findByProjectIdentifier(String projectId, String username) {
        final Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if(project == null ) {
            throw new ProjectIdException("Project ID '"+projectId.toUpperCase()+"' does not exist");
        }

        if(!project.getProjectLeader().equals(username)) {
            throw new ProjectNotFound("Project not found in your account");
        }
        return project;
    }

    @Transactional(readOnly = true)
    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    @Transactional
    public void deleteProjectIdentifier(String projectId, String username) {
        projectRepository.delete(findByProjectIdentifier(projectId, username));
    }
}
