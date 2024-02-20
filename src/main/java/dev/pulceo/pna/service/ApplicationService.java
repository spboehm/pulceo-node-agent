package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ApplicationServiceException;
import dev.pulceo.pna.exception.KubernetesServiceException;
import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.repository.ApplicationComponentRepository;
import dev.pulceo.pna.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationComponentRepository applicationComponentRepository;
    private final KubernetesService kubernetesService;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, ApplicationComponentRepository applicationComponentRepository, KubernetesService kubernetesService) {
        this.applicationRepository = applicationRepository;
        this.applicationComponentRepository = applicationComponentRepository;
        this.kubernetesService = kubernetesService;
    }

    public Application createApplication(Application application) throws ApplicationServiceException {


        // TODO: check for duplicates
        // TODO: check for port collisions

        Application persistedApplication = this.applicationRepository.save(application);

        // then add the components
        for (ApplicationComponent applicationComponent : persistedApplication.getApplicationComponents()) {
            try {
                this.createApplicationComponent(persistedApplication, applicationComponent);
            } catch (ApplicationServiceException e) {
                throw new ApplicationServiceException("Could not create application!", e);
            }
        }
        // TODO: do validation for all components
        return persistedApplication;
    }

    public ApplicationComponent createApplicationComponent(Application application, ApplicationComponent applicationComponent) throws ApplicationServiceException {
        Optional<Application> persistedApplication = this.applicationRepository.findByName(application.getName());
        if (persistedApplication.isEmpty()) {
            throw new ApplicationServiceException(String.format("Application %s not found", application.getName()));
        }
        try {
            this.kubernetesService.createDeployment(persistedApplication.get().getName(), applicationComponent);
            this.kubernetesService.createService(persistedApplication.get().getName(), applicationComponent);
        } catch (KubernetesServiceException e) {
            throw new ApplicationServiceException(e);
        }
        return this.applicationComponentRepository.save(applicationComponent);
    }

}
