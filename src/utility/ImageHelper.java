package utility;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageHelper
{
    public static BufferedImage blur(BufferedImage image, int radius)
    {
        BufferedImage scaledImage = scale(image, 0.5);
        scaledImage = horizontalBlur(verticalBlur(scaledImage, radius), radius);
        scaledImage = scale(scaledImage, 2.0);

        for(int col = 0; col < image.getWidth(); col++)
        {
            for(int row = 0; row < image.getHeight(); row++)
            {
                image.setRGB(col, row, scaledImage.getRGB(col, row));
            }
        }

        return image;
    }

    private static BufferedImage horizontalBlur(BufferedImage image, int radius)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for(int row = 0; row < image.getHeight(); row++)
        {
            for(int col = 0; col < image.getWidth(); col++)
            {
                int red = 0, green = 0, blue = 0;
                int total = 0;

                for(int miniCol = -radius; miniCol <= radius; miniCol++)
                {
                    if(col + miniCol >= 0 && col + miniCol < image.getWidth())
                    {
                        int kernelValue = 2;
                        if(miniCol == 0) kernelValue = 4;

                        int color = image.getRGB(col + miniCol, row);
                        red += ((color & 0xff0000) >> 16) * kernelValue;
                        green += ((color & 0xff00) >> 8) * kernelValue;
                        blue += (color & 0xff) * kernelValue;

                        total += kernelValue;
                    }
                }

                red /= total;
                green /= total;
                blue /= total;

                int newColor = (((red << 8) + green) << 8) + blue;
                newImage.setRGB(col, row, newColor);
            }
        }

        for(int col = 0; col < image.getWidth(); col++)
        {
            for(int row = 0; row < image.getHeight(); row++)
            {
                image.setRGB(col, row, newImage.getRGB(col, row));
            }
        }

        return image;
    }

    private static BufferedImage verticalBlur(BufferedImage image, int radius)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for(int col = 0; col < image.getWidth(); col++)
        {
            for(int row = 0; row < image.getHeight(); row++)
            {
                int red = 0, green = 0, blue = 0;
                int total = 0;

                for(int miniRow = -radius; miniRow <= radius; miniRow++)
                {
                    if(row + miniRow >= 0 && row + miniRow < image.getHeight())
                    {
                        int kernelValue = 2;
                        if(miniRow == 0) kernelValue = 4;

                        int color = image.getRGB(col, row + miniRow);
                        red += ((color & 0xff0000) >> 16) * kernelValue;
                        green += ((color & 0xff00) >> 8) * kernelValue;
                        blue += (color & 0xff) * kernelValue;

                        total += kernelValue;
                    }
                }

                red /= total;
                green /= total;
                blue /= total;

                int newColor = (((red << 8) + green) << 8) + blue;
                newImage.setRGB(col, row, newColor);
            }
        }

        for(int col = 0; col < image.getWidth(); col++)
        {
            for(int row = 0; row < image.getHeight(); row++)
            {
                image.setRGB(col, row, newImage.getRGB(col, row));
            }
        }

        return image;
    }

    private static BufferedImage scale(BufferedImage image, double scale)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        AffineTransformOp scaleTransform = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        newImage = scaleTransform.filter(image, newImage);

        return newImage;
    }
}
