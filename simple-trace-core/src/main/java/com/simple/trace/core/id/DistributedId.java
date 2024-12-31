package com.simple.trace.core.id;

import lombok.Getter;

import java.util.Objects;

@Getter
public class DistributedId {
    private final String id;

    public DistributedId() {
        this.id = GlobalIdGenerator.generate();
    }

    public DistributedId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DistributedId)) {
            return false;
        }
        DistributedId that = (DistributedId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
