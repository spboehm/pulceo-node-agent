package dev.pulceo.pna.model;

import lombok.ToString;

import java.util.UUID;


@ToString
public abstract class Resource {

    private final UUID uuid = UUID.randomUUID();

}
