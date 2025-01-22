package com.server.handsock.console;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ValidExtension {
    private final ConsolePrints consolePrints = new ConsolePrints();

    public Boolean checkImageValidExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        boolean isValidExtension = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".webp") || fileName.endsWith(".gif");
        if (!isValidExtension) return false;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) return false;
        } catch (Exception e) {
            consolePrints.printErrorLog(e);
            return false;
        }
        return true;
    }
}
