package io.github.jungletian.titanjumnote.util;

import com.activeandroid.query.Select;
import io.github.jungletian.titanjumnote.data.NoteInfo;
import java.util.List;

/**
 * Create by JungleTian on 15-8-27 00:11.
 * Email：tjsummery@gmail.com
 */
public class DBHelper {

  /**
   * 获取所有笔记
   * @return 所有笔记
   */
  public static List<NoteInfo> getAll() {
    return new Select().from(NoteInfo.class).orderBy("title DESC").execute();
  }

  /**
   * 通过某个字段进行搜索
   * @param required 条件
   * @return 查到的笔记
   */
  public static List<NoteInfo> search(String required) {
    return new Select().from(NoteInfo.class).where("content = ?", required).execute();
  }

  /**
   * 删除笔记
   * @param info 笔记信息
   */
  public static void delete(NoteInfo info) {
    info.delete(NoteInfo.class, 1);
  }
}
