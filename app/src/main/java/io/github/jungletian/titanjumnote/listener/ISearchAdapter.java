package io.github.jungletian.titanjumnote.listener;

import io.github.jungletian.titanjumnote.data.NoteInfo;
import java.util.List;

/**
 * Create by JungleTian on 15-8-28 02:06.
 * Email：tjsummery@gmail.com
 */
public interface ISearchAdapter {
  /**
   * 获取笔记信息
   * @return
   */
  List<NoteInfo> get();
}
