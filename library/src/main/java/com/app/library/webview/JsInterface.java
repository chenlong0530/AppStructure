package com.app.library.webview;

/**
 * h5和native的接口
 *
 * @author EX-YANGZHIHONG001 E-mail:EX-YANGZHIHONG001@pingan.com.cn
 * @version 1.1.0
 */
public interface JsInterface {

    void finish(String param);

    void register(String param);

    void autoLogin(String param, String callId);

    void wxShare(String param, String callId);

    void getUserData(String param, String callId);

    void getPosition(String param, String callId);

    void openBrowser(String param);

    void getSignature(String param, String callId);

    void hideMapLoading(String param);

    void appShare(String param, String callId);

    void getVersion(String param, String callId);

    void gotoUserCenter(String param);

    void setUserInfo(String param);

    void loading(String param);

    void hideLoading(String param);

    void getDevicesInfo(String param, String callId);

    void getChannel(String param, String callId);

    void tracking(String param);

    void activate(String param, String callId);

    void getInternationalStatus(String param, String callId);

    void getActionScore(String param);

    void wxPay(String param, String callId);
    /**
     * 微信支付（新），新协议带CALLID
     */
    void wxPayJsSDK(String param, String callId);

    /**
     * Native向pawf-dfp项目发起激活操作
     *
     * @param param
     */
    void dfpActive(String param);

    /**
     * Native提供相关的参数作为H5向pawf-dfp项目发起领取和显示激活状态
     *
     * @param param
     */
    void dfpGetData(String param, String callId);

    void dfpVPNGetData(String param, String callId);

    void dfpShowService(String param);

    void getVpnStatus(String param);

    void startVpn(String param);

    void closeVpn(String param);

    /**
     * 显示右上角弹窗按钮
     *
     * @param param
     */
    void showVpnNav(String param);

    /**
     * 检测应用是否安装
     * <p/>
     * 返回状态:
     * 1 已安装:         installed      显示打开或领取金币
     * 2 存在安装文件:    existed         显示安装
     */
    void isAppInstalled(String param, String callId);

    /**
     * 检测应用是否安装
     * <p/>
     * 返回状态:
     * 1 已安装:         installed      显示打开或领取金币
     * 2 存在安装文件:    existed         显示安装
     */
    void isVpnAppInstalled(String param, String callId);

    /**
     * 开始下载APP
     * <p/>
     * 返回状态
     * 1  开始      download_start
     * 2  下载中    download_process_change
     * 3  下载成功  download_success
     * 4  下载失败  download_failed
     */
    void downloadApp(String param, String callId);

    /**
     * 安装APP
     * <p/>
     * 返回状态
     * 1 安装成功:install_success    显示打开或送金币
     */
    void installApp(String param, String callId);

    /**
     * 打开应用
     * 返回状态
     * 1 打开成功 open_success
     * 2 打开失败 open_failed
     */
    void openApp(String param, String callId);

    /**
     * 查询下载进度
     */
    void queryDownloadProcess(String param, String callId);

    void closeWebView(String param);

    void aliPay(String param, String callId);
    /**
     * 阿里支付（新），新协议带CALLID
     */
    void aliPayJsSDK(String param, String callId);

    void getPAKeplerInfo(String param);

    /**
     * 小歪钱包支付
     */
    void xyPay(String param, String callId);

    /**
     * h5埋点使用 获取当前网络类型
     */
    void getNetType(String param, String callId);

    //////////////////////// [ 以下接口均为为新增接口，新协议均带CALLID ] ////////////////////////

    /**
     * 获取用户信息，新接口（开发给第三方用，只有openid，昵称，头像），新协议带CALLID
     */
    void getUserOpenData(String param, String callId);

    /**
     * 新接口，全屏/退出全屏 {'isNeedFullScreen','YES'} ，退出全屏为 NO ，新协议带CALLID
     */
    void fullScreen(String param, String callId);

    /**
     * 分享APP新接口，去掉url地址，改为通过urlKEY取本地指定的url，以区分不同的供应商，新协议带CALLID
     */
    void channelAppShare(String param, String callId);

    /**
     * 标记当前webview页面不可回退
     *
     * @param param
     */
    void jumpCurrentPage(String param);

    /**
     * 动态收益H5调Native方法
     *
     * @param param
     */
    void dynamicBenefits(String param);

    /**
     * 打开新webview页面
     * @param param
     */
    void openNewWebPage(String param);

    /**
     * 打开外部浏览器，解析H5传来的json，得到url，再调openBrowser()
     * @param param
     * @param callId
     */
    void openExternalBrowser(String param, String callId);
}