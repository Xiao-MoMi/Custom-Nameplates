package net.momirealms.customnameplates.requirements;

import net.momirealms.customnameplates.requirements.papi.*;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CustomPapi implements Requirement {

    public static HashSet<String> allPapi = new HashSet<>();

    private PapiRequirement papiRequirement;

    public CustomPapi(Map<String, Object> expressions){
        expressions.keySet().forEach(key -> {
            if (key.startsWith("&&")){
                List<PapiRequirement> papiRequirements = new ArrayList<>();
                if (expressions.get(key) instanceof MemorySection map2){
                    addAndRequirements(papiRequirements, map2.getValues(false));
                }
                papiRequirement = new ExpressionAnd(papiRequirements);
            }
            else if (key.startsWith("||")){
                List<PapiRequirement> papiRequirements = new ArrayList<>();
                if (expressions.get(key) instanceof MemorySection map2){
                    addOrRequirements(papiRequirements, map2.getValues(false));
                }
                papiRequirement = new ExpressionOr(papiRequirements);
            }
            else {
                if (expressions.get(key) instanceof MemorySection map){
                    String type = map.getString("type");
                    String papi = map.getString("papi");
                    String value = map.getString("value");
                    allPapi.add(papi);
                    switch (type){
                        case "==" -> papiRequirement = new PapiEquals(papi, value);
                        case "!=" -> papiRequirement = new PapiNotEquals(papi, value);
                        case ">=" -> papiRequirement = new PapiNoLess(papi, Double.parseDouble(value));
                        case "<=" -> papiRequirement = new PapiNoLarger(papi, Double.parseDouble(value));
                        case "<" -> papiRequirement = new PapiSmaller(papi, Double.parseDouble(value));
                        case ">" -> papiRequirement = new PapiGreater(papi, Double.parseDouble(value));
                    }
                }
            }
        });
    }

    @Override
    public boolean isConditionMet(PlayerCondition playerCondition) {
        return papiRequirement.isMet(playerCondition.getPapiMap());
    }

    private void addAndRequirements(List<PapiRequirement> requirements, Map<String, Object> map){
        List<PapiRequirement> andRequirements = new ArrayList<>();
        map.keySet().forEach(key -> {
            if (key.startsWith("&&")){
                if (map.get(key) instanceof MemorySection map2){
                    addAndRequirements(andRequirements, map2.getValues(false));
                }
            }else if (key.startsWith("||")){
                if (map.get(key) instanceof MemorySection map2){
                    addOrRequirements(andRequirements, map2.getValues(false));
                }
            }else {
                if (map.get(key) instanceof MemorySection map2){
                    String type = map2.getString("type");
                    String papi = map2.getString("papi");
                    String value = map2.getString("value");
                    allPapi.add(papi);
                    switch (type){
                        case "==" -> andRequirements.add(new PapiEquals(papi, value));
                        case "!=" -> andRequirements.add(new PapiNotEquals(papi, value));
                        case ">=" -> andRequirements.add(new PapiNoLess(papi, Double.parseDouble(value)));
                        case "<=" -> andRequirements.add(new PapiNoLarger(papi, Double.parseDouble(value)));
                        case "<" -> andRequirements.add(new PapiSmaller(papi, Double.parseDouble(value)));
                        case ">" -> andRequirements.add(new PapiGreater(papi, Double.parseDouble(value)));
                    }
                }
            }
        });
        requirements.add(new ExpressionAnd(andRequirements));
    }

    private void addOrRequirements(List<PapiRequirement> requirements, Map<String, Object> map){
        List<PapiRequirement> orRequirements = new ArrayList<>();
        map.keySet().forEach(key -> {
            if (key.startsWith("&&")){
                if (map.get(key) instanceof MemorySection map2){
                    addAndRequirements(orRequirements, map2.getValues(false));
                }
            }else if (key.startsWith("||")){
                if (map.get(key) instanceof MemorySection map2){
                    addOrRequirements(orRequirements, map2.getValues(false));
                }
            }else {
                if (map.get(key) instanceof MemorySection map2){
                    String type = map2.getString("type");
                    String papi = map2.getString("papi");
                    String value = map2.getString("value");
                    allPapi.add(papi);
                    switch (type){
                        case "==" -> orRequirements.add(new PapiEquals(papi, value));
                        case "!=" -> orRequirements.add(new PapiNotEquals(papi, value));
                        case ">=" -> orRequirements.add(new PapiNoLess(papi, Double.parseDouble(value)));
                        case "<=" -> orRequirements.add(new PapiNoLarger(papi, Double.parseDouble(value)));
                        case "<" -> orRequirements.add(new PapiSmaller(papi, Double.parseDouble(value)));
                        case ">" -> orRequirements.add(new PapiGreater(papi, Double.parseDouble(value)));
                    }
                }
            }
        });
        requirements.add(new ExpressionOr(orRequirements));
    }
}
