package com.vitor.testecedro.model.persistence.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.vitor.testecedro.model.persistence.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericORMLiteDAO<T> extends DatabaseHelper {
    private static final String TAG = "GenericORMLiteDAO";
    protected Dao<T, Object> dao;
    private Class<T> type;

    public interface GenericDaoCallback {
        void onFinished();
    }

    public GenericORMLiteDAO(Context context, Class<T> type) throws SQLException {
        super(context);
        this.type = type;
        this.dao = DaoManager.createDao(getConnectionSource(), (Class) type);
    }

    public void create(T obj) {
        try {
            this.dao.create(obj);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void create(List<T> list) {
        try {
            for (T obj : list) {
                this.dao.create(obj);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void create(final T obj, final GenericDaoCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    GenericORMLiteDAO.this.dao.create(obj);
                    callback.onFinished();
                } catch (SQLException e) {
                    Log.e(GenericORMLiteDAO.TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void create(final List<T> list, final GenericDaoCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    for (T obj : list) {
                        dao.create(obj);
                    }
                    callback.onFinished();
                } catch (SQLException e) {
                    Log.e(GenericORMLiteDAO.TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void createOrUpdate(T obj) {
        try {
            this.dao.createOrUpdate(obj);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void createOrUpdate(List<T> list) {
        try {
            for (T obj : list) {
                this.dao.createOrUpdate(obj);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

//    public void clearbeforeCreateOrUpdateAsync(final List<T> list, final Class type, final GenericDaoCallback callback) {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    TableUtils.clearTable(DatabaseHelper.getInstance().getConnectionSource(), type);
//                    for (T obj : list) {
//                        GenericDao.this.dao.createOrUpdate(obj);
//                    }
//                    callback.onFinished();
//                } catch (SQLException e) {
//                    Log.e(GenericDao.TAG, e.getMessage());
//                }
//            }
//        }).start();
//    }

    public void clearbeforeCreate(List<T> list, Class type) {
        try {
            TableUtils.clearTable(super.getConnectionSource(), type);
            for (T obj : list) {
                this.dao.create(obj);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

//    public void clearbeforeCreateAsync(final List<T> list, final Class type, final GenericDaoCallback callback) {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    TableUtils.clearTable(DatabaseHelper.getInstance().getConnectionSource(), type);
//                    for (T obj : list)
//                        dao.create(obj);
//                    callback.onFinished();
//                } catch (SQLException e) {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        }).start();
//    }

    public T findById(int id) {
        T obj = null;
        try {
            obj = this.dao.queryForId(Integer.valueOf(id));
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        return obj;
    }

    public List<T> findAll() {
        List<T> result = new ArrayList();
        try {
            result = this.dao.queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    public void update(T obj) {
        try {
            this.dao.update(obj);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void delete(T obj) {
        try {
            this.dao.delete(obj);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void delete(List<T> list) {
        try {
            for (T obj : list) {
                this.dao.delete(obj);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteAll() {
        try {
            this.dao.delete(findAll());
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
