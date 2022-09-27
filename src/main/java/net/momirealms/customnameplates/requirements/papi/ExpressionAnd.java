package net.momirealms.customnameplates.requirements.papi;

import java.util.HashMap;
import java.util.List;

public record ExpressionAnd(List<PapiRequirement> requirements) implements PapiRequirement{

    @Override
    public boolean isMet(HashMap<String, String> papiMap) {
        for (PapiRequirement requirement : requirements) {
            if (!requirement.isMet(papiMap)) return false;
        }
        return true;
    }
}