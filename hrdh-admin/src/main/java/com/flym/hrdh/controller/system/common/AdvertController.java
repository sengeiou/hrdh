package com.flym.hrdh.controller.system.common;

import com.alibaba.dubbo.config.annotation.Reference;
import com.flym.hrdh.api.model.common.AdvertVm;
import com.flym.hrdh.api.service.common.IAdvertService;
import com.flym.hrdh.api.service.common.IHrdhCacheService;
import com.flym.hrdh.config.ResponseMessage;
import com.flym.hrdh.config.ResultCode;
import com.flym.hrdh.controller.BaseController;
import com.flym.hrdh.pojo.common.Advert;
import com.flym.hrdh.pojo.common.Customer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:红人带货系统</p>
 * <p>Description:轮播图接口管理</p>
 * <p>Copyright: Copyright (c) 2020-05-23</p>
 * <p>Company: 翔梦[http://mail.flym.cn]</p>
 * @author $Author: jh.x $
 * @version $Revision: 1.0.0 $
 */
@Controller
@RequestMapping(value = "/system/advert")
public class AdvertController extends BaseController {

    @Reference(version = "1.0.0")
    IHrdhCacheService hrdhCacheService;

    @Reference(version = "1.0.0")
    protected IAdvertService advertService;

    /**
     * 轮播图列表 -205
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/advertList")
    public ResponseMessage advertList(@RequestBody Map<String, String> map) {

        ResponseMessage returnMsg = null;

        try {

            returnMsg = super.checkNotLogin(hrdhCacheService,map);

            //如果result为空则直接返回
            if (StringUtils.isNotBlank(returnMsg.result)) {
                return returnMsg;
            }

            JSONArray ja = new JSONArray();

            List<AdvertVm> advertVmList = advertService.findAdvertVmList();

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for(AdvertVm a : advertVmList){

                JSONObject obj = new JSONObject();

                //轮播图ID
                obj.put("advertId", a.getId());
                //商品ID
                obj.put("goodsId", a.getGoodsId());
                //商品标题
                obj.put("businessTitle", a.getBusinessTitle());
                //主图
                obj.put("mainPic", a.getMainPic());
                //轮播图
                obj.put("advertPic", a.getAdvertPic());
                //排序号
                obj.put("sort", a.getSort());
                //类型：1-返佣商品、2-拿货商品
                obj.put("type", a.getType());
                //状态：1-正常、2-禁用、3-删除
                obj.put("status", a.getStatus());
                //操作人名称
                obj.put("modifySysUserName", a.getModifySysUserName());
                //操作时间
                obj.put("modifyDate", sf.format(a.getModifyDate()));

                ja.add(obj);
            }

            JSONObject jb = new JSONObject();
            jb.put("list", ja);
            returnMsg.setDatas(jb);
            returnMsg.setResult("0");
            returnMsg.setMessage(ResultCode.result_0);

        } catch (Exception e) {

            e.printStackTrace();
            returnMsg.setResult("100000");
            returnMsg.setMessage(ResultCode.result_100000);
        }

        return returnMsg;
    }

    /**
     * 新增和编辑轮播图 -206
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/addAdvert")
    public ResponseMessage addAdvert(@RequestBody Map<String, String> map) {

        ResponseMessage returnMsg = null;

        try {

            returnMsg = super.checkLogin(hrdhCacheService, map);

            //如果result为空则直接返回
            Long userId ;
            if (StringUtils.isNotBlank(returnMsg.result)) {
                return returnMsg;
            } else {
                userId = returnMsg.getDatas().getLong("userId");
                returnMsg.setDatas(null);
            }

            //轮播图ID
            String advertIdStr = map.get("advertId");
            Long advertId = null;
            if (StringUtils.isNotBlank(advertIdStr)){
                advertId = Long.parseLong(advertIdStr);
            }

            //商品ID
            String goodsId = map.get("goodsId");
            if(StringUtils.isBlank(goodsId)){
                returnMsg.setResult("206011");
                returnMsg.setMessage(ResultCode.result_206011);
                return returnMsg;
            }

            //轮播图
            String advertPic = map.get("advertPic");
            if(StringUtils.isBlank(advertPic)){
                returnMsg.setResult("206021");
                returnMsg.setMessage(ResultCode.result_206021);
                return returnMsg;
            }

            //排序号
            String sort = map.get("sort");
            if(StringUtils.isBlank(sort)){
                returnMsg.setResult("206031");
                returnMsg.setMessage(ResultCode.result_206031);
                return returnMsg;
            }

            //类型：1-返佣商品、2-拿货商品
            String type = map.get("type");
            if(StringUtils.isBlank(type)){
                returnMsg.setResult("206041");
                returnMsg.setMessage(ResultCode.result_206041);
                return returnMsg;
            }

            Advert advert = null;

            Date date = new Date();

            if(advertId == null){
                advert = new Advert();

                //状态：1-正常、2-禁用、3-删除
                advert.setStatus(1);
                //创建人
                advert.setCreateSysUser(userId);
                //创建时间
                advert.setCreateDate(date);

            }else {
                advert = advertService.get(advertId);
            }

            //商品ID
            advert.setGoodsId(Long.parseLong(goodsId));
            //轮播图
            advert.setAdvertPic(advertPic);
            //排序号
            advert.setSort(Integer.parseInt(sort));
            //类型：1-返佣商品、2-拿货商品
            advert.setType(Integer.parseInt(type));
            //修改人
            advert.setModifySysUser(userId);
            //修改时间
            advert.setModifyDate(date);

            advertService.save(advert);

            returnMsg.setResult("1");
            returnMsg.setMessage(ResultCode.result_10);

        } catch (Exception e) {

            e.printStackTrace();
            returnMsg.setResult("100000");
            returnMsg.setMessage(ResultCode.result_100000);
        }

        return returnMsg;
    }

    /**
     * 修改轮播图状态 -207
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateStatus")
    public ResponseMessage updateStatus(@RequestBody Map<String, String> map) {

        ResponseMessage returnMsg = null;

        try {

            returnMsg = super.checkLogin(hrdhCacheService, map);

            //如果result为空则直接返回
            Long userId ;
            if (StringUtils.isNotBlank(returnMsg.result)) {
                return returnMsg;
            } else {
                userId = returnMsg.getDatas().getLong("userId");
                returnMsg.setDatas(null);
            }

            //检测轮播图ID不能为空
            String advertIds = map.get("advertIds");
            if(StringUtils.isBlank(advertIds)){
                returnMsg.setResult("207010");
                returnMsg.setMessage(ResultCode.result_207010);
                return returnMsg;
            }

            //检测操作类型不能为空：1-正常、2-禁用、3-删除
            String statusStr= map.get("status");
            if(StringUtils.isBlank(statusStr)){
                returnMsg.setResult("207020");
                returnMsg.setMessage(ResultCode.result_207020);
                return returnMsg;
            }

            int status = Integer.parseInt(statusStr);

            //当前时间
            Date date = new Date();

            advertService.updateStatus(advertIds, status, userId, date);

            returnMsg.setResult("1");
            returnMsg.setMessage(ResultCode.result_10);
        } catch (Exception e) {
            e.printStackTrace();
            returnMsg.setResult("100000");
            returnMsg.setMessage(ResultCode.result_100000);
        }

        return returnMsg;
    }
}
