package com.server.handsock.console;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.springframework.web.multipart.MultipartFile;

public class ValidExtension {
    public Boolean checkImageValidExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        boolean isValidExtension = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".webp") || fileName.endsWith(".gif");
        if (!isValidExtension) return false;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) return false;
        } catch (Exception e) {
            new ConsolePrints().printErrorLog(e);
            return false;
        }
        return true;
    }
}
