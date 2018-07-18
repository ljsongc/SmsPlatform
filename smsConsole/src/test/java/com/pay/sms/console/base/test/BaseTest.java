package com.pay.sms.console.base.test;

import java.util.Collection;
import java.util.Iterator;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class BaseTest {
	/**
	 * 遍历bean集合
	 * @param beans
	 */
	protected void foreachPrint(Collection<?> beans) {
		Iterator<?> it = beans.iterator();
		for (; it.hasNext();) {
			System.out.println(it.next());
		}
	}
}
