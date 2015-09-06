package io.github.jungletian.titanjumnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.jungletian.titanjumnote.adapter.NoteAdapter;
import io.github.jungletian.titanjumnote.data.NoteInfo;
import io.github.jungletian.titanjumnote.listener.ISearchAdapter;
import io.github.jungletian.titanjumnote.listener.ItemClickListener;
import io.github.jungletian.titanjumnote.listener.ItemLongClickListener;
import io.github.jungletian.titanjumnote.util.DBHelper;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
public class HomeActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NoteInfo selectInfo;

    private NoteAdapter noteAdapter;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.container)
    RecyclerView mRecyclerView;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeActivity.this, WriteNoteActivity.class), Constans.WRITE_NOTE_REQUEST_CODE);
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(this);
        setNoteAdapterListener();
        mRecyclerView.setAdapter(noteAdapter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(R.layout.search_view);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.my_note);
        searchView = (SearchView) ((LinearLayout) actionBar.getCustomView()).getChildAt(0);
        setSearchViewListener();
    }

    // 设置监听
    private void setNoteAdapterListener() {
        noteAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(NoteInfo info) {
                selectInfo = info;
                Intent intent = new Intent(HomeActivity.this, WriteNoteActivity.class);
                intent.putExtra(Constans.CURRENT_CONTENT, info.getContent());
                startActivityForResult(intent, Constans.MODIFY_NOTE_REQUEST_CODE);
            }
        });

        noteAdapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void onItemLongClick(NoteInfo info) {
                selectInfo = info;
                AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this,R.style.Base_Theme_AppCompat_Light_Dialog_Alert).create();
                dialog.setTitle("确定删除吗");
                dialog.setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setButton(Dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectInfo.delete();
                        notifyDataChanged();
                    }
                });
                dialog.show();

            }
        });
    }


    /**
     * 设置搜索View 的监听
     */
    private void setSearchViewListener() {
        // 关闭按钮监听
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                noteAdapter.setDataAndType(AdapterType.NOTE_TYPE,null);
                return false;
            }
        });
        // 搜索文字监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<NoteInfo> infos = DBHelper.getAll();

                int size = infos.size();
                for (int i = size - 1; i >= 0; i--) {
                    String content = infos.get(i).getContent();
                    if (!content.contains(newText)) {
                        infos.remove(i);
                    }
                }

                noteAdapter.setDataAndType(AdapterType.SEARCH_TYPE, new ISearchAdapter() {
                    @Override
                    public List<NoteInfo> get() {
                        return infos;
                    }
                });
                notifyDataChanged();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            noteAdapter.setDataAndType(AdapterType.NOTE_TYPE,null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constans.WRITE_NOTE_REQUEST_CODE) {
            if (data != null) {
                String str = data.getStringExtra(Constans.WRITE_NOTE_DATA);
                if (!TextUtils.isEmpty(str)) {
                    NoteInfo info = new NoteInfo(new Date().getTime(), str);
                    info.save();
                    notifyDataChanged();
                }
            }
        }else if (requestCode == Constans.MODIFY_NOTE_REQUEST_CODE) {
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
        }
    }

    /**
     * 删除笔记
     * @param info
     */
    private void deleteNote(NoteInfo info){
        info.delete();
        notifyDataChanged();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(HomeActivity.this, "设置", Toast.LENGTH_SHORT).show();
            return true;
        }/* else if (id == R.id.action_search) {
            item.setVisible(false);
            return true;
        }*/ else if (id == R.id.action_about) {

            Toast.makeText(HomeActivity.this, "关于", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }

}
