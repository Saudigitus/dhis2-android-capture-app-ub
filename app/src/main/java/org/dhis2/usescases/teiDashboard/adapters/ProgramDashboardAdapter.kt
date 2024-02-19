package org.dhis2.usescases.teiDashboard.adapters

import android.R.attr.bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.image
import org.dhis2.R
import org.dhis2.usescases.teiDashboard.data.ProgramWithEnrollment


class ProgramDashboardAdapter(
    private val mData: List<ProgramWithEnrollment>,
    private val programUid: String,
    private val listener: ProgramDashboardAdapter.OnItemClickListener
) : RecyclerView.Adapter<ProgramDashboardAdapter.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int, data: List<ProgramWithEnrollment>);
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, mData)
            }
        }

        val name = itemView.findViewById<TextView?>(R.id.displayName)
        val type = itemView.findViewById<TextView?>(R.id.name_text)
        val imageViewCerto = itemView.findViewById<ImageView?>(R.id.imageViewStatus)
        val imageViewLogo = itemView.findViewById<ImageView>(R.id.enrollment_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_enrolment, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        holder.name.text = mData.get(position).description
//        holder.type.text = "%s %s".format(
//            mData.get(position).count,
//            ""
////            mData.get(position).typeName
//        )

//        holder.type.text = "%s %s".format(
//            mData.get(position).countDescription.toString(),
//            mData.get(position).typeName
//        )
//        holder.imageViewCerto.visibility = View.INVISIBLE
//
//        val status = mData.get(position).enrollmentStatus
//        if (status) {
//            holder.imageViewCerto.visibility = View.VISIBLE
//        }
//
        holder.name.text = mData.get(position).displayName
//            holder.name.text = mData.get(position).countEnrollment.toString()

        holder.type.text = "%s %s".format(
            mData.get(position).countDescription.toString(),
            mData.get(position).typeName
        )


        holder.imageViewLogo.setImageResource(mData.get(position).metadataIconData.iconResource)
        holder.imageViewLogo.setColorFilter(mData.get(position).metadataIconData.programColor);

        holder.imageViewCerto.visibility = View.VISIBLE

        holder.imageViewCerto.isClickable = true

//        if (mData.get(position).countEnrollment == 0) {
//            holder.imageViewCerto.setImageResource(R.drawable.ic_add_circle_outline_24)
//        } else {
//            holder.imageViewCerto.setImageResource(R.drawable.ic_success)
//        }
//        Timber.tag("HOJE").d("${mData.get(position).countEnrollment}")

        val status = mData.get(position).enrollmentStatus
        if (status) {
            holder.imageViewCerto.setImageResource(R.drawable.ic_success)
        } else {
            holder.imageViewCerto.setImageResource(R.drawable.ic_add_circle_outline_24)
        }
    }

    override fun getItemCount() = mData.filter {
        it.programId != programUid
    }.size


}
