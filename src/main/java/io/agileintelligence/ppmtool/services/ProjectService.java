package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.exceptions.ProjectIdException;
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
    public Project saveOrUpdateProject(Project project, String username) {
        try {
            final User user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());

            final String projectIdentifier = project.getProjectIdentifier().toUpperCase();
            project.setProjectIdentifier(projectIdentifier);
            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifier);
            }

            if(project.getId() != null)
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifier).orElse(null));

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier()+"' already exists");
        }
    }

    @Transactional(readOnly = true)
    public Project findByProjectIdentifier(String projectId) {
        final Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if(project == null ) {
            throw new ProjectIdException("Project ID '"+projectId.toUpperCase()+"' does not exist");
        }
        return project;
    }

    @Transactional(readOnly = true)
    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public void deleteProjectIdentifier(String projectId) {
        final Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if(project == null ) {
            throw new ProjectIdException("Cannot delete project with id '"+projectId.toUpperCase()+"'. This project does not exist");
        }
        projectRepository.delete(project);
    }
}
