package trafficlights.intersection;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles the state of a 4-way intersection
 * (1 second)
 * Speed	Perception/Reaction Distance	Braking Distance	Overall Stopping Distance
 * 30 mph            44 feet                    45 feet                  89 feet
 * 40 mph            59 feet                    80 feet                  139 feet
 * 50 mph            73 feet                    125 feet                 198 feet
 * 60 mph            88 feet                    180 feet                 268 feet
 * 70 mph            103 feet                   245 feet                 348 feet
 * 80 mph            117 feet                   320 feet                 439 feet
 */
public class Intersection {

    private final List<Light> lights;
    private final ActivationRules activationRules;

    Intersection(List<Light> lights,
                 ActivationRules activationRules) {
        this.lights = ImmutableList.copyOf(lights);
        this.activationRules = activationRules;
    }

    public void tick(double duration) {
        List<Light> deactivated = new ArrayList<>();

        lights.stream()
                .filter(Light::isActive)
                .forEach(light -> {
                    light.tick(duration);
                    // Note: do not handle activating lights here; that is concurrent modification and
                    // will cause bugs (if a light is activated that is further in the list, it will have its tick
                    // function run immediately, whereas if it was prior to this light, the activated light won't have
                    // its tick function run).
                    if (!light.isActive()) {
                        deactivated.add(light);
                    }
                });

        activateTriggered(deactivated);
    }

    private void activateTriggered(List<Light> deactivated) {
        // Follow simple activation rules
        deactivated.stream()
                .filter(activationRules.getSimpleRules()::containsKey)
                .map(activationRules.getSimpleRules()::get)
                .flatMap(Collection::stream)
                .forEach(Light::activate);

        // Follow complex activation rules
        deactivated.stream()
                .filter(activationRules.getComplexRules()::containsKey)
                .map(activationRules.getComplexRules()::get)
                .flatMap(Collection::stream)
                .filter(rule -> rule.getTriggers().stream()
                        .noneMatch(Light::isActive))
                .map(ComplexActivationRule::getToActivate)
                .flatMap(Collection::stream)
                .forEach(Light::activate);
    }

    public Light.Color getColor(int i) {
        return lights.get(i).getColor();
    }

    public Stream<Light.Color> getLightColors() {
        return lights.stream()
                .map(Light::getColor);
    }

    public static IntersectionBuilder builder() {
        return new IntersectionBuilder();
    }
}
