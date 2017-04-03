package trafficlights.intersection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class IntersectionBuilder {
    private final List<Light> lights = new ArrayList<>();
    private final RuleBuilder activationRules = new RuleBuilder();

    IntersectionBuilder() {
    }

    public IntersectionBuilder addLight(Light light) {
        lights.add(light);
        return this;
    }

    public IntersectionBuilder addLights(Light... lights) {
        return addAllLights(Arrays.asList(lights));
    }

    public IntersectionBuilder addLights(int n, Supplier<Light> lightSupplier) {
        IntStream.range(0, n)
                .mapToObj(i -> lightSupplier.get())
                .forEach(lights::add);

        return this;
    }

    public IntersectionBuilder addAllLights(List<? extends Light> lights) {
        this.lights.addAll(lights);

        return this;
    }

    public IntersectionBuilder2 setActivationRules(BiConsumer<RuleBuilder, List<Light>> ruleBuilder) {
        ruleBuilder.accept(activationRules, lights);

        return new IntersectionBuilder2();
    }

    public class IntersectionBuilder2 {
        public IntersectionBuilder3 activate(int... lightIndices) {
            Arrays.stream(lightIndices)
                    .mapToObj(lights::get)
                    .forEach(Light::activate);

            return new IntersectionBuilder3();
        }
    }

    public class IntersectionBuilder3 {
        public Intersection build() {
            return new Intersection(lights, activationRules.build());
        }
    }
}
