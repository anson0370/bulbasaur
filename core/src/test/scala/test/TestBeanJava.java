package test;

/**
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-12-14
 */
public class TestBeanJava {
	public void testChangeParamMethod(Object... o) {
		for (Object o1 : o) {
			System.out.println(o1);
		}
	}
}
