package tk.sandin.test;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import weibo4j.Status;
import weibo4j.Weibo;

public class WeiboTest {
	
	private Weibo fanfou = new Weibo("172339248@qq.com", "19851129");
	private String msg;

	@Before
	public void setUp() throws Exception {
		msg = "test status " + System.currentTimeMillis();
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testGetPublicTimeLine() throws Exception{
		List<Status> status = fanfou.getPublicTimeline();
		for (Status s : status) {
			Assert.assertNotNull(s.getText());
		}
		
		// page 1, count 10
		List<Status> status_10 = fanfou.getFriendsTimeline(1, 10);
		Assert.assertEquals(10, status_10.size());
		
		List<Status> status_5 = fanfou.getFriendsTimeline(2, 5);
		Assert.assertEquals(5, status_5.size());
		
		// page2,5/per index 0 = page1,10/per index 6
		assert(status_10.get(5).equals(status_5.get(0)));
	}
	
	@Test
	public void testGetUserTimeline() throws Exception {
		String userid = fanfou.getUserId();
		List<Status> status = fanfou.getUserTimeline(userid);
		
		Assert.assertNotNull(status);
		
		for ( Status s : status) {
			System.out.println(s.getText());
			//Assert.assertEquals(userid, s.getUser().get());
		}
	}
	
	@Test
	public void testUpdate() throws Exception {

		Status status = fanfou.updateStatus(msg);
		String text = status.getText();
		String id = status.getId();
				
		assertTrue(msg.equals(text));
		
		// clean up
		fanfou.destroyStatus(id);
	}
	
	
	@Test
	public void test() throws Exception {
		fanfou.getFriendsTimeline();
	}

	@Test
	public void testSearch() {
		assertTrue(true);
	}

}
