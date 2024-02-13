package dev.pulceo.pna.service;

import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.repository.ApplicationComponentRepository;
import dev.pulceo.pna.repository.ApplicationRepository;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
@Service
public class ApplicationService {

    @Value("${k3s.config.path}")
    private String k3sConfigPath;

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




    @PostConstruct
    private void init() throws IOException, ApiException {
        try {
            File file = new File(k3sConfigPath);
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();

            // TODO: list namespaces

            // TODO: check if namespace PULCEO does exist, otherwise create
            V1ServiceList v1ServiceList = api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null, false);
            for (V1Service s : v1ServiceList.getItems()) {
                System.out.println(s.getMetadata().getName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

}
