    package trafficlights;

    import trafficlights.intersection.Intersection;
    import trafficlights.intersection.Light;

    public class Runner {
        public static void main(String[] args) {
            runSimpleFourWay();
            System.out.println();

            runSimpleFourWayWithLeftTurns();
            System.out.println();

            runComplexFourWay();
        }

        private static void runSimpleFourWay() {
            Intersection intersection = Intersection.builder()
                    .addLights(2, () -> new Light(4, 1, 0))
                    .setActivationRules((rules, lights) -> {
                        rules.after(lights.get(0)).thenActivate(lights.get(1));
                        rules.after(lights.get(1)).thenActivate(lights.get(0));
                    }).activate(0)
                    .build();

            System.out.println(" Time |  EW ^  |  NS ^ ");
            System.out.println("------------------------");

            runIntersection(intersection, 1, 20);
        }

        private static void runSimpleFourWayWithLeftTurns() {
            Intersection intersection = Intersection.builder()
                    .addLights(4, () -> new Light(4, 1, 0))
                    .setActivationRules((rules, lights) -> {
                        for (int i = 0; i < lights.size(); i++) {
                            rules.after(lights.get(i)).thenActivate(lights.get((i + 1) % lights.size()));
                        }
                    }).activate(0)
                    .build();

            System.out.println(" Time |  EW ^  |  NS <  |  NS ^  |  EW <  ");
            System.out.println("-------------------------------------------");

            runIntersection(intersection, 1, 40);
        }

        private static void runComplexFourWay() {
            Intersection intersection = Intersection.builder()
                    .addLight(new Light(3, 1, 1))
                    .addLight(new Light(4, 2, 1))
                    .addLights(6, () -> new Light(4, 1, 1))
                    .setActivationRules((rules, lights) -> {
                        rules.after(lights.get(0)).thenActivate(lights.get(3));
                        rules.after(lights.get(1)).thenActivate(lights.get(2));
                        rules.afterAll(lights.get(2), lights.get(3))
                                .thenActivate(lights.get(4), lights.get(5));

                        rules.after(lights.get(4)).thenActivate(lights.get(7));
                        rules.after(lights.get(5)).thenActivate(lights.get(6));
                        rules.afterAll(lights.get(6), lights.get(7))
                                .thenActivate(lights.get(0), lights.get(1));
                    }).activate(0, 1)
                    .build();

            System.out.println(" Time |  N <   |  S <   |  N ^   |  S ^   |  E <   | W <   |  E ^   |  W ^   ");
            System.out.println("-----------------------------------------------------------------------------");

            runIntersection(intersection, 1, 48);
        }

        private static void runIntersection(Intersection intersection, int tick, int duration) {
            for (int seconds = 0; seconds < duration; seconds++) {
                System.out.printf(" %3ds", seconds);
                intersection.getLightColors()
                        .forEach(color -> System.out.printf(" | %6s", color));
                System.out.println();
                intersection.tick(tick);
            }
        }
    }
