package io.github.jungletian.titanjumnote.listener;

import io.github.jungletian.titanjumnote.data.NoteInfo;

/**
 * Create by JungleTian on 15-8-27 22:35.
 * Email：tjsummery@gmail.com
 */
public interface ItemClickListener {
  /**
   * 某个笔记被点击
   * @param info 笔记信息
   */
  void onItemClick(NoteInfo info);
}
