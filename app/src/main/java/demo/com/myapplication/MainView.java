package demo.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.ChannelOperateAty;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.UserManager;
import com.news.yazhidao.widget.FeedDislikePopupWindow;
import com.news.yazhidao.widget.channel.ChannelTabStrip;
import com.news.yazhidao.widget.tag.TagCloudLayout;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by fengjigang on 15/10/28.
 * 主界面view
 */
public class MainView extends View implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack {

    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.yazhidao.ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "com.news.yazhidao.ACTION_USER_LOGOUT";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";
    private ArrayList<ChannelItem> mUnSelChannelItems;
    RelativeLayout view;

    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand;
    private ChannelItemDao mChannelItemDao;
    private Handler mHandler = new Handler();
    private UserLoginReceiver mReceiver;
    private long mLastPressedBackKeyTime;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<String, ArrayList<NewsFeed>> mSaveData = new HashMap<>();

    FragmentActivity activity;
    //baidu Map
//    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    private SimpleDraweeView mUserCenter;
    private TextView mDetailLeftBack;

    /**
     * 自定义的PopWindow
     */
    FeedDislikePopupWindow dislikePopupWindow;

    public MainView(FragmentActivity context) {
        super(context);
        initializeViews(context);
    }


    @Override
    public void result(String channelId, ArrayList<NewsFeed> results) {
        mSaveData.put(channelId, results);
    }

    /**
     * 回到"奇点"频道并刷新
     */
    public void backFirstItemAndRefreshData() {
        //1.返回到"奇点"频道
        mViewPager.setCurrentItem(0,false);
        mChannelTabStrip.scrollToFirstItem();
        //2.刷新数据
        MyViewPagerAdapter a = (MyViewPagerAdapter) mViewPager.getAdapter();
        NewsFeedFgt newsFeedFgt = (NewsFeedFgt) a.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        Log.e("jigang","aaa---backFirstItemAndRefreshData");
        newsFeedFgt.refreshData();
    }


    private class UserLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (ACTION_USER_LOGIN.equals(intent.getAction())) {
//                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
//                if (!TextUtil.isEmptyString(url)) {
//                    mUserCenter.setImageURI(Uri.parse(url));
//                }
//            } else if (ACTION_USER_LOGOUT.equals(intent.getAction())) {
//                mUserCenter.setImageURI(null);
//            }
        }
    }

    protected void initializeViews(final FragmentActivity mContext) {
//        MobclickAgent.onEvent(this,"bainews_user_assess_app");
        activity = mContext;
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.qd_aty_main, null);
        mChannelItemDao = new ChannelItemDao(mContext);
        mSelChannelItems = new ArrayList<>();
        mChannelTabStrip = (ChannelTabStrip) view.findViewById(R.id.mChannelTabStrip);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);

        mDetailLeftBack = (TextView) view.findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailLeftBack.setVisibility(View.GONE);
       View paddingView = (View) view.findViewById(R.id.paddingView);
        paddingView.setVisibility(View.VISIBLE);
        mViewPager.setOffscreenPageLimit(2);
        mChannelExpand = (ImageView) view.findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(mContext.getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
//                mUserCenter.setImageURI(Uri.parse("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0"));
        dislikePopupWindow = (FeedDislikePopupWindow) view.findViewById(R.id.feedDislike_popupWindow);
        dislikePopupWindow.setVisibility(View.GONE);
        dislikePopupWindow.setItemClickListerer(new TagCloudLayout.TagItemClickListener() {
            Handler mHandler = new Handler();

            @Override
            public void itemClick(int position) {
                switch (position) {
                    case 0://不喜欢
//
//                        NewsFeedFgt newsFeedFgt= (NewsFeedFgt) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        newsFeedFgt.disLikeItem();
                    case 1://重复、旧闻
                    case 2://内容质量差
                    case 3://不喜欢
                        mNewsFeedAdapter.disLikeDeleteItem();
                        dislikePopupWindow.setVisibility(View.GONE);
                        ToastUtil.showReduceRecommendToast(mContext);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 100);
                        break;
                }
            }
        });
        /**更新右下角用户登录图标*/
//        User user = SharedPreManager.getUser(this);
//        if (user != null) {
//            if (!TextUtil.isEmptyString(user.getUserIcon())) {
//                mUserCenter.setImageURI(Uri.parse(user.getUserIcon()));
//            }
//        }
        /**注册用户登录广播*/
//        mReceiver = new UserLoginReceiver();
//        IntentFilter filter = new IntentFilter(ACTION_USER_LOGIN);
//        filter.addAction(ACTION_USER_LOGOUT);
//        registerReceiver(mReceiver, filter);


    }


    public View  getNewsView(){
        return this.view;
    }
    /**
     * 开始顶部 progress 刷新动画
     */
    public void startTopRefresh() {
    }

    /**
     * 停止顶部 progress 刷新动画
     */
    public void stopTopRefresh() {
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//        }
//    }

    protected void loadData() {

		 UserManager.registerVisitor(activity,null);
    //    mHandler.postDelayed(new Runnable() {
     //       @Override
//public void run() {
   //             UmengUpdateAgent.setUpdateAutoPopup(true);
    //            UmengUpdateAgent.update(MainAty.this);
    //        }
        //}, 2000);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mChannelExpand) {

//        switch (v.getId()) {
////            case R.id.mTopSearch:
////                Intent topicSearch = new Intent(MainAty.this, TopicSearchAty.class);
////                startActivity(topicSearch);
////                break;
//            case R.id.mChannelExpand:
            Intent channelOperate = new Intent(activity, ChannelOperateAty.class);
//                Mobclic/kAgent.onEvent(this, "user_open_channel_edit_page");
            activity.startActivityForResult(channelOperate, REQUEST_CODE);
        }
//                break;
//            case R.id.mUserCenter:
//                User user = SharedPreManager.getUser(this);
////                //FIXME debug
////                if (user == null){
////                    user = new User();
////                    user.setUserName("forward_one");
////                    user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
////                }
//                if (user == null) {
//                    Intent loginAty = new Intent(this, LoginAty.class);
//                    startActivity(loginAty);
//                } else {
//                    Intent userCenterAty = new Intent(this, UserCenterAty.class);
//                    startActivity(userCenterAty);
//                }
//                MobclickAgent.onEvent(this, "yazhidao_user_open_user_center");
//                break;
//        else if (v.getId() == R.id.mDetailLeftBack) {
////            case R.id.mDetailLeftBack:
//            MainAty.this.finish();
////                break;
//
//        }
    }

    ChannelItem item1;
    ArrayList<ChannelItem> channelItems;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == REQUEST_CODE) {
            channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            int currPosition = mViewPager.getCurrentItem();
            item1 = mSelChannelItems.get(currPosition);
            int index = -1;
            for (int i = 0; i < channelItems.size(); i++) {
                ChannelItem item = channelItems.get(i);
                if (item1.getId().equals(item.getId())) {
                    index = i;
                }
            }
            if (index == -1) {
                Logger.e("jigang", "index = " + index);
                index = currPosition > channelItems.size() - 1 ? channelItems.size() - 1 : currPosition;
            }
            mViewPager.setCurrentItem(index);
            Fragment item = mViewPagerAdapter.getItem(index);
            if (item != null) {
                ((NewsFeedFgt) item).setNewsFeed(mSaveData.get(item1.getId()));
            }
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }



    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mSelChannelItems = mChannelItemDao.queryForSelected();
            mUnSelChannelItems = mChannelItemDao.queryForNormal();

            //统计用户频道订阅/非订阅 频道数
            HashMap<String, String> unSubChannel = new HashMap<>();
            unSubChannel.put("unsubscribed_channels", TextUtil.List2String(mUnSelChannelItems));
//            MobclickAgent.onEventValue(MainAty.this, "user_unsubscribe_channels", unSubChannel, mUnSelChannelItems.size());

            HashMap<String, String> subChannel = new HashMap<>();
            subChannel.put("subscribed_channels", TextUtil.List2String(mSelChannelItems));
//            MobclickAgent.onEventValue(MainAty.this, "user_subscribed_channels", subChannel, mSelChannelItems.size());

        }

        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems) {
            mSelChannelItems = pChannelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelChannelItems.get(position).getName();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = -1;
            if (channelItems != null && channelItems.size() > 0) {
                for (int i = 0; i < channelItems.size(); i++) {
                    ChannelItem item = channelItems.get(i);
                    if (item1.getId().equals(item.getId())) {
                        index = i;
                    }
                }
            }
                return POSITION_NONE;
//            if (index == -1) {
//                return POSITION_NONE;
//            } else {
//                return POSITION_UNCHANGED;
//            }
        }

        @Override
        public int getCount() {
            return mSelChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt feedFgt = NewsFeedFgt.newInstance(channelId);
            feedFgt.setNewsFeedFgtPopWindow(mNewsFeedFgtPopWindow);
            feedFgt.setNewsSaveDataCallBack(MainView.this);
            return feedFgt;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt fgt = (NewsFeedFgt) super.instantiateItem(container, position);
            ArrayList<NewsFeed> newsFeeds = mSaveData.get(channelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
//                fgt.refreshData();
            } else {
                fgt.setNewsFeed(newsFeeds);
            }
            return fgt;
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
////            super.destroyItem(container, position, object);
//
//        }

    }

    NewsFeedAdapter mNewsFeedAdapter;
    NewsFeedFgt.NewsFeedFgtPopWindow mNewsFeedFgtPopWindow = new NewsFeedFgt.NewsFeedFgtPopWindow() {
        @Override
        public void showPopWindow(int x, int y, String PubName, NewsFeedAdapter mAdapter) {
            mNewsFeedAdapter = mAdapter;
            dislikePopupWindow.setSourceList("来源：" + PubName);
            dislikePopupWindow.showView(x, y - DeviceInfoUtil.getStatusBarHeight(activity));

        }

    };

}
