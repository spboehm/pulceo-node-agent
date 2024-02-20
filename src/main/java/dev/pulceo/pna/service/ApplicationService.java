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
        if (this.isApplicationAlreadyExisting(application.getName())) {
            throw new ApplicationServiceException(String.format("Application %s already exists", application.getName()));
        }

        Application persistedApplicationWithoutServices = this.applicationRepository.save(application);
        // then add the components
        for (ApplicationComponent applicationComponent : application.getApplicationComponents()) {
            try {
                this.createApplicationComponent(persistedApplicationWithoutServices, applicationComponent);
            } catch (ApplicationServiceException e) {
                throw new ApplicationServiceException("Could not create application!", e);
            }
        }
        // TODO: do validation for all components
        return persistedApplicationWithoutServices;
    }

    public ApplicationComponent createApplicationComponent(Application application, ApplicationComponent applicationComponent) throws ApplicationServiceException {
        Optional<Application> persistedApplication = this.applicationRepository.findByName(application.getName());
        if (persistedApplication.isEmpty()) {
            throw new ApplicationServiceException(String.format("Application %s not found", application.getName()));
        }

        if (this.isApplicationComponentAlreadyExisting(applicationComponent.getName()) || isPortAlreadyInUse(applicationComponent.getPort())) {
            throw new ApplicationServiceException(String.format("ApplicationComponent %s already exists", applicationComponent.getName()));
        }

        try {
            this.kubernetesService.createDeployment(persistedApplication.get().getName(), applicationComponent);
            this.kubernetesService.createService(persistedApplication.get().getName(), applicationComponent);
        } catch (KubernetesServiceException e) {
            throw new ApplicationServiceException(e);
        }

        // persis
        ApplicationComponent savedApplicationComponent = this.applicationComponentRepository.save(applicationComponent);
        persistedApplication.get().getApplicationComponents().add(savedApplicationComponent);

        return savedApplicationComponent;
    }

    private boolean isApplicationAlreadyExisting(String name) {
        return this.applicationRepository.findByName(name).isPresent();
    }

    private boolean isApplicationComponentAlreadyExisting(String name) {
        return this.applicationComponentRepository.findByName(name).isPresent();
    }

    private boolean isPortAlreadyInUse(int port) {
        return this.applicationComponentRepository.findByPort(port).isPresent();
    }

}
