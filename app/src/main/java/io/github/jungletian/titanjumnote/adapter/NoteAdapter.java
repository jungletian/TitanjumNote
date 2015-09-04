package io.github.jungletian.titanjumnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.jungletian.titanjumnote.AdapterType;
import io.github.jungletian.titanjumnote.R;
import io.github.jungletian.titanjumnote.data.NoteInfo;
import io.github.jungletian.titanjumnote.listener.ISearchAdapter;
import io.github.jungletian.titanjumnote.listener.ItemClickListener;
import io.github.jungletian.titanjumnote.listener.ItemLongClickListener;
import io.github.jungletian.titanjumnote.util.DBHelper;
import io.github.jungletian.titanjumnote.util.DateUtils;

/**
 * Create by JungleTian on 15-8-26 23:56.
 * Email：tjsummery@gmail.com
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    // 条目点击事件
    private ItemClickListener mItemClickListener;
    // 长按点击事件
    private ItemLongClickListener mItemLongClickListener;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    private Context mContext;
    // 笔记集合
    private List<NoteInfo> infos;

    private AdapterType type = AdapterType.NOTE_TYPE;
    ISearchAdapter data;

    public NoteAdapter(Context context) {
        this.mContext = context;
        // 默认
        setDataAndType(AdapterType.NOTE_TYPE, null);
        getNoteInfos();
    }

    public void setDataAndType(AdapterType type, ISearchAdapter data){
        this.type = type;
        this.data = data;
    }

    /**
     * 获取笔记
     */
    public void getNoteInfos() {
//        if (data != null) {
//            this.infos = data.get();
//        }else {
//            this.infos = DBHelper.getAll();
//        }
        switch (type) {
            case NOTE_TYPE:
                this.infos = DBHelper.getAll();
                break;
            case SEARCH_TYPE:
                this.infos = data.get();
                break;
        }

    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        NoteInfo info = infos.get(position);
        holder.title.setText(DateUtils.longToString(info.getTitle()));
        holder.content.setText(info.getContent());
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @Bind(R.id.note_title)
        TextView title;
        @Bind(R.id.note_content)
        TextView content;
        // 卡片
        View card;
        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView;
            ButterKnife.bind(this,itemView);

            card.setOnLongClickListener(this);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(infos.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            mItemLongClickListener.onItemLongClick(infos.get(getAdapterPosition()));
            return true;
        }
    }
}
