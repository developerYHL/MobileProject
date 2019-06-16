package com.example.mobileproject.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.example.mobileproject.R
import com.example.mobileproject.model.DetailItem

class RecyclerAdapter(private val mDataList: MutableList<DetailItem>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var mListener: MyRecyclerViewClickListener? = null

    // 뷰 홀더를 생성하는 부분. 레이아웃을 만드는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail, parent, false)
        return ViewHolder(view)
    }

    // 뷰 홀더에 데이터를 설정하는 부분
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDataList[position]

        holder.title.text = item.nickname
        holder.contents.text = item.contents

        // 클릭 이벤트
        if (mListener != null) {
            // 현재 위치
            holder.itemView.setOnClickListener { mListener!!.onItemClicked(holder.adapterPosition) }
            holder.share!!.setOnClickListener { mListener!!.onShareButtonClicked(holder.adapterPosition) }
            holder.more!!.setOnClickListener { mListener!!.onLearnMoreButtonClicked(holder.adapterPosition) }
        }
    }

    // 아이템의 수
    override fun getItemCount(): Int {
        return mDataList.size
    }

    // 각각의 아이템의 레퍼런스를 저장할 뷰 홀더 클래스
    // 반드시 RecyclerView.ViewHolder를 상속해야 함
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var title: TextView = itemView.findViewById<View>(R.id.title_text) as TextView
        internal var contents: TextView = itemView.findViewById<View>(R.id.contents_text) as TextView

        internal var share: Button? = null
        internal var more: Button? = null

    }

    fun setOnClickListener(listener: MyRecyclerViewClickListener) {
        mListener = listener
    }

    interface MyRecyclerViewClickListener {
        // 아이템 전체 부분의 클릭
        fun onItemClicked(position: Int)

        // Share 버튼 클릭
        fun onShareButtonClicked(position: Int)

        // Learn More 버튼 클릭
        fun onLearnMoreButtonClicked(position: Int)
    }

    fun removeItem(position: Int) {
        mDataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mDataList.size)
    }

    fun addItem(position: Int, item: DetailItem) {
        mDataList.add(position, item)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, mDataList.size)
    }
}
