package dev.pulceo.pna.dto.application;

import dev.pulceo.pna.model.application.ApplicationComponentType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CreateNewApplicationComponentDTO {

    private String name;
    private String image;
    private int port;
    private String protocol;
    private ApplicationComponentType applicationComponentType;
    private Map<String, String> environmentVariables = new HashMap<>();

}
