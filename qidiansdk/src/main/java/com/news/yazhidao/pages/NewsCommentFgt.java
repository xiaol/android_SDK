package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsCommentHeaderView;
import com.news.yazhidao.widget.TextViewExtend;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.news.yazhidao.net.volley.NewsCommentRequest;

//import com.news.yazhidao.widget.UserCommentDialog;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻评论页
 */
public class NewsCommentFgt extends Fragment {

    public static final int REQUEST_CODE = 1030;
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String ACTION_REFRESH_CTD = "com.news.yazhidao.ACTION_REFRESH_CTD";
    public static final String LIKETYPE = "liketype";
    public static final String LIKEBEAN = "likebean";
    private PullToRefreshListView mNewsCommentList;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    private CommentsAdapter mCommentsAdapter;
    private int mPageIndex = 1;
    private RefreshPageBroReceiver mRefreshReceiver;
    //    private RefreshLikeBroReceiber mRefreshLike;
    private RelativeLayout bgLayout;
    private User mUser;
    private NewsDetailComment mComment;
    private Holder mHolder;
    private NewsFeed mNewsFeed;
    private SharedPreferences mSharedPreferences;
    private NewsCommentHeaderView mNewsCommentHeaderView;
    private Context mContext;
    private RequestManager mRequestManager;

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                mCommentsAdapter.notifyDataSetChanged();
                mNewsCommentHeaderView.setNewsCommentTitleTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL) + 2);
            } else {
//                NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//                mComments.add(0, comment);
                mNewsCommentHeaderView.setNoCommentsLayoutGone();
                mCommentsAdapter.setData(mComments);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mRequestManager = Glide.with(this);
        Bundle arguments = getArguments();
        mNewsFeed = (NewsFeed) arguments.getSerializable(KEY_NEWS_FEED);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        if (mRefreshReceiver == null) {
            mRefreshReceiver = new RefreshPageBroReceiver();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            getActivity().registerReceiver(mRefreshReceiver, filter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_comment, null);
        mNewsCommentList = (PullToRefreshListView) rootView.findViewById(R.id.mNewsCommentList);
        TextUtil.setLayoutBgResource(mContext, mNewsCommentList, R.color.white);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mNewsCommentList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mCommentsAdapter = new CommentsAdapter(getActivity());
        mNewsCommentList.setAdapter(mCommentsAdapter);
        mNewsCommentHeaderView = new NewsCommentHeaderView(getActivity());
        ListView lv = mNewsCommentList.getRefreshableView();
        mNewsCommentHeaderView.setData(mNewsFeed);
        lv.addHeaderView(mNewsCommentHeaderView);
        mNewsCommentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        loadData();
        return rootView;
    }

    private void loadData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = new NewsDetailRequest<>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
        }.getType(), HttpConstant.URL_FETCH_COMMENTS + "did=" + TextUtil.getBase64(mNewsFeed.getDocid()) + (mUser != null ? "&uid=" + SharedPreManager.mInstance(getActivity()).getUser(getActivity()).getMuid() : "") +
                "&p=" + (mPageIndex++), new Response.Listener<ArrayList<NewsDetailComment>>() {
            @Override
            public void onResponse(ArrayList<NewsDetailComment> result) {
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                mNewsCommentList.onRefreshComplete();
                if (!TextUtil.isListEmpty(result)) {
                    mComments.addAll(result);
                    mCommentsAdapter.setData(mComments);
                    mNewsCommentHeaderView.setNoCommentsLayoutGone();
                } else {
                    if (!TextUtil.isListEmpty(mComments)) {
                        mNewsCommentHeaderView.setNoCommentsLayoutGone();
                    } else {
                        mNewsCommentHeaderView.setNoCommentsLayoutVisible();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsCommentList.onRefreshComplete();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                if (error.toString().contains("服务端未找到数据 2002") && mComments.size() == 0) {
                    mNewsCommentHeaderView.setNoCommentsLayoutVisible();
                }
            }
        });
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRefreshReceiver != null) {
            getActivity().unregisterReceiver(mRefreshReceiver);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == LoginAty.REQUEST_CODE && data != null) {
//            mUser = (User) data.getSerializableExtra(LoginAty.KEY_USER_LOGIN);
//            addNewsLove(mUser, mComment, mHolder);
//        }
    }

    class CommentsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<NewsDetailComment> comments;

        CommentsAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<NewsDetailComment> comments) {
            this.comments = comments;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return comments == null ? 0 : comments.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_comment1, null, false);
                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
                holder.ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
//                holder.tvTime = (TextViewExtend) convertView.findViewById(R.id.tv_time);
                //梁帅：点赞功能注释掉
//                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
//                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            TextUtil.setTextColor(mContext, holder.tvName, R.color.new_color2);
            TextUtil.setTextColor(mContext, holder.tvContent, R.color.new_color1);
            TextUtil.setLayoutBgResource(mContext, (ImageView) convertView.findViewById(R.id.mSelectCommentDivider), R.color.new_color4);
            holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
            final NewsDetailComment comment = comments.get(position);
            final User user = SharedPreManager.mInstance(getActivity()).getUser(mContext);
            mComment = comment;
            mHolder = holder;
//            setNewsTime(holder.tvTime, comment.getCtime());
            if (!TextUtil.isEmptyString(comment.getAvatar())) {
                mRequestManager.load(Uri.parse(comment.getAvatar())).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
            } else {
                mRequestManager.load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.bg_home_login_header))).into(holder.ivHeadIcon);
            }
            holder.tvName.setText(comment.getUname());
            //梁帅：点赞功能注释掉
//            int count = comment.getCommend();
//            if (count == 0) {
//                holder.tvPraiseCount.setVisibility(View.INVISIBLE);
//            } else {
//                holder.tvPraiseCount.setVisibility(View.VISIBLE);
//                holder.tvPraiseCount.setText(comment.getCommend() + "");
//            }
            //梁帅：点赞功能注释掉
            holder.tvContent.setText(comment.getContent());
//            if (comment.getUpflag() == 0) {
//                holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
//            } else {
//                holder.ivPraise.setImageResource(R.drawable.bg_praised);
//            }

//            if (user != null && user.getUserId().equals(comment.getUid())) {
//                holder.ivPraise.setVisibility(View.GONE);
//            } else {
//                holder.ivPraise.setVisibility(View.VISIBLE);
//
//            }
            //梁帅：点赞功能注释掉
//            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

//
//                    if (user == null) {
//                        Intent loginAty = new Intent(mContext, LoginAty.class);
//                        startActivityForResult(loginAty, REQUEST_CODE);
//                    } else {
//                        addNewsLove(user, comment, holder);
//                        comments.get(position).setPraise(true);
//                        comments.get(position).setLove(comment.getLove() + 1);
//                    }

//                }
//            });
            return convertView;
        }
    }

    private void setNewsTime(TextViewExtend tvTime, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            if (between >= (24 * 3600000)) {
                tvTime.setText("");
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
                tvTime.setText("");
            } else {
                int time = (int) (between * 60 / 3600000);
                if (time > 0)
                    tvTime.setText(between * 60 / 3600000 + "分钟前");
                else
                    tvTime.setText(between * 60 * 60 / 3600000 + "秒前");
            }
        } catch (ParseException e) {
            tvTime.setText(updateTime);
            e.printStackTrace();
        }

    }

    private void addNewsLove(User user, NewsDetailComment comment, final Holder holder) {
        try {
            String name = URLEncoder.encode(user.getUserName(), "utf-8");
            String cid = URLEncoder.encode(comment.getId(), "utf-8");
            user.setUserName(name);
            comment.setId(cid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Logger.e("jigang", "love url=" + HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName());
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        NewsLoveRequest<String> loveRequest = new NewsLoveRequest<String>(Request.Method.PUT, new TypeToken<String>() {
//        }.getType(), HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName(), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String result) {
//                mNewsCommentList.onRefreshComplete();
//                Logger.e("jigang", "network success, love" + result);
//                if (!TextUtil.isEmptyString(result)) {
//                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
//                    holder.tvPraiseCount.setText(result);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mNewsCommentList.onRefreshComplete();
//                Logger.e("jigang", "network fail");
//            }
//        });
//        loveRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(loveRequest);
    }

    class Holder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvTime;
//        TextViewExtend tvPraiseCount;
//        ImageView ivPraise;
    }
}
