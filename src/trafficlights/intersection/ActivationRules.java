package trafficlights.intersection;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

class ActivationRules {
    private Map<Light, Collection<Light>> simpleRules;
    private Map<Light, Collection<ComplexActivationRule>> complexRules; // The keys are the triggers.

    public ActivationRules(IdentityHashMap<Light, Collection<Light>> simpleRules,
                           IdentityHashMap<Light, Collection<ComplexActivationRule>> complexRules) {
        this.simpleRules = simpleRules;
        this.complexRules = complexRules;
    }

    Map<Light, Collection<Light>> getSimpleRules() {
        return Collections.unmodifiableMap(simpleRules);
    }

    Map<Light, Collection<ComplexActivationRule>> getComplexRules() {
        return Collections.unmodifiableMap(complexRules);
    }
}
