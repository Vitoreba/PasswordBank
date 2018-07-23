package com.vitor.testecedro.model.persistence.dao;

import android.content.Context;
import android.util.Log;

import com.vitor.testecedro.model.Person;

import java.sql.SQLException;

public class PersonDAO extends GenericORMLiteDAO {
    private static final String TAG = "PersonDAO";
    private static PersonDAO mInstance;

    public PersonDAO(Context context) throws SQLException {
        super(context, Person.class);
    }

    public static PersonDAO getInstance(Context context) {
        if (mInstance == null) {
            try {
                mInstance = new PersonDAO(context);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return mInstance;
    }

    public Person findByEmail(String email) {
        Person obj = null;
        try {
            obj = (Person) this.dao.queryBuilder().where()
                    .eq("email", email).queryForFirst();
        } catch (SQLException e) {
//            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return obj;
    }

}
