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

public class ZipUtils {

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
