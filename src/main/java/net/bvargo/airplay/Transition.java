package net.bvargo.airplay;

/**
 * Various transitions for images on an AirPlay.
 */
public enum Transition {
    NONE("None"),
    SLIDE_LEFT("SlideLeft"),
    SLIDE_RIGHT("SlideRight"),
    DISSOLVE("Dissolve");

    private final String name;

    private Transition(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Transition{" + this.name + "}";
    }
}
