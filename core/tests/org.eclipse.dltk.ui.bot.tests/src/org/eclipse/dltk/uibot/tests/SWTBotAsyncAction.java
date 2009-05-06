package org.eclipse.dltk.uibot.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

public abstract class SWTBotAsyncAction {
	private final long fTimeout;
	protected final SWTBot bot;

	public SWTBotAsyncAction(SWTBot bot, long timeout) {
		this.bot = bot;
		fTimeout = timeout;
	}

	public SWTBotAsyncAction(SWTBot bot) {
		this(bot, 10000);
	}

	protected abstract void onComplete() throws Exception;

	protected abstract void action() throws Exception;

	protected abstract boolean startCondition() throws Exception;

	protected abstract void blockingAction() throws Exception;

	protected void failTimeoutElapsed() throws Exception {
		throw new TimeoutException(
				"Condition not achieved within reasonable timeout"); //$NON-NLS-1$
	}

	private Throwable exception;

	public void execute() throws Exception {
		Thread tester = new Thread() {
			public void run() {
				try {
					long timeStamp = System.currentTimeMillis();
					// first, we wait for start condition to become true
					while (!startCondition()) {
						if (System.currentTimeMillis() - timeStamp > fTimeout) {
							failTimeoutElapsed();
							return;
						}
						Thread.sleep(100);
					}
					try {
						action();
					} finally {
						try {
							onComplete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Throwable e) {
					exception = e;
				}
			}
		};
		tester.start();
		blockingAction();

		if (exception != null) {
			if (exception instanceof Error) {
				throw (Error) exception;
			} else {
				throw (Exception) exception;
			}
		}

	}
}
