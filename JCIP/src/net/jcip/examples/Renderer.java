package net.jcip.examples;

import static net.jcip.examples.LaunderThrowable.launderThrowable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Renderer
 * <p/>
 * Using CompletionService to render page elements as they become available
 * 
 * @author Brian Goetz and Tim Peierls
 */
public abstract class Renderer {
	private final ExecutorService executor;

	Renderer(ExecutorService executor) {
		this.executor = executor;
	}

	void renderPage(CharSequence source) {
		final List<ImageInfo> info = scanForImageInfo(source);
		CompletionService<ImageData> completionService = new ExecutorCompletionService<ImageData>(
				executor);
		for (final ImageInfo imageInfo : info) {
			completionService.submit(new Callable<ImageData>() {
				public ImageData call() {
					return imageInfo.downloadImage();
				}
			});
		}

		renderText(source);

		try {
			// Marvin: 一旦得到一个结果，马上 renderImage()（而不是
			// download所有图片后，一起renderImage）
			for (int t = 0, n = info.size(); t < n; t++) {
				Future<ImageData> f = completionService.take();
				ImageData imageData = f.get();
				renderImage(imageData);
			}
		} catch (InterruptedException e) {
			// Marvin：当前的主线程（注意：可不是 executor哦） 被 (主线程外的另一个线程) 打断了
			Thread.currentThread().interrupt();
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
