package com.anilmane.Smart.App.Review.Responder.Config;

import com.pgvector.PGvector;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;

public class PGVectorType implements UserType<PGvector> {

    @Override
    public int getSqlType() {
        return Types.OTHER; // PostgreSQL's "vector" is an "OTHER" type in JDBC
    }

    @Override
    public Class<PGvector> returnedClass() {
        return PGvector.class;
    }

    @Override
    public PGvector nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String vectorString = rs.getString(position);
        return vectorString == null ? null : new PGvector(vectorString);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PGvector value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            PGobject pgObject = new PGobject();
            pgObject.setType("vector");
            pgObject.setValue(value.toString()); // PGvector has a proper toString format
            st.setObject(index, pgObject);
        }
    }

    @Override
    public PGvector deepCopy(PGvector value) {
        return value == null ? null : new PGvector(Arrays.copyOf(value.toArray(), value.toArray().length));
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(PGvector value) {
        return deepCopy(value);
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        return deepCopy((PGvector) cached);
    }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(PGvector x) {
        return x.hashCode();
    }
}
