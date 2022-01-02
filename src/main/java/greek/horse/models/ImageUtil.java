package greek.horse.models;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

//    private static int tileCount = 3;//16*x-2560*y-2560\ =\ 0

//    public static DesktopImageCarrier split(BufferedImage img) {
//        int tileCount = (((16 * img.getWidth()) - 2560) * 3) / 2560;
//        tileCount = 1;
//        int tileW = img.getWidth() / tileCount;
//        int tileH = img.getHeight() / tileCount;
//
//        int lastW = img.getWidth() - (tileW * tileCount);
//        int lastH = img.getHeight() - (tileH * tileCount);
//
//        ArrayList<BufferedImage> list = new ArrayList<>();
//
//        for (int i = 0; i < tileCount; i++) {
//
//            int x = i * tileW;
//            for (int j = 0; j < tileCount; j++) {
//                int y = j * tileH;
//                list.add(img.getSubimage(x, y, tileW, tileH));
//            }
//
//            if (lastH > 0) {
//                int y = tileCount * tileH;
//                list.add(img.getSubimage(x, y, tileW, lastH));
//            }
//
//        }
//
//        if (lastW > 0) {
//            int x = tileCount * tileW;
//            for (int j = 0; j < tileCount; j++) {
//                int y = j * tileH;
//                list.add(img.getSubimage(x, y, lastW, tileH));
//            }
//            if (lastH > 0) {
//                int y = (tileCount) * tileH;
//                list.add(img.getSubimage(x, y, lastW, lastH));
//            }
//        }
//        return new DesktopImageCarrier(img.getWidth(), img.getHeight(), img.getType(), tileW, tileH, lastW > 0 ? tileCount + 1 : tileCount, lastH > 0 ? tileCount + 1 : tileCount, list);
//    }

//    public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
//        // The images must be the same size.
////        if (1==1)
////            return false;
//        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
//            return false;
//        } else if (imgA.getType() != imgB.getType()) {//they must be the same type
//            return false;
//        }
//
//        int width = imgA.getWidth();
//        int height = imgA.getHeight();
//
//        // Loop over every pixel.
//        for (int y = 0; y < height - 4; y = y + 5) {
//            for (int x = 0; x < width - 4; x = x + 5) {
//                // Compare the pixels for equality.
//                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }

    public static BufferedImage greyIt(BufferedImage from) {
        BufferedImage grey = new BufferedImage(
                from.getWidth(),
                from.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
//                BufferedImage.OPAQUE);

        Graphics2D graphic = grey.createGraphics();
        graphic.drawImage(from, 0, 0, Color.WHITE, null);
        graphic.dispose();
        return grey;
    }

    public static BufferedImage reduceIt(BufferedImage from) {
        BufferedImage grey = new BufferedImage(
                from.getWidth(),
                from.getHeight(),
                BufferedImage.OPAQUE);

        Graphics2D graphic = grey.createGraphics();
        graphic.drawImage(from, 0, 0, Color.WHITE, null);
        graphic.dispose();
        return grey;
    }

//    public static ArrayList<BufferedImage> compressSplit(ArrayList<BufferedImage> newSplit, ArrayList<BufferedImage> oldSplit) {
//        ArrayList<BufferedImage> list = new ArrayList(newSplit);
//        if (list.size() == oldSplit.size()) {
//            for (int i = 0; i < list.size(); i++) {
//                if (compareImages(list.get(i), oldSplit.get(i))) {
//                    list.set(i, null);
//                }
//            }
//        }
//        return list;
//    }

//    public static ArrayList<Object> prepareSplit(ArrayList<BufferedImage> sendSplit) {
//        ArrayList<Object> list = new ArrayList<>();
//        for (BufferedImage img : sendSplit) {
//
//            if (img != null) {
//                try {
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    ImageIO.write(img, "gif", baos);
//                    list.add(baos.toByteArray());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                list.add(null);
//            }
//
//        }
//        return list;
//    }

//    public static BufferedImage joinSplit(DesktopImageCarrier carrier, Image image) throws IOException {
//        BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
//
//        if (bi.getWidth() != carrier.getWidth() || bi.getHeight() != carrier.getHeight()) {
//            bi = new BufferedImage(carrier.getWidth(), carrier.getHeight(), BufferedImage.TYPE_INT_RGB);
//        }
//
//        Graphics2D graphics = bi.createGraphics();
//        int repo = 0;
//        int pos = 0;
//        for (int i = 0; i < carrier.getwCount(); i++) {
//            for (int j = 0; j < carrier.gethCount(); j++) {
//                if (carrier.getSplit().get(pos) != null) {
//
//                    repo++;
//
//                    BufferedImage subImg = ImageIO.read(new ByteArrayInputStream((byte[]) carrier.getSplit().get(pos)));
//
//                    graphics.drawImage(subImg, carrier.getTileW() * i, carrier.getTileH() * j, null);
//                }
//                pos++;
//            }
//        }
//        System.out.println(repo + " - " + carrier.getwCount() * carrier.gethCount());
//        graphics.dispose();
//        return bi;
//    }
}
