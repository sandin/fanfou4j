package weibo4j;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
	public void test(){}

}
