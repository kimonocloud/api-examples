package kimono.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import kimono.client.KCTenant;
import kimono.client.tasks.KCTask;

public class GradesJobManager {
	private class JobArguments {
		private KCTenant tenant;
		private KCTask task;

		public JobArguments(KCTenant tenant, KCTask task) {
			this.tenant = tenant;
			this.task = task;
		}

		public KCTenant getTenant() {
			return tenant;
		}

		public KCTask getTask() {
			return task;
		}
	}

	@FunctionalInterface
	public interface GradesJobHandler {
		public void handle(KCTenant tenant, KCTask task);
	}

	private ArrayBlockingQueue<JobArguments> jobQueue = new ArrayBlockingQueue<>(100);
	private boolean stop = false;
	private GradesJobHandler handler;

	public GradesJobManager(GradesJobHandler handler) {
		this.handler = handler;
	}

	public void addJob(KCTenant tenant, KCTask task) {
		try {
			jobQueue.put(new JobArguments(tenant, task));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void run(int interval, TimeUnit unit) {
		stop = false;
		new Thread(() -> {
			do {
				long ms = System.currentTimeMillis();
				List<JobArguments> jobs = new ArrayList<>();
				jobQueue.drainTo(jobs);
				jobs.forEach(job -> handler.handle(job.getTenant(), job.getTask()));
				long delay = unit.toMillis(interval) - (System.currentTimeMillis() - ms);
				if (delay > 0) {
					try {
						TimeUnit.MILLISECONDS.sleep(delay);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			} while (!isStopped());
		}).start();
	}

	public void stop() {
		synchronized (this) {
			stop = true;
		}
	}

	private boolean isStopped() {
		synchronized (this) {
			return stop;
		}
	}
}
