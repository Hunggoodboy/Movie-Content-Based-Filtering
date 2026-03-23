package com.movie.Config;

import org.hibernate.usertype.UserType;

import java.sql.*;
import java.util.Arrays;

public class VectorType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position,
                               org.hibernate.type.descriptor.WrapperOptions options) throws SQLException {
        Object obj = rs.getObject(position);
        if (obj == null) return null;
        String val = obj.toString().replaceAll("[\\[\\]\\s]", "");
        if (val.isEmpty()) return new float[0];

        String[] parts = val.split(",");
        float[] vec = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vec[i] = Float.parseFloat(parts[i]);
        }
        return vec;
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, float[] value, int index,
                            org.hibernate.type.descriptor.WrapperOptions options) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.OTHER);
            return;
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < value.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(value[i]);
        }
        sb.append("]");

        // Dùng PGobject để PostgreSQL hiểu đúng kiểu vector
        org.postgresql.util.PGobject pgObject = new org.postgresql.util.PGobject();
        pgObject.setType("vector");
        pgObject.setValue(sb.toString());
        ps.setObject(index, pgObject);
    }

    @Override
    public float[] deepCopy(float[] value) {
        if (value == null) return null;
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public boolean equals(float[] x, float[] y) {
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(float[] x) {
        return Arrays.hashCode(x);
    }
}