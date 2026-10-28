
package com.giovielle.crypto.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HexFormat;

public class FileUtils {

    public static void saveBytes(Path path, byte[] content)
            throws IOException {

        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(
                path,
                content,
                StandardOpenOption.CREATE_NEW
        );
    }


public static void saveHex(Path path, byte[] content)
        throws IOException {

    Path parent = path.getParent();

    if (parent != null) {
        Files.createDirectories(parent);
    }

    String hex = HexFormat.of().formatHex(content);

    Files.writeString(
            path,
            hex,
            StandardCharsets.US_ASCII,
            StandardOpenOption.CREATE_NEW
    );
}

public static byte[] readHex(Path path)
        throws IOException {

    String hex = Files.readString(
            path,
            StandardCharsets.US_ASCII
    ).trim();

    return HexFormat.of().parseHex(hex);
}

}
