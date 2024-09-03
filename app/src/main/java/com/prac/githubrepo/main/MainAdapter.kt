package com.prac.githubrepo.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.databinding.ItemMainBinding
import com.prac.githubrepo.main.request.StarStateRequestBuilder

class MainAdapter(
    private val starStateRequestBuilder: StarStateRequestBuilder,
    private val onStarClickListener: OnStarClickListener
) : PagingDataAdapter<RepoEntity, MainAdapter.ViewHolder>(diffUtil) {
    class ViewHolder(
        private val binding: ItemMainBinding,
        private val starStateRequestBuilder: StarStateRequestBuilder,
        private val onStarClickListener: OnStarClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repoEntity: RepoEntity) {
            with(repoEntity) {
                setRequestBuilder(binding.root, this)
                setProfile()
                setName()
                setTitle()
                setStarImage(isStarred)
                setStarCount()
                setUpdatedDate()
            }

            binding.ivStar.setStarClickListener(repoEntity, onStarClickListener)
        }

        private fun setRequestBuilder(view: View, repoEntity: RepoEntity) {
            starStateRequestBuilder.setView(view)
                .setRepoEntity(repoEntity)
                .build()
        }

        private fun RepoEntity.setProfile() {
            Glide.with(binding.root)
                .load(this.owner.avatarUrl)
                .error(R.drawable.img_glide_error)
                .placeholder(R.drawable.img_glide_profile)
                .into(binding.ivProfile)
        }

        private fun RepoEntity.setName() {
            binding.tvName.text = this.owner.login
        }

        private fun RepoEntity.setTitle() {
            binding.tvName.text = this.owner.login
        }

        private fun setStarImage(isStarred: Boolean?) {
            binding.ivStar.setImageResource(
                if (isStarred == true) R.drawable.img_star
                else R.drawable.img_unstar
            )
        }

        private fun RepoEntity.setStarCount() {
            binding.tvStar.text = this.stargazersCount.toString()
        }

        private fun RepoEntity.setUpdatedDate() {
            binding.tvLastUpdatedDate.text = this.updatedAt
        }

        private fun View.setStarClickListener(
            repoEntity: RepoEntity,
            onStarClickListener: OnStarClickListener
        ) {
            setOnClickListener {
                if (repoEntity.isStarred == true) {
                    onStarClickListener.unStar(repoEntity)
                    return@setOnClickListener
                }

                onStarClickListener.star(repoEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            starStateRequestBuilder,
            onStarClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RepoEntity>() {
            override fun areItemsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean =
                oldItem == newItem
        }
    }

    interface OnStarClickListener {
        fun star(repoEntity: RepoEntity)
        fun unStar(repoEntity: RepoEntity)
    }
}