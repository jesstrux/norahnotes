package akil.co.tz.notetaker.Daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import akil.co.tz.notetaker.models.Post;

/**
 * Created by DevDept on 6/14/18.
 */

@Dao
public interface PostDao {
    @Query("SELECT * FROM post")
    List<Post> getPosts();

    @Insert
    long insert(Post post);

    @Update
    public void updatePost(Post post);

    @Insert
    long[] insertAll(Post... posts);
}
