package akil.co.tz.mzikii.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import akil.co.tz.mzikii.Daos.PostDao;
import akil.co.tz.mzikii.models.Post;

/**
 * Created by DevDept on 6/14/18.
 */

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract PostDao postDao();
}
