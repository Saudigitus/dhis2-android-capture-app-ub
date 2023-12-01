package org.dhis2.usescases.teiDashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.dhis2.R
import org.dhis2.usescases.teiDashboard.data.ProgramDashboardModel
import org.dhis2.usescases.teiDashboard.data.ProgramWithEnrollment
import org.hisp.dhis.android.core.program.Program
import timber.log.Timber

class ProgramDashboardAdapter(
    private val mData: List<ProgramWithEnrollment>,
    private val listener: ProgramDashboardAdapter.OnItemClickListener
) : RecyclerView.Adapter<ProgramDashboardAdapter.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(position: Int);
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        val name = itemView.findViewById<TextView?>(R.id.displayName)
        val type = itemView.findViewById<TextView?>(R.id.name_text)
        val imageViewCerto = itemView.findViewById<ImageView?>(R.id.imageViewStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_enrolment, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = mData.get(position).displayName
//        holder.type.text = mData.get(position).programType
//        holder.type.text = mData.get(position).countDescription.toString()
        holder.type.text = "%s %s".format(mData.get(position).countDescription.toString(), mData.get(position).typeName)

        holder.imageViewCerto.visibility = View.INVISIBLE

        val status = mData.get(position).enrollmentStatus
        if (!status) {
            holder.imageViewCerto.visibility = View.VISIBLE
        }


    }

    override fun getItemCount() = mData.size


}
