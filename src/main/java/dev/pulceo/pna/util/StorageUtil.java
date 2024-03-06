package dev.pulceo.pna.util;

import dev.pulceo.pna.model.node.Storage;

import java.util.List;

public class StorageUtil {

    public static Storage extractStorageInformation(List<String> strings) {
        String splittedString = strings.get(1).split("\\s+")[1];
        String totalCapacity = splittedString.split("G")[0];
        return Storage.builder()
                .size(Float.parseFloat(totalCapacity))
                .slots(0)
                .build();
    }
}
