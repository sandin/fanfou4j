package weibo4j;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
	
	/**
	 * 好友帐号ID， 必须是好友关系， 与已登录帐号互相关注
	 */
	private static final String TO_USER_ID = "lds2012";
	private String AT_TO;
	private String msg;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	// 验证用户
	// 错误的用户/密码
	// 警告： 使用错误用户/密码登录会导致帐号被锁定，该测试不能在时间段内运行超过3次
	@Test
	public void testVerifyCredentials() {
		
		try {
			
			Weibo fanfou = new Weibo("172339248@qq.com", "19851129a");
			User user = fanfou.verifyCredentials();
			
		} catch (WeiboException e) {
			
			switch (e.getStatusCode()) {
				case 401 :
					System.out.println("not a user!");
					break;
				case 200 :
					fail();
					System.out.println("is a user!");
					break;
				default:
					fail();
			}
			
		}
			
	}
	
	// 验证用户
	@Test
	public void testVerifyCredentials2() {
		
		// 正确的用户/密码
		try {
			
			Weibo fanfou = new Weibo("172339248@qq.com", "19851129");
			User user = fanfou.verifyCredentials();
			
		} catch (WeiboException e) {
			
			switch (e.getStatusCode()) {
				case 401 :
					System.out.println("not a user!");
					fail();
					break;
				case 200 :
					System.out.println("is a user!");
					break;
				default:
					fail();
			}
			
		}
		
	}

}
