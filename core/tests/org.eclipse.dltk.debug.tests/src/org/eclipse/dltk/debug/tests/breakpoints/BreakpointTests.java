package org.eclipse.dltk.debug.tests.breakpoints;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;

import junit.framework.TestSuite;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptLineBreakpoint;
import org.eclipse.dltk.debug.tests.AbstractDebugTests;
import org.eclipse.dltk.internal.debug.core.model.AbstractScriptBreakpoint;
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public class BreakpointTests extends AbstractDebugTests {
	public static TestSuite suite() {
		return new Suite(BreakpointTests.class);
	}

	public BreakpointTests() {
		super("org.eclipse.dltk.debug.tests", "My Breakpoint tests");
	}

	private IScriptLineBreakpoint breakpoint;

	public void setUpSuite() throws Exception {
		super.setUpSuite();

		IResource resource = scriptProject.getProject().findMember(
				"src/test.rb");

		breakpoint = new ScriptLineBreakpoint("test_debug_model", resource,
				resource.getLocation(), 1, -1, -1, true);
	}

	public void tearDownSuite() throws Exception {
		super.tearDownSuite();
	}

	// Helper methods
	protected String getProjectName() {
		return "debug";
	}

	private static IDbgpSession createDbgpSessionMock() {
		final InvocationHandler handler = new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				throw new UnsupportedOperationException("Mock called "
						+ method.getName());
			}

		};
		return (IDbgpSession) Proxy.newProxyInstance(BreakpointTests.class
				.getClassLoader(), new Class[] { IDbgpSession.class }, handler);
	}

	// Real tests
	public void testSetGet() throws Exception {
		final IDbgpSession sessionMock = createDbgpSessionMock();
		// Id
		final String id = "32145";
		breakpoint.setId(sessionMock, id);
		assertEquals(id, breakpoint.getId(sessionMock));

		// HitCount
		final int hitCount = 234;
		assertEquals(-1, breakpoint.getHitCount(sessionMock));
		breakpoint.setHitCount(sessionMock, hitCount);
		assertEquals(hitCount, breakpoint.getHitCount(sessionMock));

		// Expression state
		assertEquals(false, breakpoint.getExpressionState());
		breakpoint.setExpressionState(true);
		assertEquals(true, breakpoint.getExpressionState());

		// Expression
		final String expression = "x + y > 3245";
		assertNull(breakpoint.getExpression());
		breakpoint.setExpression(expression);
		assertEquals(expression, breakpoint.getExpression());

		// Hit condition
		final int hitCondition = IScriptBreakpoint.HIT_CONDITION_EQUAL;
		assertEquals(-1, breakpoint.getHitCondition());
		breakpoint.setHitCondition(hitCondition);
		assertEquals(hitCondition, breakpoint.getHitCondition());

		// Hit value
		final int hitValue = 22;
		assertEquals(-1, breakpoint.getHitValue());
		breakpoint.setHitValue(hitValue);
		assertEquals(hitValue, breakpoint.getHitValue());
	}

	public void testMakeUri() throws UnsupportedEncodingException {
		assertEquals("file:///" + URLEncoder.encode("[1]", "UTF-8"), String
				.valueOf(AbstractScriptBreakpoint.makeUri(new Path("[1]"))));
	}
}
