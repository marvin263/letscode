package net.jcip.examples;

import java.util.*;
import java.util.concurrent.*;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * FutureRenderer
 * <p/>
 * Waiting for image download with \Future
 * 
 * @author Brian Goetz and Tim Peierls
 */
public abstract class FutureRenderer {
	private final ExecutorService executor = Executors.newCachedThreadPool();

	void renderPage(CharSequence source) {
		final List<ImageInfo> imageInfos = scanForImageInfo(source);
		Callable<List<ImageData>> task = new Callable<List<ImageData>>() {
			public List<ImageData> call() {
				List<ImageData> result = new ArrayList<ImageData>();
				for (ImageInfo imageInfo : imageInfos)
					result.add(imageInfo.downloadImage());
				return result;
			}
		};

		Future<List<ImageData>> future = executor.submit(task);
		renderText(source);

		try {
			// Marvin: get()会一直等着
			// 直到 在线程B(即，executor)中执行的 Callable 执行完毕（所有图片download完毕）
			List<ImageData> imageData = future.get();
			for (ImageData data : imageData)
				renderImage(data);
		} catch (InterruptedException e) {
			// Marvin：当前的主线程（注意：可不是 executor哦） 被 (主线程外的另一个线程) 打断了
			// Re-assert the thread's interrupted status
			Thread.currentThread().interrupt();
			// Marvin：注意：这里很重要。记得 cancel哦，亲
			// We don't need the result, so cancel the task too
			future.cancel(true);
		} catch (ExecutionException e) {
			throw launderThrowable(e.getCause());
		}
	}

	interface ImageData {
	}

	interface ImageInfo {
		ImageData downloadImage();
	}

	abstract void renderText(CharSequence s);

	abstract List<ImageInfo> scanForImageInfo(CharSequence s);

	abstract void renderImage(ImageData i);
}
