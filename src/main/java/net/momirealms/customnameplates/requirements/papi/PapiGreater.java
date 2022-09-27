package net.momirealms.customnameplates.requirements.papi;

import java.util.HashMap;

public record PapiGreater(String papi, double requirement) implements PapiRequirement{

    @Override
    public boolean isMet(HashMap<String, String> papiMap) {
        double value = Double.parseDouble(papiMap.get(papi));
        return value > requirement;
    }
}
