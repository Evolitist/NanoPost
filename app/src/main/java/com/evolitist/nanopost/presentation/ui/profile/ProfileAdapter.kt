package com.evolitist.nanopost.presentation.ui.profile

/*import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.evolitist.nanopost.R
import com.evolitist.nanopost.databinding.ItemProfileHeaderBinding
import com.evolitist.nanopost.data.network.model.UserApiModel

class ProfileAdapter(
    private val onSubscribeClick: () -> Unit,
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    var data: UserApiModel? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data, onSubscribeClick)
    }

    override fun getItemCount() = 1

    class ViewHolder(
        private val binding: ItemProfileHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UserApiModel?, onSubscribeClick: () -> Unit) {
            data ?: return
            data.avatar?.url?.let { binding.avatar.load(it) }
            binding.name.text = data.displayName ?: data.username

            data.bio.let {
                binding.bio.text = it
                binding.bioGroup.isVisible = !it.isNullOrBlank()
            }

            binding.subscribeButton.apply {
                isVisible = data.subscribed != null
                setOnClickListener { onSubscribeClick() }
                data.subscribed?.let {
                    if (it) {
                        text = "Unsubscribe"
                        setIconResource(R.drawable.ic_cross_24)
                    } else {
                        text = "Subscribe"
                        setIconResource(R.drawable.ic_add_24)
                    }
                }
            }
        }
    }
}*/
