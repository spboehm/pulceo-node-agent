package dev.pulceo.pna.model.resources;

public enum ResourceUtilizationType {
    CPU_UTIL, MEM_UTIL, NET_UTIL, STORAGE_UTIL, UNDEFINED;
    
    public static String getName(ResourceUtilizationType resourceUtilizationType) {
        return switch (resourceUtilizationType) {
            case CPU_UTIL -> "cpu-util";
            case MEM_UTIL -> "mem-util";
            case NET_UTIL -> "net-util";
            case STORAGE_UTIL -> "storage-util";
            default -> "";
        };
    }

    public static ResourceUtilizationType getTypeFromString(String resourceUtilizationType) {
        return switch (resourceUtilizationType) {
            case "cpu-util" -> CPU_UTIL;
            case "mem-util" -> MEM_UTIL;
            case "net-util" -> NET_UTIL;
            case "storage-util" -> STORAGE_UTIL;
            default -> UNDEFINED;
        };
    }
}
