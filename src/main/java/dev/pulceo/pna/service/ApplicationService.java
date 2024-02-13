package dev.pulceo.pna.service;

import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.repository.ApplicationComponentRepository;
import dev.pulceo.pna.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {



    private ApplicationRepository applicationRepository;
    private ApplicationComponentRepository applicationComponentRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, ApplicationComponentRepository applicationComponentRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationComponentRepository = applicationComponentRepository;
    }

    // TODO: create service / application
    public Application createApplication(Application application) {
        // TODO: do validation for all components
        return this.applicationRepository.save(application);
    }

    public ApplicationComponent createApplicationComponent(Application application, ApplicationComponent applicationComponent) {
        return this.applicationComponentRepository.save(applicationComponent);
    }






}
