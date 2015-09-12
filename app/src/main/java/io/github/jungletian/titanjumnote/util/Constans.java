package io.github.jungletian.titanjumnote.util;

import com.evernote.client.android.EvernoteSession;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
public class Constans {

  /** 新建笔记请求码 */
  public static final int WRITE_NOTE_REQUEST_CODE = 0x001;
  /** 修改笔记请求码 */
  public static final int MODIFY_NOTE_REQUEST_CODE = 0x002;
  /** 当前内容 */
  public static final String CURRENT_CONTENT = "current_content";
  /** 写笔记 */
  public static final String WRITE_NOTE_DATA = "write_note_data";

  public static final String Evernote_Consumer_Key = "tjsummery";
  public static final String Evernote_Consumer_Secret = "2fd34509c53fd061";
  public static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
}
