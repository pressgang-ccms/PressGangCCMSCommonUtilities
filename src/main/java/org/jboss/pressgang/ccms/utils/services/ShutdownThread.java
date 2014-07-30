/*
  Copyright 2011-2014 Red Hat, Inc, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jboss.pressgang.ccms.utils.services;

import org.jboss.pressgang.ccms.utils.common.NotificationUtilities;

/**
 * An instance of this thread will be executed when the Java VM is shutdown
 * (probably in response to CTRL-C). We capture this event, and use it to
 * disconnect from the STOMP server.
 */
public class ShutdownThread extends Thread
{
	private final long shutdownTime;
	private final BaseServiceThread serviceThread;

	public ShutdownThread(final BaseServiceThread serviceThread, final long shutdownTime)
	{
		this.serviceThread = serviceThread;
		this.shutdownTime = shutdownTime;
	}

	@Override
	public void run()
	{
		NotificationUtilities.dumpMessageToStdOut("Shutdown Requested");
		
		/* exit the service loop */
		if (serviceThread != null)
			serviceThread.shutdown();
		else
		    return;

		/* wait for a certain amount of time for the service thread to shutdown */
		final long startTime = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		while (!serviceThread.isShutdown() && now - startTime <= shutdownTime)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException ex)
			{
				NotificationUtilities.dumpMessageToStdOut("Shutdown Thread Interrupted");
				break;
			}
			
			now = System.currentTimeMillis();
		}
		
		if (now - startTime <= shutdownTime)
		{
			NotificationUtilities.dumpMessageToStdOut("Clean shutdown performed");
		}
		else
		{
			NotificationUtilities.dumpMessageToStdOut("Unclean shutdown performed. Consider increasing the shutdownTime.");
		}
	}
}
