package com.cppteam.xcx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cppteam.common.util.*;
import com.cppteam.mapper.UserMapper;
import com.cppteam.pojo.User;
import com.cppteam.pojo.UserExample;
import com.cppteam.xcx.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

/**
 * 微信小程序登录接口实现
 * @author happykuan
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Value("${APP_ID}")
	private String APP_ID;
	@Value("${APP_SECRET}")
	private String APP_SECRET;
	@Value("${GRANT_TYPE}")
	private String GRANT_TYPE;
	@Value("${SESSION_KEY_URL}")
	private String SESSION_KEY_URL;

	@Value("${DEFAULT_NULL}")
	private String DEFAULT_NULL;
	@Value("${AVATAR_THUMB_DEFAULT_WIDTH}")
	private Integer AVATAR_THUMB_DEFAULT_WIDTH;
	@Autowired
	private UserMapper userMapper;
	/**
	 * 根据code获取用户登录token
	 */
	@Override
	public TripResult getToken(String code) {
		// code 换取session_key
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", APP_ID);
		params.put("secret", APP_SECRET);
		params.put("js_code", code);
		params.put("grant_type", GRANT_TYPE);
		String resJson = HttpClientUtil.doPost(SESSION_KEY_URL, params);
		
		// 将json字符串转成map
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map = gson.fromJson(resJson, map.getClass());
		
		// 成功获取openID, 返回token
		if (map.containsKey("openid")) {
			String openid = (String) map.get("openid");
			UserExample userExample = new UserExample();
			UserExample.Criteria criteria = userExample.createCriteria();
			criteria.andOpenidEqualTo(openid);
			List<User> users = userMapper.selectByExample(userExample);

			Map<String, String> tokenMap = new HashMap<String, String>();
			// 新使用用户
			if (users.isEmpty()) {
				String userId = IDGenerator.createUserId();
				User user = new User();
				user.setId(userId);
				user.setOpenid(openid);
				user.setNickname("default");
				user.setAvatar("default");
				user.setAvaterThumb("default");
				userMapper.insertSelective(user);
				tokenMap.put("token", JWTUtil.generateToken(userId));
			} else {
				tokenMap.put("token", JWTUtil.generateToken(users.get(0).getId()));
			}

			return TripResult.ok("ok", tokenMap);
		}
		return TripResult.build(405, (String) map.get("errmsg"));
	}


	/**
	 * 根据token检测用户是否合法
	 */
	@Override
	public TripResult checkLoginStatus(String token) {
		String openid = JWTUtil.validToken(token);
		if (StringUtils.isBlank(openid)) {
			return TripResult.build(400, "token无效");
		}
		return TripResult.ok("ok", openid);
	}

	@Override
	public TripResult getToken1(String encryptedData, String iv, String code) {
		//登录凭证不能为空
		if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(iv) || StringUtils.isBlank(code)) {
			return TripResult.build(400, "缺少参数");
		}
		// code 换取session_key
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", APP_ID);
		params.put("secret", APP_SECRET);
		params.put("js_code", code);
		params.put("grant_type", GRANT_TYPE);
		String resJson = HttpClientUtil.doPost(SESSION_KEY_URL, params);
		// 将json字符串转成map
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map = gson.fromJson(resJson, map.getClass());

		if (!map.containsKey("openid")) {
			//获取失败,返回微信给的错误码和提示信息
			return TripResult.build((Integer) map.get("errcode"), (String) map.get("errmsg"));
		}
		//返回正确
			//获取会话密钥（session_key）
			String session_key = (String) map.get("session_key");
			//用户的唯一标识（openid）
			String openid = (String) map.get("openid");
			//////////////// 对encryptedData加密数据进行AES解密 ////////////////
			String result = null;
			try {
				result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return TripResult.build(400, "解密失败");
			}
			if (StringUtils.isBlank(result)) {
				return TripResult.build(400, "解密为null");
			}
			//解密成功,获得信息
			Map<String, String> userInfoJSON = new HashMap<>();
			userInfoJSON = gson.fromJson(result, userInfoJSON.getClass());

			User newUserInfo = new User();
			newUserInfo.setOpenid(userInfoJSON.get("openId"));
			newUserInfo.setNickname(userInfoJSON.get("nickName"));
			newUserInfo.setSex(userInfoJSON.get("gender"));
			newUserInfo.setProvince(userInfoJSON.get("province"));
			newUserInfo.setCity(userInfoJSON.get("city"));
			newUserInfo.setCountry(userInfoJSON.get("country"));
			newUserInfo.setUnionid(userInfoJSON.get("unionId"));
			//获取新头像
			String url = userInfoJSON.get("avatarUrl");
			String avatar = ImageUtils.saveImage(url);
			String avatarThumb = ImageUtils.saveImage(ImageUtils.thumbnailImage(url, AVATAR_THUMB_DEFAULT_WIDTH), null);
			newUserInfo.setAvatar(avatar);
			newUserInfo.setAvaterThumb(avatarThumb);

			//通过openId,unionId判断是否存在user,符合其中之一即可
			UserExample userExample = new UserExample();
			userExample.or().andOpenidEqualTo(openid);
			userExample.or().andUnionidEqualTo(newUserInfo.getUnionid());

			List<User> useres = userMapper.selectByExample(userExample);
			if (useres.isEmpty()) {
				//新用户,注册信息到数据库
				newUserInfo.setId(IDGenerator.createUserId());
				userMapper.insertSelective(newUserInfo);
			} else {
				//老用户,更新信息到数据库
				// 删除旧头像
				User oldUserInfo = useres.get(0);
				String oldAvatar = oldUserInfo.getAvatar();
				String oldAvatarThumb = oldUserInfo.getAvaterThumb();
				if (!DEFAULT_NULL.equals(oldAvatar)) {
					ImageUtils.deleteImage(oldAvatar);
				}
				if (!DEFAULT_NULL.equals(oldAvatarThumb)) {
					ImageUtils.deleteImage(oldAvatarThumb);
				}

				newUserInfo.setId(useres.get(0).getId());
				userMapper.updateByPrimaryKeySelective(newUserInfo);
			}
			Map<String, String> tokenMap = new HashMap<String, String>(1);
			tokenMap.put("token", JWTUtil.generateToken(newUserInfo.getId()));
			return TripResult.ok("ok", tokenMap);

	}
}
