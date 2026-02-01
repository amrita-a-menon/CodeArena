package com.codearena.persistence;

import com.codearena.models.UserProfile;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(
        name = "CodeArenaUserProgress",
        storages = @Storage(StoragePathMacros.WORKSPACE_FILE)
)
public final class UserProgress implements PersistentStateComponent<UserProfile> {
    private UserProfile state = new UserProfile();

    public static UserProgress getInstance(Project project) {
        return project.getService(UserProgress.class);
    }

    public static UserProfile loadProfile(Project project) {
        UserProgress service = getInstance(project);
        if (service.state == null) {
            service.state = new UserProfile();
        }
        if (service.state.getUserId() == null || service.state.getUserId().isBlank()) {
            service.state.setUserId(System.getProperty("user.name", "player"));
        }
        return service.state;
    }

    public static void saveProfile(Project project, UserProfile profile) {
        UserProgress service = getInstance(project);
        service.state = profile;
        project.getMessageBus()
                .syncPublisher(UserProgressListener.TOPIC)
                .progressUpdated(profile);
    }

    @Override
    public UserProfile getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull UserProfile state) {
        this.state = state;
    }
}
