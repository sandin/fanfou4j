package weibo4j;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.notification.Failure;

public class WeiboTest {
	
	private Weibo fanfou = new Weibo("172339248@qq.com", "19851129");
	private String msg;
	private static final String TO_USER_NAME = "lds2012";
	private String AT_TO;

	@Before
	public void setUp() throws Exception {
		msg = "test status " + new Date();
		AT_TO = "@" + fanfou.showUser(TO_USER_NAME).getName() + " ";
	}

	@After
	public void tearDown() throws Exception {
		
	}
	

	@Test
	@Ignore
	public void testGetPublicTimeLine() throws Exception{
		List<Status> status = fanfou.getPublicTimeline();
		for (Status s : status) {
			Assert.assertNotNull(s.getText());
		}
	}
	
	@Test
	@Ignore
	public void testGetFrientsTimeline() throws Exception {
		// page 1, count 10
		List<Status> status_10 = fanfou.getFriendsTimeline(1, 10);
		Assert.assertEquals(10, status_10.size());
		
		// page 2, conut 5
		List<Status> status_5 = fanfou.getFriendsTimeline(2, 5);
		Assert.assertEquals(5, status_5.size());
		
		// 取page 1, count 10的第6条 等同于 取page 2, count5的第1条
		assert(status_10.get(5).equals(status_5.get(0)));
	}
	
	@Test
	@Ignore
	public void testGetUserTimeline() throws Exception {
		
		// update status list
		List<Status> update = new ArrayList<Status>();
		
		// update 3 test status
		for (int i = 3; i > 0; i--) {
			update.add(fanfou.updateStatus(msg));
		}
		Assert.assertEquals(3, update.size());
		
		// get user time line by user(myself) id
		String userid = fanfou.getUserId();
		List<Status> status_20 = fanfou.getUserTimeline(userid);
		
		Assert.assertFalse(status_20.isEmpty());
		Assert.assertTrue(status_20.size() > 3);
		Assert.assertEquals(msg, status_20.get(0).getText());
		
		// test getUserTimeline(page, count)
		List<Status> status_2 = fanfou.getUserTimeline(1, 3);
		Assert.assertEquals(3, status_2.size());
		
		// clear up
		for (Status s : update) {
			fanfou.destroyStatus(s.getId());
		}
	}

	@Test
	@Ignore
	public void testShowStatus() throws Exception {
		// get one status by status id
		Status status = fanfou.updateStatus(msg);
		Status status_show = fanfou.showStatus(status.getId());
		
		assertTrue(status.equals(status_show));
		
		// clean up
		fanfou.destroyStatus(status.getId());
	}
	
	@Test
	@Ignore
	public void testGetMentions() throws Exception {
		User myself = fanfou.showUser(fanfou.getUserId());
		
		// repay to user(myself)
		String message = "@" + myself.getScreenName()  + " "+ msg;
		Status new_mention = fanfou.updateStatus(message);
		
		// get mention status of user
		List<Status> status = fanfou.getMentions();
//		for (Status s : status) {
//			System.out.println(s.getText());
//		}
		
		Assert.assertFalse(status.isEmpty());
		Assert.assertEquals(message, status.get(0).getText());
		assertTrue(new_mention.equals(status.get(0)));
		
		// update the newest status
		Status newest_mention = fanfou.updateStatus(message + " new message");
		// get the newest mentions list
		List<Status> new_mentions = fanfou.getMentions(new_mention.getId());
		
		// just one status(the newest status)
		Assert.assertEquals(1, new_mentions.size());
		assertTrue(new_mentions.get(0).equals(newest_mention));
		
		// clearup
		fanfou.destroyStatus(new_mention.getId());
		fanfou.destroyStatus(newest_mention.getId());
	}
	
	@Test
	@Ignore
	public void testUpdateStatus() throws Exception {

		Status status = fanfou.updateStatus(msg);
		String text = status.getText();
		String id = status.getId();
				
		assertTrue(msg.equals(text));
		
		User user = fanfou.showUser(TO_USER_NAME);
//		System.out.println(user.getStatusText());
		
		// reply to someone
		Status repay = fanfou.updateStatus("@" + user.getName() + " " + msg, user.getStatusId());
		Assert.assertEquals(
			user.getStatusId(),
			repay.getInReplyToStatusId()
		);
		Assert.assertEquals(user.getId(), repay.getInReplyToUserId());
		
		// clean up
		fanfou.destroyStatus(id);
		fanfou.destroyStatus(repay.getId());
	}
	
	@Test
	@Ignore
	public void testRepost() throws Exception {
		User user = fanfou.showUser(fanfou.getUserId());
		String repost_status_id = user.getStatusId();
		String repost_status_text = user.getStatusText();
		String message = msg + "repost to " + TO_USER_NAME;
		
		Status repost = fanfou.repost(repost_status_id, message);
		String text = repost.getText();
		
//		System.out.println(text);
//		System.out.println(repost);
//		System.out.println(repost_status_text);
//		System.out.println("123".contains("123"));
		
		// new status contains the origin status text
		assertTrue(text.contains(repost_status_text));
		assertTrue(text.contains("@" + user.getName()));
		
		// clean up
		fanfou.destroyStatus(repost.getId());
	}
	
	@Test
	@Ignore
	public void testDestroyStatus() throws Exception {
		
		// update some status in case have no status
		fanfou.updateStatus(msg);
		fanfou.updateStatus(msg);
		
		User myself = fanfou.showUser(fanfou.getUserId());
		List<Status> status = fanfou.getUserTimeline();
		int all_status_count = myself.getStatusesCount();
		int page = (int) Math.ceil( all_status_count / 20.0 );
		
		int delete_count = 0;
		
		// delete status as much as API can
		for (int i = 1; i <= page; i++) {
			List<Status> status_per = fanfou.getUserTimeline(i, 20);
			for (Status s : status_per) {
//				System.out.println(s.getText());
				fanfou.destroyStatus(s.getId());
				delete_count++;
			}
		}
		
		List<Status> status_left = fanfou.getUserTimeline();
//		System.out.println(status.size() - status_left.size() == delete_count );
//		System.out.println(status.size());
//		System.out.println(status_left.size());
//		System.out.println(delete_count);
		Assert.assertTrue(status.size() - status_left.size() == delete_count );
		
		// update one status in case 
		fanfou.updateStatus("clean up on " + new Date());
	}
	
	@Test
	@Ignore
	public void testUploadPhoto() throws Exception {
		// 上传照片
		FileInputStream file = new FileInputStream("fanfou.jpg");
		// big image for test origin image url
//		FileInputStream file = new FileInputStream("big_img.jpg");
		Status status = fanfou.uploadPhoto(msg, file);
//		System.out.println(status);
		
		String original_pic = status.getOriginal_pic();
		String bmiddle_pic = status.getBmiddle_pic();
		String thumbail_pic = status.getThumbnail_pic();
		
		assertTrue(!original_pic.isEmpty());
		assertTrue(!bmiddle_pic.isEmpty());
		assertTrue(!thumbail_pic.isEmpty());
		
		fanfou.destroyStatus(status.getId());
	}
	
	@Test
	@Ignore
	public void testSearch() throws Exception {
		
		QueryResult result = fanfou.search(new Query("哈"));
		
		// search result is not null
		List<Status> status = result.getStatus();
		assertTrue(!status.isEmpty());
	
//		System.out.println(status.size());
//		for (Status s : status) {
//			System.out.println(s.getText());
//		}
	}
		
	
	@Test
	@Ignore
	//TODO: fanfou API's trends server is not ready. 
	public void testGetTrends() throws Exception {
		Trends trends = fanfou.getTrends();
		assertTrue(trends.getAsOf() instanceof Date);
		
		Trend[] trend_array = trends.getTrends();
//		for (Trend t : trend_array) {
//			System.out.print(t);
//		}
		
		// get nothing 'cause this API is down 
		assertTrue( trend_array.length == 0 );
	}
	
	@Test
	@Ignore
	public void testGetFirendStatus() throws Exception {
		List<User> users = fanfou.getFriendsStatuses();
		
		// The user has one friend at least
		assertTrue(users.size() > 1);
		assertTrue(! users.get(0).getId().isEmpty());
		
		// all users' name
//		for (User u : users) {
//			System.out.println(u.getName());
//		}
		
		// Get the other user's friends 
		User san = fanfou.showUser(TO_USER_NAME);
		
		// Test by user id
		
		List<User> users_by_id = fanfou.getFriendsStatuses(san.getId());
		
		assertTrue(users_by_id.size() > 1);
		for (User u : users_by_id) {
//			System.out.println(u.getName());
			assertTrue(! u.getName().isEmpty() );
		}
		
		// Test by user id and paging
		
		User fan = fanfou.showUser("wangxing");
		assertTrue(! fan.getName().isEmpty());
		
	    int friend_count = fan.getFriendsCount();
		
		List<User> users_by_page_1 = fanfou.getFriendsStatuses(fan.getId(), 
				new Paging(1));
		
		// All friends' name
//		for (User u : users_by_page_1 ) {
//			System.out.println(u.getName());
//		}
		
		// The user(王兴) has 50 friends at least
		System.out.println(users_by_page_1.size());
		assertTrue( users_by_page_1.size() > 50 );
	}
	
	@Test
	@Ignore
	public void testGetFollowersStatuses() throws Exception {
		
		// default user 
		List<User> followers = fanfou.getFollowersStatuses();
		
		// I only have few followers(more then one)
//		assertTrue(followers.size() > 1);
		
		// User(fanfou)
		User fan = fanfou.showUser("fanfou");
		
		List<User> followers_of_fanfou = fanfou.getFollowersStatuses(fan.getId());
		List<User> followers_of_fanfou_page_2 = fanfou.getFollowersStatuses(fan.getId(), new Paging(2));
		
		// He has a lot of followers
		assertTrue(followers_of_fanfou.size() > 50);
		assertTrue(! followers_of_fanfou.get(5).getName().isEmpty());
		
		// He definitely has two page of followers
		assertTrue(followers_of_fanfou_page_2.size() > 50);
	}

	@Test
	//@Ignore
	public void testShowUser() throws Exception {
		
		// update a status for test user's last status
		fanfou.updateStatus(msg);
		
		// need to test(not all)
		Map<String, Object> expect = new HashMap<String, Object>();
		expect.put("id", "aFanfou");
		expect.put("name", "aFanfou");
		expect.put("screen_name", "aFanfou");
		expect.put("location", "湖北 武汉");
		expect.put("gender", "男");
		expect.put("birthday", "2011-01-01");
		expect.put("description", "fanfou android app");
		expect.put("url", new URL("http://www.sandin.tk"));
		expect.put("created_at", new Date("Fri Dec 10 06:41:43 +0000 2010"));
		expect.put("utc_offset", 28800);
		expect.put("following", false);
		expect.put("status_text", msg);
		expect.put("is_protected", false);
		expect.put("notifications", false);
		
		// not for sure
		expect.put("profile_image_url", new URL("http://avatar1.fanfou.com/s0/00/00/00.jpg")); //default photo
		expect.put("source", "API");
//		expect.put("friends_count", 3);
//		expect.put("followers_count", 0);
//		expect.put("favourites_count", 0);
//		expect.put("statuses_count", 16);
		
		// get user info
		User myself = fanfou.showUser(fanfou.getUserId());
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("id", myself.getId());
		map.put("screen_name", myself.getScreenName());
		map.put("location",  myself.getLocation());
		map.put("gender", myself.getGender());
		map.put("birthday", myself.getBirthday());
		map.put("description",  myself.getDescription());
		map.put("profile_image_url", myself.getProfileImageURL());
		map.put("url", myself.getURL());
		map.put("followers_count", myself.getFollowersCount());
		map.put("favourites_count",  myself.getFavouritesCount());
		map.put("statuses_count", myself.getStatusesCount());
		map.put("is_protected",  myself.isProtected());
		map.put("created_at", myself.getCreatedAt());
		map.put("following", myself.isFollowing());
		map.put("notifications", myself.isNotificationEnabled());
		map.put("utc_offset", myself.getUtcOffset());
		
		//The last status
		map.put("status_created_at", myself.getStatusCreatedAt());
		map.put("status_id", myself.getStatusId());
		map.put("status_text", myself.getStatusText());
		map.put("status_source", myself.getStatusSource());
		map.put("status_trencated", myself.isStatusTruncated());
		map.put("status_in_reply_to_status_id", myself.getStatusInReplyToStatusId());
		map.put("status_in_reply_to_user_id", myself.getStatusInReplyToUserId());
		map.put("status_in_reply_to_screen_name", myself.getStatusInReplyToScreenName());
		map.put("status_favorited", myself.isStatusFavorited());
		
		// TEST ALL
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object value = map.get(key);
			Object expect_value = expect.get(key);
			if (value != null && expect_value != null ) {
				assertTrue(expect.get(key).equals(value));
//				System.out.println(expect.get(key).equals(value));
//				System.out.println(expect_value);
//				System.out.println(value);
//				System.out.println("--------------------------------");
			} else if ( value == null ) {
//				System.out.println(key + " is NULL...............");
//				System.out.println("--------------------------------");
			} else if (expect_value == null) {
//				System.out.println(key + " is not been tested.");
//				System.out.println("--------------------------------");
			} else {
//				System.out.println(key + " [skip].");
//				System.out.println("--------------------------------");
				fail(key + " is skip.");
			}
		}
	}
	
	@Test
	@Ignore
	public void test() throws Exception {}
}
