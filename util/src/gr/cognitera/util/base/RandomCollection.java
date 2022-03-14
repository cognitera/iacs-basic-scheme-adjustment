package gr.cognitera.util.base;

import java.util.Random;
import java.util.List;

public final class RandomCollection<T> {

    private final List<T> data;
    private final Random random;


    public RandomCollection(final List<T> data, final long seed) {
        this.data = data;
        this.random = new Random();
        this.random.setSeed(seed);
    }

    public T randomElement() {
        return randomElement(this.data, this.random);
    }

    public static <T> T randomElement(final List<T> data, final Random r) {
        if (data.isEmpty())
            return null;
        else
            return data.get(r.nextInt(data.size()));
    }
}
