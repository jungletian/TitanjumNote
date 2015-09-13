package io.github.jungletian.titanjumnote.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.evernote.client.android.EvernoteSession;
import com.umeng.analytics.MobclickAgent;
import io.github.jungletian.titanjumnote.R;
import io.github.jungletian.titanjumnote.adapter.AdapterType;
import io.github.jungletian.titanjumnote.adapter.NoteAdapter;
import io.github.jungletian.titanjumnote.data.NoteInfo;
import io.github.jungletian.titanjumnote.listener.ISearchAdapter;
import io.github.jungletian.titanjumnote.listener.ItemClickListener;
import io.github.jungletian.titanjumnote.listener.ItemLongClickListener;
import io.github.jungletian.titanjumnote.util.Constans;
import io.github.jungletian.titanjumnote.util.DBHelper;
import io.github.jungletian.titanjumnote.util.NavigationDrawerFragment;
import io.github.jungletian.titanjumnote.view.SwipeBackFrameLayout;
import java.util.Date;
import java.util.List;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
public class HomeActivity extends AppCompatActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  /** 创建笔记按钮 */
  @Bind(R.id.fab) FloatingActionButton fab;

  /** RV */
  @Bind(R.id.container) RecyclerView mRecyclerView;

  @Bind(R.id.swipe_back) SwipeBackFrameLayout swipeBackFrameLayout;

  /** 笔记信息 */
  private NoteInfo selectInfo;
  /** 适配器 */
  private NoteAdapter noteAdapter;
  /** 搜索 */
  private SearchView searchView;
  /** RV的LayoutManager */
  private StaggeredGridLayoutManager layoutManager;
  /** 存储 */
  private SharedPreferences sharedPreferences;

  private EvernoteSession mEvernoteSession;

  @Override protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    // 绑定
    ButterKnife.bind(this);
    // 数据
    initData();

    // TODO 二期加入
    //initEvernote();
    // 适配器
    setupNoteAdapter();
    setupActionBar();
    setSearchViewListener();
    swipeBackFrameLayout.setCallBack(new SwipeBackFrameLayout.CallBack() {
      @Override public void onShouldFinish() {
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.out_to_right);
      }
    });

    //UmengUpdateAgent.update(this);
  }

  @Override protected void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);
  }

  @Override protected void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }

  private void initEvernote() {
    mEvernoteSession = new EvernoteSession.Builder(this)
        .setEvernoteService(Constans.EVERNOTE_SERVICE)
        .setSupportAppLinkedNotebooks(true)
        .build(Constans.Evernote_Consumer_Key, Constans.Evernote_Consumer_Secret)
        .asSingleton();
  }

  /**
   * 初始化ActionBar
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(true);

    actionBar.setCustomView(R.layout.search_view);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setTitle(R.string.my_note);
    searchView = (SearchView) ((LinearLayout) actionBar.getCustomView()).getChildAt(0);
  }

  /**
   * 去写笔记页面
   */
  @OnClick(R.id.fab) public void goWriteNote() {
    startActivityForResult(new Intent(HomeActivity.this, WriteNoteActivity.class),
        Constans.WRITE_NOTE_REQUEST_CODE);
  }

  /**
   * 初始化数据
   */
  private void initData() {
    sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
    isSingle = sharedPreferences.getBoolean("isSingle", false);
  }

  /**
   * 初始化适配器
   */
  private void setupNoteAdapter() {
    layoutManager =
        new StaggeredGridLayoutManager(isSingle ? 1 : 2, StaggeredGridLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(layoutManager);
    noteAdapter = new NoteAdapter(this);
    noteAdapter.setItemClickListener(new ItemClickListener() {
      @Override public void onItemClick(NoteInfo info) {
        selectInfo = info;
        Intent intent = new Intent(HomeActivity.this, WriteNoteActivity.class);
        intent.putExtra(Constans.CURRENT_CONTENT, info.getContent());
        startActivityForResult(intent, Constans.MODIFY_NOTE_REQUEST_CODE);
      }
    });

    noteAdapter.setItemLongClickListener(new ItemLongClickListener() {
      @Override public void onItemLongClick(NoteInfo info) {
        selectInfo = info;
        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this,
            R.style.Base_Theme_AppCompat_Light_Dialog_Alert).create();
        dialog.setTitle("确定删除吗");
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
          }
        });
        dialog.setButton(Dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            selectInfo.delete();
            notifyDataChanged();
          }
        });
        dialog.show();
      }
    });
    mRecyclerView.setAdapter(noteAdapter);
  }

  /**
   * 设置搜索View 的监听
   */
  private void setSearchViewListener() {
    // 关闭按钮监听
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override public boolean onClose() {
        noteAdapter.setDataAndType(AdapterType.NOTE_TYPE, null);
        return false;
      }
    });
    // 搜索文字监听
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        final List<NoteInfo> infos = DBHelper.getAll();

        int size = infos.size();
        for (int i = size - 1; i >= 0; i--) {
          String content = infos.get(i).getContent();
          if (!content.contains(newText)) {
            infos.remove(i);
          }
        }

        noteAdapter.setDataAndType(AdapterType.SEARCH_TYPE, new ISearchAdapter() {
          @Override public List<NoteInfo> get() {
            return infos;
          }
        });
        notifyDataChanged();
        return false;
      }
    });
  }

  @Override public void onBackPressed() {
    if (!searchView.isIconified()) {
      searchView.setIconified(true);
      noteAdapter.setDataAndType(AdapterType.NOTE_TYPE, null);
      return;
    }
    super.onBackPressed();
  }

  /**
   * 数据改变
   */
  private void notifyDataChanged() {
    noteAdapter.getNoteInfos();
    noteAdapter.notifyDataSetChanged();
  }

  @Override protected void onDestroy() {
    sharedPreferences.edit().putBoolean("isSingle", isSingle).commit();
    super.onDestroy();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Constans.WRITE_NOTE_REQUEST_CODE) {
      if (data != null) {
        String str = data.getStringExtra(Constans.WRITE_NOTE_DATA);
        if (!TextUtils.isEmpty(str)) {
          NoteInfo info = new NoteInfo(new Date().getTime(), str);
          info.save();
          notifyDataChanged();
        }
      }
    } else if (requestCode == Constans.MODIFY_NOTE_REQUEST_CODE) {
      if (null != data) {
        String str = data.getStringExtra(Constans.WRITE_NOTE_DATA);
        if (TextUtils.isEmpty(str)) {
          deleteNote(selectInfo);
        } else if (selectInfo.getContent().equals(str)) { // 若返回的数据跟之前的数据相同
          return;
        } else {
          selectInfo.setContent(str);
          selectInfo.setTitle(new Date().getTime());
          selectInfo.save();
          notifyDataChanged();
        }
      }
    } else if (requestCode == EvernoteSession.REQUEST_CODE_LOGIN) {
      if (resultCode == Activity.RESULT_OK) {
        // handle success
      } else {
        // handle failure
      }
    }
  }

  /**
   * 删除笔记
   */
  private void deleteNote(NoteInfo info) {
    info.delete();
    notifyDataChanged();
  }

  @Override public void onNavigationDrawerItemSelected(int position) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.container, new NavigationDrawerFragment())
        .commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home, menu);
    MenuItem item = menu.getItem(0);
    item.setTitle(isSingle ? "二列" : "一列");
    return true;
  }

  private boolean isSingle = false;

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      isSingle = !isSingle;
      item.setTitle(isSingle ? "二列" : "一列");
      layoutManager.setSpanCount(isSingle ? 1 : 2);
      notifyDataChanged();
      return true;
    } else if (id == R.id.action_about) {
      AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this,
          R.style.Base_Theme_AppCompat_Light_Dialog_Alert).create();
      dialog.setTitle("关于");
      dialog.setView(LayoutInflater.from(this).inflate(R.layout.change_log, null));
      dialog.setButton(Dialog.BUTTON_POSITIVE, "知道了", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
      dialog.show();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
