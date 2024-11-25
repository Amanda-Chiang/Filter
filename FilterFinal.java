public class FilterFinal
{
    public static void main(String [] args)
    {
        PixImage original = new PixImage("rk1.jpg");
        original.showImage();

        PixImage bnw = new PixImage("bnw.jpeg");
        
        PixImage result1 = circle(original,bnw);
        result1.showImage();
        //result1.saveImage("raykay2.png");

        // do not grade this one
        PixImage result2 = pastel(original);
        result2.showImage();
        

        PixImage result3 = grayGlitch(original);
        result3.showImage();
        //result3.saveImage("raykay1.png");

    }

    // FOR BEST RESULTS: bnw image should be of similar size as the original or only a little smaller
    // Filter that puts a BNW mask on the inner circle and changes each pixel to its closest color, 
    // and outside of that it has a gradient grayscale based on distance from the center
    public static PixImage circle(PixImage original, PixImage bnw)
    {
        PixImage result = new PixImage(original.red.length, original.red[0].length);

        int height = original.red.length;
        int width = original.red[0].length;

        int centerX = width / 2;
        int centerY = height / 2;

        double maxDistance = Math.sqrt(width * width + height*height);

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {

                int distance = (int) Math.sqrt((j-centerX)*(j-centerX)+(i-centerY)*(i-centerY));
                double percentage = distance / maxDistance;

                double newPerc = distance / (maxDistance*0.5);

                double gray = 1/(newPerc * (original.red[i][j] * 0.21 + 0.07 * original.blue[i][j] + 0.72 * original.green[i][j]) / 3);

                // all parts except for the center circle of the image will be a gradient grayscale
                if (percentage > 0.3) {
                    result.red[i][j] = (int) (gray + (1-newPerc) * 255);
                    result.blue[i][j] = (int) (gray + (1-newPerc) * 255);
                    result.green[i][j] = (int) (gray + (1-newPerc) * 255);
                }
                // the center circle will change to the closest color and add a mask 
                else if (i < bnw.red.length && j < bnw.red[0].length){
                    int [] cs = findClosestColor(original.red[i][j], original.green[i][j], original.blue[i][j]);
                    result.red[i][j] = cs[0] - (bnw.red[i][j] / 2);
                    result.green[i][j] = cs[1] - (bnw.green[i][j] / 2);
                    result.blue[i][j] = cs[2] - (bnw.blue[i][j] / 2);
                }
                // if the bnw mask is too small, fill the rest of the center circle with the
                // closest color
                else {
                    int [] cs = findClosestColor(original.red[i][j], original.green[i][j], original.blue[i][j]);
                    result.red[i][j] = cs[0];
                    result.green[i][j] = cs[1];
                    result.blue[i][j] = cs[2];
                }
                

            }
        }
        return result;
    }

    // replaces the colors with the nearest pastel color
    public static PixImage pastel(PixImage original) {
        PixImage result = new PixImage(original.red.length, original.red[0].length);

        int height = original.red.length;
        int width = original.red[0].length;

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                int [] cs = findClosestPastel(original.red[i][j], original.green[i][j], original.blue[i][j]);
                result.red[i][j] = cs[0];
                result.green[i][j] = cs[1];
                result.blue[i][j] = cs[2];

            }
        }
        return result;
    }

    // glitch filter that turns the red upside down and shifted over. the blue and green colors 
    // are shifted and mirrored but not flipped. the resulting image is 5 pixels longer and
    // 40 pixels wider than the original. works for any image.
    public static PixImage grayGlitch(PixImage original) {
        PixImage result = new PixImage(original.red.length+5,original.red[0].length+40);

        int height = original.red.length;
        int width = original.red[0].length;

        for (int i = 1; i < original.red.length; i++) {
            for(int j = 1; j < original.red[0].length; j++) {

                int gray = (int) (original.red[original.red.length-i][j] * 0.21 + 0.07 * original.blue[original.red.length-i][j] + 0.72 * original.green[original.red.length-i][j] / 3);

                result.blue[height-i][width-(j-10)] = gray;
                result.red[height-i][j+20] = original.red[i][j];
                result.green[height-i+2][width-j] = gray;
                
            }
        }

        return result;
    }

    public static int [] findClosestColor(int r, int g, int b)
    {
        int [][] colors = {{6, 212, 184},
                           {205, 205, 255},
                           {232, 232, 74},
                           {93, 134, 217},
                           };
        int closest = 0;
        float bestDist = (r - colors[0][0]) * (r - colors[0][0]) + 
                         (g - colors[0][1]) * (g - colors[0][1]) +
                         (b - colors[0][2]) * (b - colors[0][2]);
        for (int i = 1; i < colors.length; i++)
        {
            float d = (r - colors[i][0]) * (r - colors[i][0]) + 
                      (g - colors[i][1]) * (g - colors[i][1]) +
                      (b - colors[i][2]) * (b - colors[i][2]);
            if (d < bestDist)
            {
                closest = i;
                bestDist = d;
            }
        }
        return colors[closest];
    }

    public static int [] findClosestPastel(int r, int g, int b)
    {
        int [][] colors = {{170, 219, 162},
                           {164, 226, 237},
                           {202, 171, 235},
                           {245, 166, 216},
                           {240, 178, 108},
                           {164, 235, 190},
                           {247, 247, 247},
                           {255, 166, 166},
                           {166, 194, 255},
                           {158, 200, 247},
                           };
        int closest = 0;
        float bestDist = (r - colors[0][0]) * (r - colors[0][0]) + 
                         (g - colors[0][1]) * (g - colors[0][1]) +
                         (b - colors[0][2]) * (b - colors[0][2]);
        for (int i = 1; i < colors.length; i++)
        {
            float d = (r - colors[i][0]) * (r - colors[i][0]) + 
                      (g - colors[i][1]) * (g - colors[i][1]) +
                      (b - colors[i][2]) * (b - colors[i][2]);
            if (d < bestDist)
            {
                closest = i;
                bestDist = d;
            }
        }
        return colors[closest];
    }

}