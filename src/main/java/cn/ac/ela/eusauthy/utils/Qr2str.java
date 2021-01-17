package cn.ac.ela.eusauthy.utils;

import io.izzel.taboolib.util.IO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Qr2str {

    public static String createAsciiPic(String path) {
        final String base = "███████████████████████████████████████";
        BufferedImage image = zoomImage(path);
        StringBuilder resultBuilder = new StringBuilder();
        for (int y = 0; y < image.getHeight(); y += 2) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = image.getRGB(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
//                System.out.print(index >= base.length() ? " " : String.valueOf(base.charAt(index)));
                resultBuilder.append(index >= base.length() ? " " : String.valueOf(base.charAt(index)));
            }
            resultBuilder.append("\n");
        }
        return resultBuilder.toString();
    }

    public static BufferedImage zoomImage(String src) {
        BufferedImage result = null;
        try {
            File srcfile = new File(src);
            if (!srcfile.exists()) {
                System.out.println("文件不存在");
            }
            BufferedImage im = ImageIO.read(srcfile);
            /* 原始图像的宽度和高度 */
            int width = im.getWidth();
            int height = im.getHeight();
//            // 压缩计算
//            float resizeTimes = 0.3f; /* 这个参数是要转化成的倍数,如果是1就是转化成1倍 */
//            /* 调整后的图片的宽度和高度 */
//            int toWidth = (int) (width * resizeTimes);
//            int toHeight = (int) (height * resizeTimes);
            int toWidth = (int) (64);
            int toHeight = (int) (64);
            /* 新生成结果图片 */
            result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);

            result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0,
                    null);
        } catch (Exception e) {
            System.out.println("创建缩略图发生异常" + e.getMessage());
        }
        return result;
    }

    public static void main(String[] args){
        System.out.println(Qr2str.createAsciiPic("C:\\Users\\ElaBosak\\Desktop\\qr.png"));
    }
}
