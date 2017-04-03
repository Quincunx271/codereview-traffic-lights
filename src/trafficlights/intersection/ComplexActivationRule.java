package trafficlights.intersection;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkArgument;

class ComplexActivationRule {
    private Collection<Light> triggers;
    private Collection<Light> toActivate;

    public ComplexActivationRule(Collection<Light> triggers,
                                 Collection<Light> toActivate) {
        checkArgument(!triggers.isEmpty());
        checkArgument(!toActivate.isEmpty());
        this.triggers = triggers;
        this.toActivate = toActivate;
    }

    public Collection<Light> getTriggers() {
        return Collections.unmodifiableCollection(triggers);
    }

    public Collection<Light> getToActivate() {
        return Collections.unmodifiableCollection(toActivate);
    }
}
