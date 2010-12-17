package weibo4j;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WeiboTest {
	
	private Weibo fanfou;

	@Before
	public void setUp() throws Exception {
		fanfou = new Weibo("172339248@qq.com", "19851129");
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() {
		assertTrue(true);
	}

}
