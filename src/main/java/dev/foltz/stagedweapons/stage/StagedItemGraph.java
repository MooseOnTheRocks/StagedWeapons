package dev.foltz.stagedweapons.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StagedItemGraph<T extends IStagedItem<?>> {
    private final Map<String, Stage<T>> stages;

    public StagedItemGraph(List<Stage<T>> stages) {
        this.stages = new HashMap<>();
        for (var stage : stages) {
            this.stages.put(stage.name, stage);
        }
    }

    public Stage<T> getStage(String name) {
        var stage = stages.get(name);
        if (stage == null) {
            return stages.get("default");
        }
        return stages.get(name);
    }

    public static <T extends IStagedItem<?>> StagedItemGraph.Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends IStagedItem<?>> {
        private final List<Stage<T>> stages;

        public Builder() {
            this.stages = new ArrayList<>();
        }

        public Builder<T> withStage(Stage<T> stage) {
            stages.add(stage);
            return this;
        }

        public Builder<T> withStage(Stage.Builder<T> stage) {
            return withStage(stage.build());
        }

        public StagedItemGraph<T> build() {
            return new StagedItemGraph<>(stages);
        }
    }
}
