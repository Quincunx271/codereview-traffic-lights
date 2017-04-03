package trafficlights.intersection;

import java.util.*;

public class RuleBuilder {
    private final IdentityHashMap<Light, Collection<Light>> simpleRules = new IdentityHashMap<>();
    private final IdentityHashMap<Light, Collection<ComplexActivationRule>> complexRules = new IdentityHashMap<>();

    RuleBuilder() {
    }

    public RuleBuilder2 after(Light light) {
        return new RuleBuilder2(light);
    }

    public RuleBuilder2 afterAll(Light... lights) {
        return new RuleBuilder2(lights);
    }

    ActivationRules build() {
        return new ActivationRules(simpleRules, complexRules);
    }

    public class RuleBuilder2 {
        private final Collection<Light> triggers;

        private RuleBuilder2(Light... triggers) {
            this.triggers = new ArrayList<>(Arrays.asList(triggers));
        }

        public void thenActivate(Light... lights) {
            thenActivate(Arrays.asList(lights));
        }

        public void thenActivate(Collection<Light> lights) {
            lights = new ArrayList<>(lights);

            if (triggers.size() == 1) {
                // simple rule
                simpleRules.merge(triggers.iterator().next(), lights, (existing, toAdd) -> {
                    existing.addAll(toAdd);
                    return existing;
                });
            } else {
                // complex rule
                ComplexActivationRule rule = new ComplexActivationRule(triggers, lights);

                for (Light trigger : triggers) {
                    List<ComplexActivationRule> newRule = new ArrayList<>();
                    newRule.add(rule);

                    complexRules.merge(trigger, newRule, (oldRules, newRules) -> {
                        oldRules.addAll(newRules);
                        return oldRules;
                    });
                }
            }
        }
    }
}
