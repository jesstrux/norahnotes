package akil.co.tz.notetaker.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import akil.co.tz.notetaker.Daos.PostDao;
import akil.co.tz.notetaker.models.Post;

/**
 * Created by DevDept on 6/14/18.
 */

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract PostDao postDao();
}
