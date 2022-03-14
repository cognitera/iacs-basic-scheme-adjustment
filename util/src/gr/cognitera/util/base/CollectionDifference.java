package gr.cognitera.util.base;

import java.util.Objects;

import java.util.List;

public final class CollectionDifference<A, B, AB> {

    public final List<A> inAnotinB;
    public final List<B> inBnotinA;
    public final List<AB> inBoth;

    public CollectionDifference(final List<A> inAnotinB
                                , final List<B> inBnotinA
                                , final List<AB> inBoth) {
        this.inAnotinB = inAnotinB;
        this.inBnotinA = inBnotinA;
        this.inBoth = inBoth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inAnotinB, inBnotinA, inBoth);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CollectionDifference other = (CollectionDifference) obj;
        return
            Objects.equals(this.inAnotinB, other.inAnotinB) &&
            Objects.equals(this.inBnotinA, other.inBnotinA) &&
            Objects.equals(this.inBoth   , other.inBoth)
            ;
    }
    

}

