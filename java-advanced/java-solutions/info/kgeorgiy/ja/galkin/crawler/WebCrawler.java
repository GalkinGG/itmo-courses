package info.kgeorgiy.ja.galkin.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WebCrawler implements NewCrawler {

    /**
     * enum for default values of argumnets
     */
    private enum DefaultValues {
        DEPTH(1),
        DOWNLOADERS(10),
        EXTRACTORS(10),
        PERHOST(10);

        private final int defaultValue;

        DefaultValues(int value) {
            this.defaultValue = value;
        }

        private int getDefaultValue() {
            return defaultValue;
        }
    }

    private final Downloader downloader;
    private final ExecutorService downloadExecutor;
    private final ExecutorService extractorExecutor;

    /**
     * Construct a crawler with fixed count of downloaders and extractors
     *
     * @param downloader     implementation of {@link Downloader}
     * @param downloaders    number of downloaders
     * @param extractors     number of extractors
     * @param ignoredPerHost ignored because perhost >= downloaders
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int ignoredPerHost) {
        this.downloader = downloader;
        this.downloadExecutor = Executors.newFixedThreadPool(downloaders);
        this.extractorExecutor = Executors.newFixedThreadPool(extractors);
    }

    /**
     * Usage: WebCrawler url [depth [downloads [extractors [perHost]]]]
     *
     * @param args parameters
     * @throws IOException if an error in CachingDownloader occurred.
     */
    public static void main(final String[] args) throws IOException {
        if (args.length > 5 || args.length < 1) {
            throw new IllegalArgumentException("Illegal count of arguments, expected 5");
        }
        String link = args[0];
        int[] values = getArgs(args);
        try (WebCrawler crawler = new WebCrawler(new CachingDownloader(60), values[1], values[2], values[3])) {
            Result result = crawler.download(link, values[0]);
            System.out.println("Downloaded: " + result.getDownloaded());
            System.out.println("Errors: " + result.getErrors());
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    /**
     * provides default values for missing parameters in main
     *
     * @param args terminal args
     * @return int array of values for crawler
     */
    private static int[] getArgs(String[] args) {
        int[] values = new int[4];
        Arrays.stream(DefaultValues.values()).forEach(it -> {
            int i = it.ordinal();
            if (i + 1 >= args.length) {
                values[i] = it.getDefaultValue();
            } else {
                values[i] = Integer.parseInt(args[i + 1]);
            }
        });
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        Map<String, IOException> failed = new ConcurrentHashMap<>();
        Set<String> completed = new ConcurrentSkipListSet<>();
        Set<String> nextLayer = new ConcurrentSkipListSet<>();
        final Phaser phaser = new Phaser(1);
        nextLayer.add(url);
        for (int i = depth; i > 0; i--) {
            Set<String> workLayer = nextLayer.stream()
                    .filter(link -> {
                        try {
                            URLUtils.getHost(link);
                            return excludes.stream().noneMatch(link::contains) && !failed.containsKey(link) && completed.add(link);
                        } catch (MalformedURLException e) {
                            failed.put(link, e);
                            return false;
                        }
                    }).collect(Collectors.toCollection(ConcurrentSkipListSet::new));
            nextLayer.clear();
            if (workLayer.isEmpty()) {
                break;
            }
            int workDepth = i - 1;
            workLayer.forEach(link -> {
                try {
                    URLUtils.getHost(link);
                    phaser.register();
                    downloadExecutor.submit(() -> {
                        try {
                            Document document = downloader.download(link);
                            if (workDepth != 0) {
                                phaser.register();
                                extractorExecutor.submit(() -> {
                                    try {
                                        nextLayer.addAll(document.extractLinks());
                                    } catch (IOException ignored) {
                                    } finally {
                                        phaser.arriveAndDeregister();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            failed.put(link, e);
                            completed.remove(link);
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    });
                } catch (MalformedURLException e) {
                    failed.put(link, e);
                    completed.remove(link);
                }
            });
            phaser.arriveAndAwaitAdvance();
        }
        return new Result(new ArrayList<>(completed), failed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        downloadExecutor.close();
        extractorExecutor.close();
    }
}
