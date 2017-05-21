package com.flynorc.a08_newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Flynorc on 20-May-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ArticleViewHolder> {
    private List<Article> articles;

    //implement the view holder pattern using a custom class extending the RecyclerView.ViewHolder
    public static class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView articleTitle;
        private TextView articleSection;
        private TextView articleAuthor;
        private TextView articlePublished;
        private Article article;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            articleTitle = (TextView) itemView.findViewById(R.id.article_title);
            articleSection = (TextView) itemView.findViewById(R.id.article_section);
            articleAuthor = (TextView) itemView.findViewById(R.id.article_author);
            articlePublished = (TextView) itemView.findViewById(R.id.article_published);

            itemView.setOnClickListener(this);
        }

        public void setArticle(Article article) {
            this.article = article;
        }

        //on click listener for items
        @Override
        public void onClick(View v) {
            //start an implicit intent to open a web page of the url address of the article
            //that will be handled by the users browser
            String url = article.getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));

            v.getContext().startActivity(i);
        }
    }

    //constructor for our custom RecyclerAdapter
    public RecyclerAdapter(List<Article> articles) {
        this.articles = articles;
    }

    //setter for articles
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }


    /*
     * implementation of methods that are abstract in RecyclerView.Adapter
     */
    //when there are no existing items available for reuse, a new one is inflated
    @Override
    public RecyclerAdapter.ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ArticleViewHolder(inflatedView);
    }

    //recycle the list item
    @Override
    public void onBindViewHolder(RecyclerAdapter.ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.setArticle(article);
        holder.articleTitle.setText(article.getTitle());
        holder.articlePublished.setText(article.getDate());
        holder.articleSection.setText(article.getSection());
        holder.articleAuthor.setText(article.getAuthorName());
    }

    //get the number of items in the list
    @Override
    public int getItemCount() {
        return articles.size();
    }
}
