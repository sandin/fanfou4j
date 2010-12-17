package tk.sandin.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import weibo4j.Status;
import weibo4j.User;
import weibo4j.Weibo;

public class WeiboTest {
	
	private Weibo fanfou = new Weibo("172339248@qq.com", "19851129");
	private String msg;

	@Before
	public void setUp() throws Exception {
		msg = "test status " + new Date();
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
		
		Assert.assertNotNull(status_20);
		Assert.assertTrue(status_20.size() > 3);
		Assert.assertEquals(msg, status_20.get(0).getText());
		
		// test getUserTimeline(page, count)
		List<Status> status_2 = fanfou.getUserTimeline(1, 10);
		Assert.assertEquals(10, status_2.size());
		
		// clear up
		for (Status s : update) {
			fanfou.destroyStatus(s.getId());
		}
	}
	
	@Test
	public void testUpdateStatus() throws Exception {

		Status status = fanfou.updateStatus(msg);
		String text = status.getText();
		String id = status.getId();
				
		assertTrue(msg.equals(text));
		
		User user = fanfou.showUser("lds2012");
//		System.out.println(user.getStatusText());
		
		// reply to someone
		Status repay = fanfou.updateStatus("@" + user.getName() + " " + msg, user.getStatusId());
		System.out.println(repay);
		System.out.println(user.getStatusId());
		
		//Assert.assertEquals(user.getId(), repay.getInReplyToUserId());
		
		// clean up
		fanfou.destroyStatus(id);
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
		for (Status s : status) {
			//System.out.println(s.getText());
		}
		
		Assert.assertNotNull(status);
		Assert.assertEquals(message, status.get(0).getText());
		assertTrue(new_mention.equals(status.get(0)));
		
		// update the newest status
		Status newest_mention = fanfou.updateStatus(message + "other message");
		// get the newest mentions list
		List<Status> new_mentions = fanfou.getMentions(new_mention.getId());
		
		// just one status(the newest status)
		Assert.assertEquals(1, new_mentions.size());
		assertTrue(new_mentions.get(0).equals(newest_mention));
		
		
	}

	@Test
	@Ignore
	public void testSearch() {
		//TODO: fanfou search server is not ready.
		assertTrue(true);
	}

}
