package dev.pulceo.pna.service;

import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.repository.ApplicationComponentRepository;
import dev.pulceo.pna.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // TODO: create service / application
    public Application createApplication(Application application) {
        // TOOD: look at the application components and create the k8s resources


        System.out.println(application);


        // TODO: do validation for all components
        return this.applicationRepository.save(application);
    }

    public ApplicationComponent createApplicationComponent(Application application, ApplicationComponent applicationComponent) {
        return this.applicationComponentRepository.save(applicationComponent);
    }

}
