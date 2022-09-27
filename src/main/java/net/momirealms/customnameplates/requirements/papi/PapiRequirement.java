package net.momirealms.customnameplates.requirements.papi;

import java.util.HashMap;

public interface PapiRequirement {
    boolean isMet(HashMap<String, String> papiMap);
}
