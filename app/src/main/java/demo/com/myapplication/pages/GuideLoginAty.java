package demo.com.myapplication.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.AuthorizedUser;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import demo.com.myapplication.R;

public class GuideLoginAty extends BaseActivity implements View.OnClickListener {
    private View mGuideWeiboLogin;
    private View mGuideWinxinLogin;
    private View mGuideSkip;
    private ProgressDialog progressDialog;
    private long mFirstClickTime;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_guide_login);
    }

    @Override
    protected void initializeViews() {
        mGuideWeiboLogin = findViewById(R.id.mGuideWeiboLogin);
        mGuideWeiboLogin.setOnClickListener(this);
        mGuideWinxinLogin = findViewById(R.id.mGuideWinxinLogin);
        mGuideWinxinLogin.setOnClickListener(this);
        mGuideSkip = findViewById(R.id.mGuideSkip);
        mGuideSkip.setOnClickListener(this);
    }

    @Override
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mGuideWeiboLogin:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
                weibo.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onError(Platform arg0, int arg1, Throwable arg2) {
                        // TODO Auto-generated method stub
                        arg2.printStackTrace();
                    }

                    @Override
                    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                        // TODO Auto-generated method stub
                        //输出所有授权信息
                        arg0.getDb().exportData();
                        PlatformDb platformdb = arg0.getDb();
                        Intent intent = new Intent();
                        AuthorizedUser authorizedUser = new AuthorizedUser();
                        //游客合并三方时提供该字段
                        authorizedUser.setMuid(SharedPreManager.mInstance(GuideLoginAty.this).getUser(GuideLoginAty.this).getMuid());
                        //三方之间进行合并时提供该字段
                        authorizedUser.setMsuid("");
                        //用户类型 - 本地注册用户:1, 游客用户:2 ,微博三方用户:3 ,微信三方用户:4, 黄历天气:12, 纹字锁频:13, 猎鹰浏览器:14, 白牌:15
                        authorizedUser.setUtype(3);
                        //平台类型 - IOS:1, 安卓:2, 网页:3, 无法识别:4
                        authorizedUser.setPlatform(2);
                        //第三方用户id 如:"3344667788"
                        authorizedUser.setSuid(platformdb.getUserId());
                        //第三方登录token 如:"2233445566"
                        authorizedUser.setStoken(platformdb.getToken());
                        //过期时间 如:"2016-5-27 17:37:22"
                        authorizedUser.setSexpires(DateUtil.longToDAte(platformdb.getExpiresTime()));
                        //用户名 如:"test"
                        authorizedUser.setUname(platformdb.getUserName());
                        //性别 0:男 1:女
                        if ("m".equals(platformdb.getUserGender())) {
                            authorizedUser.setGender(0);
                        } else {
                            authorizedUser.setGender(1);
                        }
                        //头像地址 如:"http://tva4.sinaimg.cn/crop.106.0.819.819.1024/d869d439jw8etv9fxkb1uj20rt0mrwh7.jpg"
                        authorizedUser.setAvatar(platformdb.getUserIcon());
                        //用户屏蔽字段列表  没有可以不填
                        ArrayList<String> averse = new ArrayList<>();
                        //averse.add("政治");
                        //averse.add("战争");
                        //averse.add("腐败");
                        authorizedUser.setAverse(averse);
                        //用户偏好字段列表 没有可以不填
                        ArrayList<String> prefer = new ArrayList<>();
                        //prefer.add("体育");
                        //prefer.add("音乐");
                        //prefer.add("杂志");
                        authorizedUser.setAverse(prefer);
                        //用户地理位置信息 没有可以不填
                        //如:"河南省"
                        authorizedUser.setProvince(SharedPreManager.mInstance(GuideLoginAty.this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                        //如:"郑州市"
                        authorizedUser.setCity(SharedPreManager.mInstance(GuideLoginAty.this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                        //如:"二七区"
                        authorizedUser.setDistrict(SharedPreManager.mInstance(GuideLoginAty.this).get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                        boolean showGuidePage = SharedPreManager.mInstance(GuideLoginAty.this).getBoolean(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE);
                        intent.putExtra(CommonConstant.LOGIN_AUTHORIZEDUSER_ACTION, authorizedUser);
                        if (!showGuidePage) {
                            intent.setClass(GuideLoginAty.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            setResult(CommonConstant.RESULT_LOGIN_CODE, intent);
                        }
                        GuideLoginAty.this.finish();
                    }

                    @Override
                    public void onCancel(Platform arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
                //authorize与showUser单独调用一个即可
                weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
                break;
            case R.id.mGuideWinxinLogin:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
                wechat.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onError(Platform arg0, int arg1, Throwable arg2) {
                        // TODO Auto-generated method stub
                        arg2.printStackTrace();
                    }

                    @Override
                    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                        // TODO Auto-generated method stub
                        //输出所有授权信息
                        arg0.getDb().exportData();
                        Log.i("tag", "22222" + arg0.getDb().exportData());

                    }

                    @Override
                    public void onCancel(Platform arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
                //authorize与showUser单独调用一个即可
                wechat.authorize();//单独授权,OnComplete返回的hashmap是空的
                break;
            case R.id.mGuideSkip:
                this.finish();
                Intent mainAty = new Intent(this, MainActivity.class);
                startActivity(mainAty);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreManager.mInstance(GuideLoginAty.this).save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE, true);
        super.onDestroy();
    }

    /**
     * 显示全屏dialog
     */
    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("登录中...");
            progressDialog.show();
        }
    }
}
