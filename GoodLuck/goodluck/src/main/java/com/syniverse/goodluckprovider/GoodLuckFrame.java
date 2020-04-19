package com.syniverse.goodluckprovider;

import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.ContinuousAudioDataStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class GoodLuckFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final long INTERVAL = 20;

    private Object thelock = new Object();
    private Random r = new Random();
    private JLabel labelImage;
    private JButton btn;

    private volatile boolean stopRunning = true;
    private volatile int flickerWinnerIndex = NOT_CANDIDATE;

    private static final int ALREADY_WIN_COUNT_INDEX = 0;
    /**
     * already_win_person[0] is the count of already won
     */
    private static int[] already_win_person;

    private static final int IMAGE_WIDTH = 690;
    private static final int IMAGE_HEIGHT = 922;

    private static final String drum = "drum.wav";
    private static final String wowowo = "wowowo.wav";
    private static byte[] drumBytes = null;
    private static byte[] wowowoBytes = null;
    private ContinuousAudioDataStream drumAudio;
    private AudioDataStream wowowoAudio;

    private static final int NOT_CANDIDATE = -1;
    private static final String NOT_CANDIDATE_NAME = "go4it.jpg";
    private static Icon NOT_CANDIDATE_ICON;

    private static final String SYNIVERSE_LOGO_NAME = "company_logo.jpg";
    public static Image COMPANY_LOGO_ICON;

    private static final ArrayList<Icon> LIST_CANDIDATE_IMAGES = new ArrayList<Icon>();
    private static final Map<String, Integer> mapName2Size = new HashMap<String, Integer>();
    private static final String CLICK_TO_FIND = "Click to Find";
    private static final String LOOKING_FOR = "Looking for ..... ";

    private static void loadImage(String imageFolderName) {
        Map<String, byte[]> mapName2ImageBytes = null;
        try {
            mapName2ImageBytes = readFromFileSystem(imageFolderName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Entry<String, byte[]>> imageDataSet = mapName2ImageBytes.entrySet();
        System.out.println("image count:" + imageDataSet.size());
        for (Entry<String, byte[]> oneImage : imageDataSet) {
            String imageName = oneImage.getKey();// com/marvin/images/marvinli.jpg
            byte[] imageData = oneImage.getValue();

            if (imageName.endsWith(NOT_CANDIDATE_NAME.toLowerCase())) {
                System.out.println("not candidate image name: " + imageName);
                NOT_CANDIDATE_ICON = createImageIcon(imageData, IMAGE_WIDTH,
                        IMAGE_HEIGHT);
            } else if (imageName.endsWith(SYNIVERSE_LOGO_NAME.toLowerCase())) {
                COMPANY_LOGO_ICON = Toolkit.getDefaultToolkit().createImage(
                        imageData);
                System.out.println("syniverse logo image name: " + imageName);
            } else {
                LIST_CANDIDATE_IMAGES.add(createImageIcon(imageData,
                        IMAGE_WIDTH, IMAGE_HEIGHT));
                System.out.println("normal candidate image name: " + imageName);
            }

        }
        System.out.println("candidate size: " + LIST_CANDIDATE_IMAGES.size());
        already_win_person = new int[LIST_CANDIDATE_IMAGES.size() + 1];
    }

    private static void loadSound(String soundFolderName) {
        Map<String, byte[]> mapName2SoundBytes = null;
        try {
            mapName2SoundBytes = readFromJarFileByFolderName(soundFolderName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Entry<String, byte[]>> soundDataSet = mapName2SoundBytes.entrySet();
        System.out.println("sound count: " + soundDataSet.size());
        for (Entry<String, byte[]> oneImage : soundDataSet) {
            String soundName = oneImage.getKey();// com/marvin/images/marvinli.jpg
            byte[] byteSound = oneImage.getValue();
            if (soundName.toLowerCase().endsWith(drum.toLowerCase())) {
                System.out.println("drum sound name: " + soundName);
                drumBytes = byteSound;
            } else if (soundName.toLowerCase().endsWith(wowowo.toLowerCase())) {
                System.out.println("wowowo sound name: " + soundName);
                wowowoBytes = byteSound;
            }
        }
    }

    private static ImageIcon createImageIcon(byte[] imageData, int newWidth,
                                             int newHeight) {
        Image img = Toolkit.getDefaultToolkit().createImage(imageData, 0,
                imageData.length);
        ImageIcon scaled = new ImageIcon(img.getScaledInstance(newWidth,
                newHeight, Image.SCALE_DEFAULT));
        return scaled;
    }

    public static Map<String, byte[]> readFromFileSystem(
            String imageFolderName) throws IOException {
        String jarStayFolder = getJarStayFolder(GoodLuckFrame.class);
        File[] images = new File(jarStayFolder + "/" + imageFolderName).listFiles();
        Map<String, byte[]> mapName2Data = new HashMap<String, byte[]>();
        if (!isEmpty(images)) {
            for (File f : images) {
                if (f.isFile()) {
                    byte[] buf = new byte[(int) f.length()];
                    FileInputStream fis = new FileInputStream(f);
                    fis.read(buf);
                    mapName2Data.put(f.getName().toLowerCase(), buf);
                    fis.close();
                }
            }
        }
        return mapName2Data;
    }

    private static <T> boolean isEmpty(T[] arr) {
        return arr == null || arr.length == 0;
    }

    public static Map<String, byte[]> readFromJarFileByFolderName(
            String imageFolderName) throws IOException {
        URL jarURL = GoodLuckFrame.class.getProtectionDomain().getCodeSource()
                .getLocation();
        InputStream is = jarURL.openStream();

        ZipInputStream zis = new ZipInputStream(is);
        Map<String, byte[]> mapName2Data = doReadDataFromJar(zis, mapName2Size, imageFolderName);
        zis.close();
        return mapName2Data;
    }

    private static Map<String, byte[]> doReadDataFromJar(ZipInputStream zis,
                                                         Map<String, Integer> mapName2Size, String prefixFolder)
            throws IOException {
        String lowcasePrefix = prefixFolder.toLowerCase() + "/";
        Map<String, byte[]> mapName2Bytes = new HashMap<String, byte[]>();

        ZipEntry ze = null;
        while ((ze = zis.getNextEntry()) != null) {
            String lowcaseName = ze.getName().toLowerCase();
            if (lowcaseName.startsWith(lowcasePrefix)
                    && lowcaseName.length() > lowcasePrefix.length()) {
                int size = (int) ze.getSize();
                if (size == -1) {
                    size = mapName2Size.get(lowcaseName).intValue();
                }
                byte[] buf = new byte[(int) size];
                int off = 0;
                int chunk = 0;
                while ((size - off) > 0) {
                    chunk = zis.read(buf, off, (size - off));
                    if (chunk == -1) {
                        break;
                    }
                    off += chunk;
                }
                mapName2Bytes.put(lowcaseName, buf);
            }
            zis.closeEntry();
        }
        return mapName2Bytes;
    }

    public static String getJarStayFolder(Class<?> cls) {
        String jarSelf = cls.getProtectionDomain().getCodeSource()
                .getLocation().getFile();
        try {
            jarSelf = java.net.URLDecoder.decode(jarSelf, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jarStayFolder = new File(jarSelf).getParentFile()
                .getAbsolutePath();
        return jarStayFolder;
    }

    private static void fillMapName2Size() throws IOException {
        // extracts just sizes only.
        URL jarURL = GoodLuckFrame.class.getProtectionDomain().getCodeSource()
                .getLocation();
        System.out.println("jarURL=" + jarURL);
        System.out.println("jarFile=" + jarURL.getFile());

        ZipFile zf = new ZipFile(jarURL.getFile());

        Enumeration<? extends ZipEntry> e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            mapName2Size.put(ze.getName().toLowerCase(),
                    Integer.valueOf((int) ze.getSize()));
        }
        zf.close();
    }

    public void initComponent() {
        prepareBeforeInitComponent();

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        labelImage = new JLabel();
        labelImage.setVerticalAlignment(SwingConstants.CENTER);
        labelImage.setHorizontalAlignment(SwingConstants.CENTER);
        labelImage.setIcon(NOT_CANDIDATE_ICON);
        container.add(labelImage, BorderLayout.CENTER);

        btn = new JButton(CLICK_TO_FIND);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleClickEvent();
            }
        });
        container.add(btn, BorderLayout.SOUTH);
    }

    private void prepareBeforeInitComponent() {
        try {
            fillMapName2Size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadImage("images");
        loadSound("audio");
        stopRunning = true;
        startChangeImageThread();
    }

    private void handleClickEvent() {
        synchronized (thelock) {
            // currently not running
            // Want to run: if you click, then run
            if (stopRunning == true) {
                long begin = System.currentTimeMillis();
                if (wowowoAudio != null) {
                    AudioPlayer.player.stop(wowowoAudio);
                }
                drumAudio = new ContinuousAudioDataStream(new AudioData(
                        drumBytes));
                AudioPlayer.player.start(drumAudio);
                long end = System.currentTimeMillis();
                System.out.println("play drum: " + (end - begin));

                // you have clicked. running now........
                btn.setText(LOOKING_FOR);
                stopRunning = false;
                thelock.notifyAll();
            }
            // currently running
            // Want to stop running: if you click, then stop running
            else {
                // stop the sound
                long begin = System.currentTimeMillis();
                AudioPlayer.player.stop(drumAudio);
                wowowoAudio = new AudioDataStream(new AudioData(wowowoBytes));
                AudioPlayer.player.start(wowowoAudio);
                long end = System.currentTimeMillis();
                System.out.println("play wowowo: " + (end - begin));

                // you have clicked. stopped running. screen not changing now
                btn.setText(CLICK_TO_FIND);
                stopRunning = true;
                thelock.notifyAll();
            }
        }
    }

    private void startChangeImageThread() {
        Thread thread = new Thread("Change-Image") {
            public void run() {
                while (true) {
                    synchronized (thelock) {
                        // currently not running
                        // we've found the carrier, it is flickerWinnerIndex.
                        // flickerWinnerIndex won't appear any longer
                        while (stopRunning == true) {
                            final int finalWinnerIndex = flickerWinnerIndex;
                            flickerWinnerIndex = NOT_CANDIDATE;
                            System.out.println("actually is: "
                                    + finalWinnerIndex);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    System.out.println("stop at: "
                                            + finalWinnerIndex);
                                    setIconByIndex(labelImage, finalWinnerIndex);
                                    addWinner2AlreadyWin(finalWinnerIndex);
                                }
                            });
                            try {
                                thelock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Permit to run. flicker the left images
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            flickerWinnerIndex = generateWinner();
                            setIconByIndex(labelImage, flickerWinnerIndex);
                        }
                    });

                    if (stopRunning == false) {
                        try {
                            Thread.sleep(INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    private void addWinner2AlreadyWin(int finalWinnerIndex) {
        if (finalWinnerIndex != NOT_CANDIDATE) {
            int lastAlreadyWinCount = already_win_person[ALREADY_WIN_COUNT_INDEX];
            int nowAlreadyWinCount = lastAlreadyWinCount + 1;
            already_win_person[ALREADY_WIN_COUNT_INDEX] = nowAlreadyWinCount;
            already_win_person[nowAlreadyWinCount] = finalWinnerIndex;
        }
    }

    private void setIconByIndex(JLabel label, int iconIndex) {
        if (iconIndex == NOT_CANDIDATE) {
            label.setIcon(NOT_CANDIDATE_ICON);
        } else {
            label.setIcon(LIST_CANDIDATE_IMAGES.get(iconIndex));
        }
    }

    private int generateWinner() {
        int totalCandicateCount = LIST_CANDIDATE_IMAGES.size();
        int alreadyWinCount = already_win_person[ALREADY_WIN_COUNT_INDEX];

        // no one left
        if (alreadyWinCount == totalCandicateCount) {
            return NOT_CANDIDATE;
        }

        while (true) {
            int oneWinnerIndex = r.nextInt(totalCandicateCount);
            if (isInArray(oneWinnerIndex, already_win_person, 1,
                    alreadyWinCount) == false) {
                return oneWinnerIndex;
            }
        }
    }

    private boolean isInArray(int findTarget, int[] array, int scanFromIndex,
                              int scanSize) {
        for (int i = scanFromIndex; i < scanFromIndex + scanSize; i++) {
            if (array[i] == findTarget) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        GoodLuckFrame glf = new GoodLuckFrame();
        glf.initComponent();

        glf.setIconImage(COMPANY_LOGO_ICON);
        glf.setTitle("Looking for Good-Luck-Provider");
        System.out.println("dd");
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 630;
        int height = 850;
        glf.setBounds((scrn.width - width) / 2, (scrn.height - height) / 2,
                width, height);
        glf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        glf.setVisible(true);
    }
}
