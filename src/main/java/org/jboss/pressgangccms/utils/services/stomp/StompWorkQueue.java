package org.jboss.pressgangccms.utils.services.stomp;

import org.jboss.pressgangccms.utils.concurrency.BaseWorkQueue;

public final class StompWorkQueue extends BaseWorkQueue<BaseStompRunnable>
{
	private static StompWorkQueue instance = null;
	
	synchronized public static StompWorkQueue getInstance()
	{
		if (instance == null)
		{
			instance = new StompWorkQueue();
		}
		return instance;
	}
	
	private StompWorkQueue()
	{
		super();
	}
}