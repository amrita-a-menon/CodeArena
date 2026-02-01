package com.codearena.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(
        name = "CodeArenaSettings",
        storages = @Storage("codearena.xml")
)
public class SettingsManager implements PersistentStateComponent<SettingsManager.State> {
    public static class State {
        public String aiAdapterType = "MOCK"; // Start with MOCK for testing
        public String openAIKey = "";
        public String localLLMUrl = "http://localhost:8080";
    }

    private State state = new State();

    public static SettingsManager getInstance() {
        return ApplicationManager.getApplication().getService(SettingsManager.class);
    }

    public static String getAIAdapterType() {
        return getInstance().state.aiAdapterType;
    }

    public static String getOpenAIKey() {
        return getInstance().state.openAIKey;
    }

    public static void setOpenAIKey(String key) {
        getInstance().state.openAIKey = key;
    }

    public static void setAIAdapterType(String type) {
        getInstance().state.aiAdapterType = type;
    }

    public static String getLocalLLMUrl() {
        return getInstance().state.localLLMUrl;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}