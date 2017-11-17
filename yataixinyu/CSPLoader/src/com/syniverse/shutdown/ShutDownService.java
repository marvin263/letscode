package com.syniverse.shutdown;

import java.io.File;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.loader.SetNameThreadFactory;
import com.syniverse.loader.SubscriberLoader;

public class ShutDownService {
	private static final Log LOGGER = LogFactory.getLog(ShutDownService.class);
	public static final long initialDelay = 10;
	public static final long SHUTDOWN_DELAY = 10;

	public static final Set<Connection> notClosedConn = new HashSet<Connection>();
	public final String shutdownFullPath;

	public ShutDownService() {
		shutdownFullPath = CreateShutDown.shutdownFullPath();
		new File(shutdownFullPath).delete();
	}

	public static void addNew(Connection conn) {
		synchronized (notClosedConn) {
			LOGGER.info("Adding a new connection. size=" + notClosedConn.size());
			notClosedConn.add(conn);
		}
	}

	public static void removeClosed(Connection conn) {
		synchronized (notClosedConn) {
			LOGGER.info("Removing a closed connection. size="
					+ notClosedConn.size());
			notClosedConn.remove(conn);
		}
	}

	/**
	 * Close all connection and exit JVM. We put System.exit(0) here to prevent
	 * creating new connection
	 */
	public static void closeAllBrutally() {
		synchronized (notClosedConn) {
			LOGGER.info("Try to close all unclosed connection. size="
					+ notClosedConn.size());
			try {
				Iterator<Connection> it = notClosedConn.iterator();
				while (it.hasNext()) {
					Connection oneConn = (Connection) it.next();
					if (oneConn != null) {
						try {
							oneConn.close();
						} catch (Throwable e) {
							LOGGER.error("Error when close connection", e);
						}
					}
				}
				notClosedConn.clear();
				LOGGER.info("All connections are closed");
			} catch (Throwable e) {
				LOGGER.error("closeAllBrutally big exception", e);
			}
			System.exit(0);
		}
	}

	public ExecutorService startShutDownService() {
		ScheduledExecutorService shutdownSvc = Executors
				.newSingleThreadScheduledExecutor(new SetNameThreadFactory(
						SubscriberLoader.ThreadPool_Prefix_Shutdown));

		shutdownSvc.scheduleWithFixedDelay(new CheckShutDownFile(shutdownSvc),
				initialDelay, SHUTDOWN_DELAY, TimeUnit.SECONDS);
		return shutdownSvc;
	}

	class CheckShutDownFile implements Runnable {
		private final ScheduledExecutorService ses;

		public CheckShutDownFile(final ScheduledExecutorService ses) {
			this.ses = ses;
		}

		@Override
		public void run() {
			LOGGER.info("Detecting shutdown command......");
			File f = new File(shutdownFullPath);
			if (f.exists() == false) {
				return;
			} else {
				LOGGER.info("Detected shutdown command, begin to shutdown");
				f.delete();
				closeAllBrutally();
			}
		}
	}

	public static void main(String[] args) {
		// new ShutDownService().startShutDownService();
	}
}
