/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for zipping files and directories.
 * Provides methods for zipping entire directories and adding individual files to a zip archive.
 */
public class ZipUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ZipUtils() {}

    /**
     * Zips the contents of a directory into a zip file.
     * This method recursively walks through the directory and adds each file to the zip archive.
     *
     * @param folderPath The path to the folder to be zipped.
     * @param zipFilePath The path to the output zip file.
     * @throws IOException If an I/O error occurs while reading the directory or writing the zip file.
     */
    public static void zipDirectory(Path folderPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
            try (Stream<Path> paths = Files.walk(folderPath)) {
                for (Path path : (Iterable<Path>) paths::iterator) {
                    if (Files.isDirectory(path)) {
                        continue;
                    }
                    String zipEntryName = folderPath.relativize(path).toString().replace("\\", "/");
                    ZipEntry zipEntry = new ZipEntry(zipEntryName);
                    try (InputStream is = Files.newInputStream(path)) {
                        addToZip(zipEntry, is, zos);
                    }
                }
            }
        }
    }

    /**
     * Adds a file's contents to the zip archive.
     *
     * @param zipEntry The zip entry (file) to be added to the zip archive.
     * @param is The input stream from which the file contents are read.
     * @param zos The zip output stream where the file will be written.
     * @throws IOException If an I/O error occurs while writing the file to the zip archive.
     */
    public static void addToZip(ZipEntry zipEntry, InputStream is, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(zipEntry);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            zos.write(buffer, 0, bytesRead);
        }
        zos.closeEntry();
    }
}
