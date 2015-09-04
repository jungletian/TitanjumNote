package io.github.jungletian.titanjumnote.util;

import com.activeandroid.query.Select;

import java.util.List;

import io.github.jungletian.titanjumnote.data.NoteInfo;

/**
 * Create by JungleTian on 15-8-27 00:11.
 * Email：tjsummery@gmail.com
 */
public class DBHelper {

    public static List<NoteInfo> getAll() {
        return new Select()
                .from(NoteInfo.class)
                .orderBy("title DESC")
                .execute();
    }

    public static List<NoteInfo> search(String required){
        return new Select()
                .from(NoteInfo.class)
                .where("content = ?",required)
                .execute();

    }

    public static void delete(NoteInfo info){
        info.delete(NoteInfo.class, 1);
    }

}
