/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.testing.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.testing.TestCategoryEngineManager;
import org.eclipse.dltk.internal.testing.launcher.NullTestRunnerUI;
import org.eclipse.dltk.internal.testing.launcher.NullTestingEngine;
import org.eclipse.dltk.internal.testing.model.TestElement.Status;
import org.eclipse.dltk.testing.DLTKTestingConstants;
import org.eclipse.dltk.testing.DLTKTestingMessages;
import org.eclipse.dltk.testing.DLTKTestingPlugin;
import org.eclipse.dltk.testing.ITestCategoryEngine;
import org.eclipse.dltk.testing.ITestRunnerUI;
import org.eclipse.dltk.testing.ITestSession;
import org.eclipse.dltk.testing.ITestingClient;
import org.eclipse.dltk.testing.ITestingEngine;
import org.eclipse.dltk.testing.MessageIds;
import org.eclipse.dltk.testing.TestCategoryDescriptor;
import org.eclipse.dltk.testing.model.ITestElement;
import org.eclipse.dltk.testing.model.ITestElementContainer;
import org.eclipse.dltk.testing.model.ITestElementPredicate;
import org.eclipse.dltk.testing.model.ITestRunSession;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * A test run session holds all information about a test run, i.e.
 * launch configuration, launch, test tree (including results). 
 */
public class TestRunSession implements ITestRunSession, ITestSession {

	/**
	 * The launch, or <code>null</code> iff this session was run externally.
	 */
	private final ILaunch fLaunch;
	private final String fTestRunName;
	/**
	 * Java project, or <code>null</code>.
	 */
	private final IScriptProject fProject;

	private final ITestingEngine fTestingEngine;
	private final ITestRunnerUI testRunnerUI;
	private final ITestCategoryEngine[] categoryEngines;
	
	/**
	 * Test runner client or <code>null</code>.
	 */
	private ITestRunnerClient fTestRunnerClient;

	private final ListenerList/*<ITestSessionListener>*/ fSessionListeners;
	
	/**
	 * The model root, or <code>null</code> if swapped to disk.
	 */
	private TestRoot fTestRoot;
	
	/**
	 * The test run session's cached result, or <code>null</code> if <code>fTestRoot != null</code>.
	 */
	private Result fTestResult;
	
	/**
	 * Map from testId to testElement.
	 */
	private Map<String, TestElement> fIdToTest;
	
	/**
	 * test categories
	 */
	private Map<String, TestCategoryElement> fCategoryMap;

	/**
	 * The TestSuites for which additional children are expected. 
	 */
	private List<IncompleteTestSuite> fIncompleteTestSuites;
	
	/**
	 * Suite for unrooted test case elements, or <code>null</code>.
	 */
	private TestSuiteElement fUnrootedSuite;
	
 	/**
 	 * Number of tests started during this test run.
 	 */
	volatile int fStartedCount;
	/**
	 * Number of tests ignored during this test run.
	 */
	volatile int fIgnoredCount;
	/**
	 * Number of errors during this test run.
	 */
	volatile int fErrorCount;
	/**
	 * Number of failures during this test run.
	 */
	volatile int fFailureCount;
	/**
	 * Total number of tests to run.
	 */
	volatile int fTotalCount;
	/**
	 * Number of created test cases.
	 */
	volatile int fCreatedTestCaseCount;
	/**
	 * Start time in millis.
	 */
	volatile long fStartTime;
	volatile boolean fIsRunning;
	
	volatile boolean fIsStopped;
	

	/**
	 * @param testRunName
	 * @param project may be <code>null</code>
	 */
	public TestRunSession(String testRunName, IScriptProject project) {
		//TODO: check assumptions about non-null fields

		fLaunch= null;
		fProject= null; //TODO
		
		Assert.isNotNull(testRunName);
		fTestRunName= testRunName;
		fTestingEngine= NullTestingEngine.getInstance();
		testRunnerUI= NullTestRunnerUI.getInstance();
		categoryEngines = null;
		
		fTestRoot= new TestRoot(this);
		fIdToTest = new HashMap<String, TestElement>();
		fCategoryMap = new HashMap<String, TestCategoryElement>();
		
		fTestRunnerClient= null;

		fSessionListeners= new ListenerList();
	}
	
	
	public TestRunSession(ILaunch launch, IScriptProject project,
			ITestRunnerClient runnerClient) {
		Assert.isNotNull(launch);
		Assert.isNotNull(runnerClient);
		
		fLaunch= launch;
		fProject= project;
		
		ILaunchConfiguration launchConfiguration= launch.getLaunchConfiguration();
		if (launchConfiguration != null) {
			fTestRunName= launchConfiguration.getName();
			fTestingEngine= DLTKTestingConstants.getTestingEngine(launchConfiguration);
			testRunnerUI= fTestingEngine.getTestRunnerUI(project, launchConfiguration);
			categoryEngines = TestCategoryEngineManager
					.getCategoryEngines(testRunnerUI);
		} else {
			fTestRunName= project.getElementName();
			fTestingEngine= NullTestingEngine.getInstance();
			testRunnerUI= NullTestRunnerUI.getInstance();
			categoryEngines = null;
		}
		
		fTestRoot= new TestRoot(this);
		fIdToTest = new HashMap<String, TestElement>();
		fCategoryMap = new HashMap<String, TestCategoryElement>();
		
		fTestRunnerClient= runnerClient;
		fTestRunnerClient.startListening(new TestSessionNotifier());
		
		final ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		launchManager.addLaunchListener(new ILaunchesListener2() {
			public void launchesTerminated(ILaunch[] launches) {
				if (Arrays.asList(launches).contains(fLaunch)) {
					if (fTestRunnerClient != null) {
						fTestRunnerClient.stopWaiting();
					}
					launchManager.removeLaunchListener(this);
				}
			}
			public void launchesRemoved(ILaunch[] launches) {
				if (Arrays.asList(launches).contains(fLaunch)) {
					if (fTestRunnerClient != null) {
						fTestRunnerClient.stopWaiting();
					}
					launchManager.removeLaunchListener(this);
				}
			}
			public void launchesChanged(ILaunch[] launches) {
			}
			public void launchesAdded(ILaunch[] launches) {
			}
		});

		fSessionListeners= new ListenerList();
		addTestSessionListener(new TestRunListenerAdapter(this));
	}
	
	void reset() {
		fStartedCount= 0; 
		fFailureCount= 0; 
		fErrorCount= 0; 
		fIgnoredCount= 0; 
		fTotalCount= 0;
		
		fTestRoot= new TestRoot(this);
		fTestResult= null;
		fIdToTest = new HashMap<String, TestElement>();
		fCategoryMap = new HashMap<String, TestCategoryElement>();
	}

	/*
	 * @see org.eclipse.dltk.testing.model.ITestElement#getId()
	 */
	public String getId() {
		return fTestRunName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.ITestRunSession#getProgressState()
	 */
	public ProgressState getProgressState() {
		if (isRunning()) {
			return ProgressState.RUNNING;
		}
		if (isStopped()) {
			return ProgressState.STOPPED;
		}
		return ProgressState.COMPLETED;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.model.ITestElement#getTestResult(boolean)
	 */
	public Result getTestResult(boolean includeChildren) {
		if (fTestRoot != null) {
			return fTestRoot.getTestResult(true);
		} else {
			return fTestResult;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.model.ITestElementContainer#getChildren()
	 */
	public ITestElement[] getChildren() {
		return getTestRoot().getChildren();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.model.ITestElement#getFailureTrace()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getFailureTrace()
	 */
	public FailureTrace getFailureTrace() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.model.ITestElement#getParentContainer()
	 */
	public ITestElementContainer getParentContainer() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.junit.model.ITestElement#getTestRunSession()
	 */
	public ITestRunSession getTestRunSession() {
		return this;
	}
	
	
	public TestRoot getTestRoot() {
		swapIn(); //TODO: TestRoot should stay (e.g. for getTestRoot().getStatus())
		return fTestRoot;
	}

	/**
	 * @return the Java project, or <code>null</code>
	 */
	public IScriptProject getLaunchedProject() {
		return fProject;
	}
	
	public ITestingEngine getTestingEngine() {
		return fTestingEngine;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getLaunch()
	 */
	public ILaunch getLaunch() {
		return fLaunch;
	}
	
	public String getTestRunName() {
		return fTestRunName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getErrorCount()
	 */
	public int getErrorCount() {
		return fErrorCount;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getFailureCount()
	 */
	public int getFailureCount() {
		return fFailureCount;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getStartedCount()
	 */
	public int getStartedCount() {
		return fStartedCount;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getIgnoredCount()
	 */
	public int getIgnoredCount() {
		return fIgnoredCount;
	}
	
	public int getTotalCount() {
		return fTotalCount;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#setTotalCount(int)
	 */
	public void setTotalCount(int count) {
		this.fTotalCount = count;
//		System.out.println("COUNT:" + count);
	}

	/**
	 * @param value
	 */
	protected void adjustTotalCount(int value) {
		if (value > fTotalCount) {
			fTotalCount = value;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#getStartTime()
	 */
	public long getStartTime() {
		return fStartTime;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#isStopped()
	 */
	public boolean isStopped() {
		return fIsStopped;
	}

	public void addTestSessionListener(ITestSessionListener listener) {
		swapIn();
		fSessionListeners.add(listener);
	}
	
	public void removeTestSessionListener(ITestSessionListener listener) {
		fSessionListeners.remove(listener);
	}
	
	public void swapOut() {
		if (fTestRoot == null)
			return;
		if (isRunning() || isStarting() || isKeptAlive())
			return;
		
		Object[] listeners= fSessionListeners.getListeners();
		for (int i= 0; i < listeners.length; ++i) {
			ITestSessionListener registered= (ITestSessionListener) listeners[i];
			if (! registered.acceptsSwapToDisk())
				return;
		}
		
		try {
			File swapFile= getSwapFile();
			
			DLTKTestingModel.exportTestRunSession(this, swapFile);
			fTestResult= fTestRoot.getTestResult(true);
			fTestRoot= null;
			fTestRunnerClient= null;
			fIdToTest = new HashMap<String, TestElement>();
			fCategoryMap = new HashMap<String, TestCategoryElement>();
			fIncompleteTestSuites= null;
			fUnrootedSuite= null;
			
		} catch (IllegalStateException e) {
			DLTKTestingPlugin.log(e);
		} catch (CoreException e) {
			DLTKTestingPlugin.log(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#isStarting()
	 */
	public boolean isStarting() {
		return getStartTime() == 0 && fLaunch != null && ! fLaunch.isTerminated();
	}


	public void removeSwapFile() {
		File swapFile= getSwapFile();
		if (swapFile.exists())
			swapFile.delete();
	}

	private File getSwapFile() throws IllegalStateException {
		File historyDir= DLTKTestingPlugin.getHistoryDirectory();
		String isoTime= new SimpleDateFormat("yyyyMMdd-HHmmss.SSS").format(new Date(getStartTime())); //$NON-NLS-1$
		String swapFileName= isoTime + ".xml"; //$NON-NLS-1$
		return new File(historyDir, swapFileName);
	}


	public void swapIn() {
		if (fTestRoot != null)
			return;

		try {
			DLTKTestingModel.importIntoTestRunSession(getSwapFile(), this);
		} catch (IllegalStateException e) {
			DLTKTestingPlugin.log(e);
			fTestRoot= new TestRoot(this);
			fTestResult= null;
		} catch (CoreException e) {
			DLTKTestingPlugin.log(e);
			fTestRoot= new TestRoot(this);
			fTestResult= null;
		}
	}

	public void stopTestRun() {
		if (isRunning() || ! isKeptAlive())
			fIsStopped= true;
		if (fTestRunnerClient != null)
			fTestRunnerClient.stopTest();
	}

	/**
	 * @return <code>true</code> iff the runtime VM of this test session is still alive 
	 */
	public boolean isKeptAlive() {
		if (fTestRunnerClient != null
				&& fLaunch != null
				&& fTestRunnerClient.isRunning()
				&& ILaunchManager.DEBUG_MODE.equals(fLaunch.getLaunchMode())) {
			ILaunchConfiguration config= fLaunch.getLaunchConfiguration();
			try {
				return config != null
				&& config.getAttribute(DLTKTestingConstants.ATTR_KEEPRUNNING, false);
			} catch (CoreException e) {
				return false;
			}
			
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.testing.model.ITestSession#isRunning()
	 */
	public boolean isRunning() {
		return fIsRunning;
	}
	
	/**
	 * @param testId 
	 * @param className 
	 * @param testName 
	 * @param launchMode 
	 * @return <code>false</code> iff the rerun could not be started
	 * @throws CoreException 
	 */
	public boolean rerunTest(ITestElement testElement, String launchMode) throws CoreException {
		if (isKeptAlive()) {
			Status status= ((TestCaseElement) getTestElement(testElement.getId())).getStatus();
			if (status == Status.ERROR) {
				fErrorCount--;
			} else if (status == Status.FAILURE) {
				fFailureCount--;
			}
			/* TODO fTestRunnerClient.rerunTest(testId, className, testName); */
			return true;
			
		} else if (fLaunch != null) {
			// run the selected test using the previous launch configuration
			ILaunchConfiguration launchConfiguration= fLaunch.getLaunchConfiguration();
			if (launchConfiguration != null) {

				//String name= className;
				//if (testName != null) 
					//name+= "."+testName; //$NON-NLS-1$
				//String configName= Messages.format(DLTKTestingMessages.TestRunnerViewPart_configName, name); 
				//ILaunchConfigurationWorkingCopy tmp= launchConfiguration.copy(configName); 
				// fix for bug: 64838  junit view run single test does not use correct class [JUnit] 
//				tmp.setAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, className);
				// reset the container
				//tmp.setAttribute(DLTKTestingConstants.ATTR_TEST_CONTAINER, ""); //$NON-NLS-1$
				//if (testName != null) {
					//tmp.setAttribute(DLTKTestingConstants.ATTR_TEST_METHOD_NAME, testName);
					//	String args= "-rerun "+testId;
					//	tmp.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
				//}
				//tmp.launch(launchMode, null);	
				return true;
			}
		}
		
		return false;
	}
	
	public TestElement getTestElement(String id) {
		return fIdToTest.get(id);
	}

	private TestCategoryElement selectCategory(String id, String testName,
			boolean isSuite) {
		if (categoryEngines != null) {
			for (int i = 0; i < categoryEngines.length; ++i) {
				final TestCategoryDescriptor descriptor = categoryEngines[i]
						.getCategory(id, testName, isSuite);
				if (descriptor != null) {
					TestCategoryElement categoryElement = fCategoryMap
							.get(descriptor.getId());
					if (categoryElement == null) {
						categoryElement = new TestCategoryElement(fTestRoot,
								descriptor.getId(), descriptor.getName());
						fCategoryMap.put(descriptor.getId(), categoryElement);
					}
					return categoryElement;
				}
			}
		}
		return null;
	}

	private TestElement addTreeEntry(String treeEntry) {
		// format: testId","testName","isSuite","testcount
		int index0= treeEntry.indexOf(',');
		String id= treeEntry.substring(0, index0);
		
		StringBuffer testNameBuffer= new StringBuffer(100);
		int index1 = scanTestName(treeEntry, index0 + 1, testNameBuffer, true);
		String testName= testNameBuffer.toString().trim();
		
		int index2= treeEntry.indexOf(',', index1 + 1);
		boolean isSuite= treeEntry.substring(index1 + 1, index2).equals("true"); //$NON-NLS-1$
		
		int testCount= Integer.parseInt(treeEntry.substring(index2 + 1));
		
		return addTreeEntry(id, testName, isSuite, testCount);
	}

	private TestElement addTreeEntry(String id, String testName,
			boolean isSuite, int testCount) {
		if (fIncompleteTestSuites.isEmpty()) {
			TestContainerElement category = selectCategory(id, testName,
					isSuite);
			if (category == null) {
				category = fTestRoot;
			}
			return createTestElement(category, id, testName, isSuite, testCount);
		} else {
			int suiteIndex= fIncompleteTestSuites.size() - 1;
			IncompleteTestSuite openSuite= fIncompleteTestSuites.get(suiteIndex);
			openSuite.fOutstandingChildren--;
			if (openSuite.fOutstandingChildren <= 0)
				fIncompleteTestSuites.remove(suiteIndex);
			return createTestElement(openSuite.fTestSuiteElement, id, testName, isSuite, testCount);
		}
	}

	public TestElement createTestElement(TestContainerElement parent, String id, String testName, boolean isSuite, int testCount) {
		TestElement testElement;
		if (isSuite) {
			TestSuiteElement testSuiteElement= new TestSuiteElement(parent, id, testName, testCount);
			testElement= testSuiteElement;
			if (testCount > 0)
				fIncompleteTestSuites.add(new IncompleteTestSuite(testSuiteElement, testCount));
		} else {
			testElement= new TestCaseElement(parent, id, testName);
			++fCreatedTestCaseCount;
			adjustTotalCount(fCreatedTestCaseCount);
		}
		fIdToTest.put(id, testElement);
		return testElement;
	}
	
	/**
	 * Append the test name from <code>s</code> to <code>testName</code>.
	 *  
	 * @param s the string to scan
	 * @param start the offset of the first character in <code>s</code> 
	 * @param testName the result
	 * 
	 * @return the index of the next ','
	 */
	static int scanTestName(String s, int start, StringBuffer testName,
			boolean breakOnComma) {
		boolean inQuote= false;
		int i= start;
		for (; i < s.length(); i++) {
			char c= s.charAt(i);
			if (c == '\\' && !inQuote) {
				inQuote= true;
				continue;
			} else if (inQuote) {
				inQuote= false;
				testName.append(c);
			} else if (breakOnComma && c == ',')
				break;
			else
				testName.append(c);
		}
		return i;
	}

	/**
	 * An {@link ITestRunListener2} that listens to events from the
	 * {@link RemoteTestRunnerClient} and translates them into high-level model
	 * events (broadcasted to {@link ITestSessionListener}s).
	 */
	private class TestSessionNotifier implements ITestRunListener2 {
		
		public void testRunStarted(int testCount) {
			fIncompleteTestSuites= new ArrayList<IncompleteTestSuite>();
			
			fStartedCount= 0;
			fIgnoredCount= 0;
			fFailureCount= 0;
			fErrorCount= 0;
			fTotalCount= testCount;
			fCreatedTestCaseCount = 0;
			
			fStartTime= System.currentTimeMillis();
			fIsRunning= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionStarted();
			}
		}
	
		public void testRunEnded(long elapsedTime) {
			fIsRunning= false;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionEnded(elapsedTime);
			}
		}
	
		public void testRunStopped(long elapsedTime) {
			fIsRunning= false;
			fIsStopped= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionStopped(elapsedTime);
			}
		}
	
		public void testRunTerminated() {
			fIsRunning= false;
			fIsStopped= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionTerminated();
			}
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.junit.model.ITestRunListener2#testTreeEntry(java.lang.String)
		 */
		public void testTreeEntry(String description) {
			TestElement testElement= addTreeEntry(description);
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testAdded(testElement);
			}
		}

		public void testTreeEntry(String testId, String testName,
				boolean isSuite, int testCount) {
			TestElement testElement = addTreeEntry(testId, testName, isSuite,
					testCount);

			Object[] listeners = fSessionListeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testAdded(testElement);
			}
		}
	
		private TestElement createUnrootedTestElement(String testId, String testName) {
			TestSuiteElement unrootedSuite= getUnrootedSuite();
			TestElement testElement= createTestElement(unrootedSuite, testId, testName, false, 1);
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testAdded(testElement);
			}
			
			return testElement;
		}

		private TestSuiteElement getUnrootedSuite() {
			if (fUnrootedSuite == null) {
				fUnrootedSuite= (TestSuiteElement) createTestElement(fTestRoot, "-2", DLTKTestingMessages.TestRunSession_unrootedTests, true, 0);  //$NON-NLS-1$
			}
			return fUnrootedSuite;
		}

		public void testStarted(String testId, String testName) {
			if (fStartedCount == 0) {
				Object[] listeners= fSessionListeners.getListeners();
				for (int i= 0; i < listeners.length; ++i) {
					((ITestSessionListener) listeners[i]).runningBegins();
				}
			}
			TestElement testElement= getTestElement(testId);
			if (testElement == null) {
				testElement= createUnrootedTestElement(testId, testName);
			} else if (! (testElement instanceof TestCaseElement)) {
				logUnexpectedTest(testId, testElement);
				return;
			}
			TestCaseElement testCaseElement= (TestCaseElement) testElement;
			setStatus(testCaseElement, Status.RUNNING);
			
			fStartedCount++;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testStarted(testCaseElement);
			}
		}
	
		public void testEnded(String testId, String testName) {
			TestElement testElement= getTestElement(testId);
			if (testElement == null) {
				testElement= createUnrootedTestElement(testId, testName);
			} else if (! (testElement instanceof TestCaseElement)) {
				logUnexpectedTest(testId, testElement);
				return;
			}
			TestCaseElement testCaseElement= (TestCaseElement) testElement;
			if (testName.startsWith(MessageIds.IGNORED_TEST_PREFIX)) {
				testCaseElement.setIgnored(true);
				fIgnoredCount++;
			} else if (testName.length() != 0
					&& !testName.equals(testCaseElement.getTestName())) {
				testCaseElement.setTestName(testName);
			}

			if (testCaseElement.getStatus() == Status.RUNNING)
				setStatus(testCaseElement, Status.OK);
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testEnded(testCaseElement);
			}
		}
		
		
		public void testFailed(int status, String testId, String testName, String trace) {
			testFailed(status, testId, testName, trace, null, null, -1);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.junit.model.ITestRunListener2#testFailed(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		public void testFailed(int statusCode, String testId, String testName, String trace, String expected, String actual, int code) {
			TestElement testElement= getTestElement(testId);
			if (testElement == null) {
				testElement= createUnrootedTestElement(testId, testName);
				return;
			}

			Status status= Status.convert(statusCode, code);
			registerTestFailureStatus(testElement, status, trace, nullifyEmpty(expected), nullifyEmpty(actual));
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testFailed(testElement, status, trace, expected, actual, code);
			}
		}

		private String nullifyEmpty(String string) {
			if (string != null) {
				int length = string.length();
				if (length == 0)
					return null;
				else if (string.charAt(length - 1) == '\n')
					return string.substring(0, length - 1);
			}
			return string;
		}
	
		public void testReran(String testId, String testClass, String testName, int status, String trace) {
			testReran(testId, testClass, testName, status, trace, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.junit.model.ITestRunListener2#testReran(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)
		 */
		public void testReran(String testId, String className, String testName, int statusCode, String trace, String expectedResult, String actualResult) {
			TestElement testElement= getTestElement(testId);
			if (testElement == null) {
				testElement= createUnrootedTestElement(testId, testName);
			} else if (! (testElement instanceof TestCaseElement)) {
				logUnexpectedTest(testId, testElement);
				return;
			}
			TestCaseElement testCaseElement= (TestCaseElement) testElement;
			
			Status status= Status.convert(statusCode, ITestingClient.PASSED);
			registerTestFailureStatus(testElement, status, trace, nullifyEmpty(expectedResult), nullifyEmpty(actualResult));
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				//TODO: post old & new status?
				((ITestSessionListener) listeners[i]).testReran(testCaseElement, status, trace, expectedResult, actualResult);
			}
		}
	
		private void logUnexpectedTest(String testId, TestElement testElement) {
//			DLTKTestingPlugin.log(new Exception("Unexpected TestElement type for testId '" + testId + "': " + testElement)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static class IncompleteTestSuite {
		public TestSuiteElement fTestSuiteElement;
		public int fOutstandingChildren;
		
		public IncompleteTestSuite(TestSuiteElement testSuiteElement, int outstandingChildren) {
			fTestSuiteElement= testSuiteElement;
			fOutstandingChildren= outstandingChildren;
		}
	}
	
	public void registerTestFailureStatus(TestElement testElement, Status status, String trace, String expected, String actual) {
		testElement.setStatus(status, trace, expected, actual);
		if (status.isError()) {
			fErrorCount++;
		} else if (status.isFailure()) {
			fFailureCount++;
		}
	}

	public void registerTestEnded(TestElement testElement, boolean completed) {
		if (testElement instanceof TestCaseElement) {
			fTotalCount++;
			if (! completed) {
				return;
			}
			fStartedCount++;
			if (((TestCaseElement) testElement).isIgnored()) {
				fIgnoredCount++;
			}
			if (! testElement.getStatus().isErrorOrFailure())
				setStatus(testElement, Status.OK);
		}
	}
	
	private void setStatus(TestElement testElement, Status status) {
		testElement.setStatus(status);
	}
	
	public ITestElement[] getFailedTestElements(ITestElementPredicate predicate) {
		List<ITestElement> failures = new ArrayList<ITestElement>();
		addFailures(failures, getTestRoot(), predicate);
		return failures.toArray(new TestElement[failures.size()]);
	}

	private void addFailures(List<ITestElement> failures, ITestElement testElement,
			ITestElementPredicate predicate) {
		Result testResult = testElement.getTestResult(true);
		if ((testResult == Result.ERROR || testResult == Result.FAILURE)
				&& predicate.matches(testElement)) {
			failures.add(testElement);
		}
		if (testElement instanceof TestSuiteElement) {
			TestSuiteElement testSuiteElement = (TestSuiteElement) testElement;
			ITestElement[] children = testSuiteElement.getChildren();
			for (int i = 0; i < children.length; i++) {
				addFailures(failures, children[i], predicate);
			}
		}
	}

	public ITestingClient getTestRunnerClient() {
		if (fTestRunnerClient instanceof ITestingClient) {
			return (ITestingClient) fTestRunnerClient;
		} else {
			return null;
		}
	}

	/**
	 * @return
	 */
	public final ITestRunnerUI getTestRunnerUI() {
		return testRunnerUI;
	}
}
