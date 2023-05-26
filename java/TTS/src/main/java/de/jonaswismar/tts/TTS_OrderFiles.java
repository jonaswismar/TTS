/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package de.jonaswismar.tts;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jonas
 */
public class TTS_OrderFiles {

    public static void main(String[] args) {
        boolean copyFiles = true;
        boolean writeCode = true;
        boolean writeCSV = true;

        HashSet setOrte = new HashSet();
        HashSet setStrassen = new HashSet();

        File ttsSourceFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\data\\Geografie");
        File ttsTempFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\tmp");
        File ttsTargetFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\data_sd");
        File ttsCodeFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\src_other");
        File ttsOtherFolder = new File("C:\\Users\\jonas\\Documents\\PlatformIO\\Projects\\TTS\\src_other");

        try {
            System.out.println("Lösche Verzeichnis \"" + ttsTempFolder + "\"");
            FileUtils.deleteDirectory(ttsTempFolder);
            System.out.println("Lösche Verzeichnis \"" + ttsTargetFolder + "\"");
            FileUtils.deleteDirectory(ttsTargetFolder);
            System.out.println("Lösche Verzeichnis \"" + ttsCodeFolder + "\"");
            FileUtils.deleteDirectory(ttsCodeFolder);
            System.out.println("Lösche Verzeichnis \"" + ttsOtherFolder + "\"");
            FileUtils.deleteDirectory(ttsOtherFolder);
        } catch (IOException ex) {
            Logger.getLogger(TTS_OrderFiles.class.getName()).log(Level.SEVERE, null, ex);
        }

        int counterPostleitzahlen = 0;
        int counterOrte = 0;
        int counterStrassen = 0;

        System.out.println("Lade Gemeinden aus Verzeichnis \"" + ttsSourceFolder + "\"");
        String[] foldersGemeinden = ttsSourceFolder.list(DirectoryFileFilter.INSTANCE);
        for (String foldersGemeinde : foldersGemeinden) {
            System.out.println("\tBearbeite Gemeinde \"" + foldersGemeinde + "\"");
            File fileGemeinde = new File(ttsSourceFolder + "\\" + foldersGemeinde);
            File filePostleitzahlen = new File(ttsSourceFolder + "\\" + foldersGemeinde + "\\PLZ");

            /*
             PLZ
             */
            Collection<File> filesPostleitzahlen = FileUtils.listFiles(filePostleitzahlen, new String[]{"mp3"}, false);
            System.out.println("\t\t" + filesPostleitzahlen.size() + " Postleitzahl/en");
            for (File filePostleitzahl : filesPostleitzahlen) {
                counterPostleitzahlen = counterPostleitzahlen + 1;
                String fileNamePostleitzahl = filePostleitzahl.getName();
                String newFileNamePostleitzahl = StringUtils.leftPad(String.valueOf(counterPostleitzahlen), 3, "0") + "_" + fileNamePostleitzahl;
                if (copyFiles) {
                    try {
                        FileUtils.copyFile(filePostleitzahl, new File(ttsTargetFolder + "\\02\\" + newFileNamePostleitzahl));
                    } catch (IOException ex) {
                        Logger.getLogger(TTS_OrderFiles.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            /*
             Orte
             */
            File[] filesOrte = fileGemeinde.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
            System.out.println("\t\t" + (filesOrte.length - 2) + " Ort/e");

            for (File fileOrt : filesOrte) {
                String filePath = fileOrt.getAbsolutePath();
                if (!StringUtils.contains(filePath, "NAMEN") && !StringUtils.contains(filePath, "PLZ")) {
                    String fileNameOrt = fileOrt.getName();
                    System.out.println("\t\tBearbeite Ort \"" + fileNameOrt + "\"");

                    if (setOrte.contains(fileNameOrt)) {

                    } else {
                        counterOrte = counterOrte + 1;
                        String newFileNameOrt = StringUtils.leftPad(String.valueOf(counterOrte), 3, "0") + "_" + cleanSDPlayerFileName(fileNameOrt);

                        if (copyFiles) {
                            try {
                                FileUtils.copyFile(new File(ttsSourceFolder + "\\" + foldersGemeinde + "\\NAMEN\\" + fileNameOrt + ".mp3"), new File(ttsTargetFolder + "\\01\\" + newFileNameOrt + ".mp3"));
                            } catch (IOException ex) {
                                Logger.getLogger(TTS_OrderFiles.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        setOrte.add(fileNameOrt);
                    }
                    /*
                    Straßen
                     */
                    //
                    Collection<File> filesStrassen = FileUtils.listFiles(new File(filePath), new String[]{"mp3"}, false);
                    System.out.println("\t\t\t" + filesStrassen.size() + " Straße/n");

                    for (File fileStrasse : filesStrassen) {
                        String fileNameStrasse = fileStrasse.getName();
                        String nameStrasse = FilenameUtils.removeExtension(fileNameStrasse);

                        if (setStrassen.contains(nameStrasse)) {

                        } else {
                            counterStrassen = counterStrassen + 1;
                            String newFileNameStrasse = StringUtils.leftPad(String.valueOf(counterStrassen), 4, "0") + "_" + cleanSDPlayerFileName(fileNameStrasse);

                            if (copyFiles) {
                                try {
                                    FileUtils.copyFile(fileStrasse, new File(ttsTargetFolder + "\\MP3\\" + newFileNameStrasse));
                                } catch (IOException ex) {
                                    Logger.getLogger(TTS_OrderFiles.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            setStrassen.add(nameStrasse);
                        }
                    }
                }
            }
        }
        System.out.println(counterPostleitzahlen + " Postleitzahl/en");
System.out.println(counterOrte + " Ort/e");
System.out.println(counterStrassen + " Straße/n");

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

    public static void writeCode(String fileName, String content, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, "UTF8", append);
        } catch (IOException ex) {
            Logger.getLogger(TTS_OrderFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
