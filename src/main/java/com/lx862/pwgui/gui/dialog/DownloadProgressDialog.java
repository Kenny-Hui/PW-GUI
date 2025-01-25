package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;

public class DownloadProgressDialog extends ProgressDialog {

    public DownloadProgressDialog(Window window, String title, String itemName, URL url, Path destination, Runnable callback) {
        super(window, title);

        Main.LOGGER.info(String.format("Downloading %s from %s", itemName, url));
        setStatus(String.format("Downloading %s...", itemName));

        SwingWorker<Boolean, Long> downloadWorker = new SwingWorker<Boolean, Long>() {
            private long contentLength = -1;

            @Override
            protected Boolean doInBackground() throws Exception {
                URLConnection connection = url.openConnection();
                this.contentLength = connection.getContentLength();

                try (BufferedInputStream in = new BufferedInputStream(url.openStream()); FileOutputStream fos = new FileOutputStream(destination.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    long downloaded = 0;
                    while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        downloaded += 1024;
                        publish(downloaded);
                    }
                }
                return true;
            }

            @Override
            protected void process(List<Long> chunks) {
                if (contentLength == -1) return;

                int prg = (int) ((chunks.get(chunks.size() - 1) / (float) contentLength) * 100);
                setStatus(String.format("Downloading %s... (%d%%)", itemName, prg));
                DownloadProgressDialog.this.setProgress(prg);
            }

            @Override
            protected void done() {
                try {
                    get();
                    Main.LOGGER.info(String.format("Finished downloading %s", itemName));
                    callback.run();
                } catch (Exception e) {
                    Main.LOGGER.exception(e);
                    String[] options = new String[]{"Copy URL", "OK"};
                    int result = JOptionPane.showOptionDialog(DownloadProgressDialog.this, String.format("An error occured while downloading %s:\n%s", itemName, e.getMessage()), Util.withTitlePrefix("Download failed"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
                    if(result == 0) {
                        Util.copyToClipboard(url.toString());
                    }
                }

                dispose();
            }
        };
        downloadWorker.execute();
    }
}
