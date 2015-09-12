package io.github.jungletian.titanjumnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.jungletian.titanjumnote.R;
import io.github.jungletian.titanjumnote.data.NoteInfo;
import io.github.jungletian.titanjumnote.listener.ISearchAdapter;
import io.github.jungletian.titanjumnote.listener.ItemClickListener;
import io.github.jungletian.titanjumnote.listener.ItemLongClickListener;
import io.github.jungletian.titanjumnote.util.DBHelper;
import io.github.jungletian.titanjumnote.util.DateUtils;
import java.util.List;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
  /** 上下文 */
  private Context mContext;
  /** 笔记集合 */
  private List<NoteInfo> infos;
  /** 适配器类型 */
  private AdapterType type = AdapterType.NOTE_TYPE;
  ISearchAdapter data;
  /** 条目点击事件 */
  private ItemClickListener mItemClickListener;
  /** 长按点击事件 */
  private ItemLongClickListener mItemLongClickListener;

  /**
   * 设置点击监听器
   * @param mItemClickListener 监听器
   */
  public void setItemClickListener(ItemClickListener mItemClickListener) {
    this.mItemClickListener = mItemClickListener;
  }

  /**
   * 设置长按监听器
   * @param mItemLongClickListener 监听器
   */
  public void setItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
    this.mItemLongClickListener = mItemLongClickListener;
  }


  public NoteAdapter(Context context) {
    this.mContext = context;
    // 默认
    setDataAndType(AdapterType.NOTE_TYPE, null);
    getNoteInfos();
  }

  /**
   * 设置数据和数据类型
   * @param type 类型
   * @param data 数据
   */
  public void setDataAndType(AdapterType type, ISearchAdapter data) {
    this.type = type;
    this.data = data;
  }

  /**
   * 获取笔记
   */
  public void getNoteInfos() {
    switch (type) {
      case NOTE_TYPE:
        this.infos = DBHelper.getAll();
        break;
      case SEARCH_TYPE:
        this.infos = data.get();
        break;
    }
  }

  @Override public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
    return new ViewHolder(v);
  }

  @Override public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
    NoteInfo info = infos.get(position);
    holder.title.setText(DateUtils.longToString(info.getTitle()));
    holder.content.setText(info.getContent());
  }

  @Override public int getItemCount() {
    return infos.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    @Bind(R.id.note_title) TextView title;
    @Bind(R.id.note_content) TextView content;
    // 卡片
    View card;

    public ViewHolder(View itemView) {
      super(itemView);
      card = itemView;
      ButterKnife.bind(this, itemView);

      card.setOnLongClickListener(this);
      card.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      mItemClickListener.onItemClick(infos.get(getAdapterPosition()));
    }

    @Override public boolean onLongClick(View v) {
      mItemLongClickListener.onItemLongClick(infos.get(getAdapterPosition()));
      return true;
    }
  }
}
