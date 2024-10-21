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
    private val onRepositoryClickListener: OnRepositoryClickListener
) : PagingDataAdapter<RepoEntity, MainAdapter.ViewHolder>(diffUtil) {
    class ViewHolder(
        private val binding: ItemMainBinding,
        private val starStateRequestBuilder: StarStateRequestBuilder,
        private val onRepositoryClickListener: OnRepositoryClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repoEntity: RepoEntity) {
            with(repoEntity) {
                setRequestBuilder(binding.root, this)
                setProfile()
                setName()
                setTitle()
                setStarImage(isStarred)
                setStarCount()
                setDefaultBranch()
                setUpdatedDate()
            }

            binding.root.setRepositoryClickListener(repoEntity, onRepositoryClickListener)
            binding.ivStar.setStarClickListener(repoEntity, onRepositoryClickListener)
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
            binding.tvTitle.text = this.name
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

        private fun RepoEntity.setDefaultBranch() {
            binding.tvDefaultBranch.text = this.defaultBranch
        }

        private fun RepoEntity.setUpdatedDate() {
            binding.tvLastUpdatedDate.text = this.updatedAt
        }

        private fun View.setStarClickListener(
            repoEntity: RepoEntity,
            onRepositoryClickListener: OnRepositoryClickListener
        ) {
            setOnClickListener {
                if (repoEntity.isStarred == true) {
                    onRepositoryClickListener.unStar(repoEntity)
                    return@setOnClickListener
                }

                onRepositoryClickListener.star(repoEntity)
            }
        }

        private fun View.setRepositoryClickListener(
            repoEntity: RepoEntity,
            onRepositoryClickListener: OnRepositoryClickListener
        ) {
            setOnClickListener {
                onRepositoryClickListener.clickRepository(repoEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            starStateRequestBuilder,
            onRepositoryClickListener
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

    interface OnRepositoryClickListener {
        fun clickRepository(repoEntity: RepoEntity)
        fun star(repoEntity: RepoEntity)
        fun unStar(repoEntity: RepoEntity)
    }
}