package dev.foltz.stagedweapons.stage;

@FunctionalInterface
public interface StagedItemEventHandler<T extends IStagedItem<?>> {
    String handleEvent(StagedItemView<? extends T> view);

    default StagedItemEventHandler<T> then(StagedItemEventHandler<T> next) {
        return view -> {
            String stage = handleEvent(view);
            if (stage.isEmpty()) {
                return next.handleEvent(view);
            }
            return stage;
        };
    }

    default StagedItemEventHandler<T> then(String next) {
        return then(view -> next);
    }
}
