package io.github.jungletian.titanjumnote.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
@Table(name = "NoteInfos") public class NoteInfo extends Model {

  /** 标题 */
  @Column(name = "title") public long title;
  /** 内容 */
  @Column(name = "content") public String content;

  public NoteInfo() {
    super();
  }

  public NoteInfo(long title, String content) {
    super();
    this.title = title;
    this.content = content;
  }

  public long getTitle() {
    return title;
  }

  public void setTitle(long title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
