/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package de.jonaswismar.tts;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jonas
 */
public class TTS {

    public static void main(String[] args) {
        File ttsSourceFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\data\\Strassen und Orte");
        File ttsTempFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\data_tmp");
        File ttsTargetFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\data_sd");
        System.out.println("Lade alle *.mp3 aus Verzeichnis \"" + ttsSourceFolder + "\"");
        Collection<File> files = FileUtils.listFiles(ttsSourceFolder, new String[]{"mp3"}, true);
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            if (!StringUtils.contains(filePath, "NAMEN") && !StringUtils.contains(filePath, "PLZ")) {
                try {
                    File targetFile = new File(ttsTempFolder + "\\" + cleanSDPlayerFileName(fileName));
                    FileUtils.copyFile(file, targetFile);
                } catch (IOException ex) {
                    Logger.getLogger(TTS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Collection<File> filesTmp = FileUtils.listFiles(ttsTempFolder, new String[]{"mp3"}, true);
        File[] filesTmpArray = new File[filesTmp.size()];
        filesTmpArray = filesTmp.toArray(filesTmpArray);
        for (int i = 0; i < filesTmpArray.length; i++) {
            File file = filesTmpArray[i];
            String fileName = file.getName();
            File targetFile = new File(ttsTargetFolder + "\\MP3\\" + StringUtils.leftPad(String.valueOf(i + 1), 4, "0") + "_" + fileName);
            try {
                FileUtils.moveFile(file, targetFile);
            } catch (IOException ex) {
                Logger.getLogger(TTS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String cleanSDPlayerFileName(String oldName) {
        String newName = oldName.replace(" ", "_")
                .replace("ü", "ue")
                .replace("ö", "oe")
                .replace("ä", "ae")
                .replace("ß", "ss");

        // first replace all capital Umlauts in a non-capitalized context (e.g. Übung)
        newName = newName.replaceAll("Ü(?=[a-zäöüß ])", "Ue")
                .replaceAll("Ö(?=[a-zäöüß ])", "Oe")
                .replaceAll("Ä(?=[a-zäöüß ])", "Ae");

        // now replace all the other capital Umlauts
        newName = newName.replace("Ü", "UE")
                .replace("Ö", "OE")
                .replace("Ä", "AE");

        return newName;
    }
}
