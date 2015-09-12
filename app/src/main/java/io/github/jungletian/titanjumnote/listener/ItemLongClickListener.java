package io.github.jungletian.titanjumnote.listener;

import io.github.jungletian.titanjumnote.data.NoteInfo;

/**
 * Create by JungleTian on 15-8-27 22:36.
 * Email：tjsummery@gmail.com
 */
public interface ItemLongClickListener {
  /**
   * 某个信息长按点击事件
   * @param info 笔记信息
   */
  void onItemLongClick(NoteInfo info);
}
