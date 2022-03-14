package gr.cognitera.util.adql;

import java.util.Objects;

import com.google.common.collect.Range;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import gr.cognitera.util.base.Maybe;


public class ContiguousRAConstraint {


    public final Range<Double>        raBandA;
    public final Maybe<Range<Double>> raBandB;

    private ContiguousRAConstraint(){
        this.raBandA = null;
        this.raBandB = null;
        throw new UnsupportedOperationException("this constructor is not supposed to ever be called");
    }
    
    private ContiguousRAConstraint(final Range<Double> raBandA,
                                   final Maybe<Range<Double>> raBandB) {
        this.raBandA = raBandA;
        this.raBandB = raBandB;
    }

    public static ContiguousRAConstraint singleBand(final Range<Double> raBandA) {
        return new ContiguousRAConstraint(raBandA, new Maybe<Range<Double>>(null));
    }
    
    public static ContiguousRAConstraint dualBand(final Range<Double> raBandA, final Range<Double> raBandB) {
        return new ContiguousRAConstraint(raBandA, new Maybe<Range<Double>>(raBandB));
    }

    public String adqlCondition(final String raColumn) {
        if (!raBandB.isPresent()) {
            return RangeUtil.rangeCondition(raColumn, raBandA);
        } else {
            return String.format("(%s) OR (%s)"
                                 , RangeUtil.rangeCondition(raColumn, raBandA)
                                 , RangeUtil.rangeCondition(raColumn, raBandB.value));
        }
    }


    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof ContiguousRAConstraint))
            return false;
 
        final ContiguousRAConstraint other = (ContiguousRAConstraint) o ;
        return Objects.equals(raBandA, other.raBandA) &&
            Objects.equals(raBandB, other.raBandB) ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(raBandA, raBandB);
    }
    
    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("raBandA", raBandA)
            .add("raBandB", raBandB)
            ;
    }

}
