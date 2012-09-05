package com.facebook.presto.aggregations;

import com.facebook.presto.Cursor;
import com.facebook.presto.Range;
import com.facebook.presto.Tuple;
import com.facebook.presto.TupleInfo;

import javax.inject.Provider;

public class SumAggregation
        implements AggregationFunction
{
    public static final Provider<AggregationFunction> PROVIDER = new Provider<AggregationFunction>()
    {
        @Override
        public SumAggregation get()
        {
            return new SumAggregation();
        }
    };
    private static final TupleInfo TUPLE_INFO = new TupleInfo(TupleInfo.Type.FIXED_INT_64);

    private long sum;

    @Override
    public TupleInfo getTupleInfo()
    {
        return TUPLE_INFO;
    }

    @Override
    public void add(Cursor cursor, Range relevantRange)
    {
//        cursor.advancePositionTo(relevantRange.getStart());
//        while (cursor.hasNextValue() && relevantRange.contains(cursor.nextValuePosition())) {
//            long value = cursor.getLong(0);
//            long nextPosition = cursor.peekNextValuePosition();
//            long endPosition = Math.min(relevantRange.getEnd(), nextPosition);
//            long size = endPosition - cursor.getPosition();
//            sum += (value * size);
//        }

        // todo if cursor is not "valid", advance to first position

        // advance to start of range
        // todo add seek method to cursor
        while (cursor.getPosition() < relevantRange.getStart()) {
            cursor.advanceNextPosition();
        }

        while (relevantRange.contains(cursor.getPosition())) {
            sum += cursor.getLong(0);
            if (!cursor.hasNextPosition()) {
                break;
            }
            cursor.advanceNextPosition();
        }
    }

    @Override
    public Tuple evaluate()
    {
        return getTupleInfo().builder()
                .append(sum)
                .build();
    }
}
