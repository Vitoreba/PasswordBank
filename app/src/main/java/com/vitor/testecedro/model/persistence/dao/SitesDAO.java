package com.vitor.testecedro.model.persistence.dao;

import android.content.Context;
import android.util.Log;

import com.vitor.testecedro.model.Site;

import java.sql.SQLException;

public class SitesDAO extends GenericORMLiteDAO {

    private static final String TAG = "SitesDAO";
    private static SitesDAO mInstance;

    public SitesDAO(Context context) throws SQLException {
        super(context, Site.class);
    }

    public static SitesDAO getInstance(Context context) {
        if (mInstance == null) {
            try {
                mInstance = new SitesDAO(context);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return mInstance;
    }
}
