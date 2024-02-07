package dev.pulceo.pna.util;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class K3sUtils {

    public static void main(String[] args) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1NodeList listofNode = api.listNode(null, null, null, null, null, null, null, null, null, null, false);
        for (V1Node item : listofNode.getItems()) {
            System.out.println(item.getStatus().getAllocatable());
        }
    }

}
