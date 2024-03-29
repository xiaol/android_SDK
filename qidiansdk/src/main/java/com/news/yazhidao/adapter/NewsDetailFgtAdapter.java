package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.TextViewExtend;

import static com.news.yazhidao.R.id.attentionlayout;

//import com.news.yazhidao.pages.NewsDetailWebviewAty;

/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends CommonAdapter<RelatedItemEntity> {

    private Activity mContext;
    public static final int REQUEST_CODE = 1030;
    private int mCardWidth, mCardHeight;
    private int mScreenWidth;

    public NewsDetailFgtAdapter(Activity context) {
        super(R.layout.item_news_detail_relate_attention, context, null);
        this.mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        if (relatedItemEntity.getUrl().equals("-1")) {//没有数据时也可以让listView滑动
            Logger.e("aaa", "没有数据时的状况！！！！！！！！！！！！！");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.getView(attentionlayout).setLayoutParams(layoutParams);
            return;
        }
        onAttentionItemClickListener((RelativeLayout) holder.getView(attentionlayout), relatedItemEntity);
        TextUtil.setLayoutBgResource(mContext,  (RelativeLayout) holder.getView(attentionlayout), R.drawable.bg_feed_list_select);
        TextViewExtend title = holder.getView(R.id.attention_Title);
        String strTitle = relatedItemEntity.getTitle().replace("<font color='#0091fa' >", "").replace("</font>", "");
        title.setText(strTitle);
        TextUtil.setTextColor(mContext, title, R.color.newsFeed_titleColor);
        holder.setTextViewExtendText(R.id.attention_Source, relatedItemEntity.getPname());
        TextUtil.setTextColor(mContext, (TextViewExtend) holder.getView(R.id.attention_Source), R.color.new_color3);
        if (getCount() == position + 1) {//去掉最后一条的线
            holder.getView(R.id.attention_bottomLine).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.attention_bottomLine).setVisibility(View.VISIBLE);
        }
        ImageView ivCard = holder.getView(R.id.title_img_View);
        String imgUrl = relatedItemEntity.getImgUrl();
        if (TextUtil.isEmptyString(imgUrl)) {
            ivCard.setVisibility(View.GONE);
        } else {
            ivCard.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            Glide.with(mContext).load(imgUrl).centerCrop().placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCard);
        }
    }

    public void onAttentionItemClickListener(RelativeLayout mAttentionlayout, final RelatedItemEntity relatedItemEntity) {
        mAttentionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, RelevantViewWebviewAty.class);
//                String zhihuUrl = relatedItemEntity.getUrl();
//                intent.putExtra(RelevantViewWebviewAty.KEY_URL, zhihuUrl);
//                mContext.startActivity(intent);
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(relatedItemEntity.getUrl());
                String value = sanitizer.getValue("nid");
                Intent intent = new Intent(mContext, NewsDetailAty2.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, value);
                mContext.startActivity(intent);
//                Pattern p = Pattern.compile("<font color='#0091fa' >([^<]*)</font>");
//                Matcher m = p.matcher(title);
//                StringBuilder sbKeyword = new StringBuilder();
//                while (m.find()) {
//                    sbKeyword.append(m.group(1));
//                }
//                String keyword = sbKeyword.toString();
//                Intent intent;
//                if (TextUtil.isEmptyString(keyword)) {
//                    intent = new Intent(mContext, NewsDetailWebviewAty.class);
//                    String zhihuUrl = relatedItemEntity.getUrl();
//                    intent.putExtra(NewsDetailWebviewAty.KEY_URL, zhihuUrl);
//                    mContext.startActivity(intent);
//                } else {
//                    intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                    intent.putExtra(SearchManager.QUERY, keyword);
//                    intent.setClassName("com.lieying.browser", "com.lieying.browser.BrowserActivity");
//                    intent.putExtra("back_to_navigation", true);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);
//                }
            }
        });
    }


}
