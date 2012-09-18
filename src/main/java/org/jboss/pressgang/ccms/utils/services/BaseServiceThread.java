package org.jboss.pressgang.ccms.utils.services;

abstract public class BaseServiceThread extends Thread
{
	public abstract void shutdown();
	public abstract boolean isShutdown();
}
