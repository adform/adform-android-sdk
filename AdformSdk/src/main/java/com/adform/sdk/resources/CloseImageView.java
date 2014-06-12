package com.adform.sdk.resources;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by mariusm on 28/05/14.
 */
public class CloseImageView extends ImageView {
    public static final int CLOSE_IMAGE_DIMEN = 32;
    public static final int CLOSE_IMAGE_MARGIN = 18;
    private Context mContext;
    private Bitmap mBitmap;
    public static final String CLOSE_INTERSTITIAL_XXHDPI = "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";
    public static final String CLOSE_INTERSTITIAL_XHDPI = "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";
    public static final String CLOSE_INTERSTITIAL_HDPI = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3gUcCxouVmGoXQAABFFJREFUaN7tmT2IXFUUx387sxM3QjarS5zoCtpkjdklWogxMahFLCS9RVBCIljYaKmCBtEUURDWJZU2VmqXYBewC0oWbUQbwQ9MoZKgScjOi87sjM3/yN3jfTP3fcxuBC9c3uO9e8/9n+9z74X/eJsYE80G0NQToA+s6Tm4GRmYDAD3gG7OuJbGDcRQr46FqzBv0twObAMeAnYB83pf1P9vgC+B79S/Aq4Bv0dobajZ7QaWgB8FwPeueuzfD5q7e8wmva41pLVF4DPgigPVD+w81mP/r4jWomiPlYltwLNaeHWIdIv0rmgNgGe0xljaNHBeC92oAbjvRvO81qqttUVwNTCDwZi60b4uTbRTbHqUs2bA1WDsxAYEiKaiVFZ1zdtEJBuj1PN6BnSEoVS0mQYubAJw3y8IS6MI+AZwPMHm+zXa/bB/xwJcSe3hhGhzLZBQ0cj0p54rjtawsfuKONGKIkEe0at6tuVwbwV2mwp+WRLdkcDEdQkqyaH3CmB3hFrbwB4VaACvFDCJU5qzRaXEHSPmdsXg3hTpf5wgxU+ArcHcW/R8KcjSfk5Hz9c11s//MGHdj4ZpYRKYBS4mOt1OYCEigGOB2geOoRcjABakzZSgcFEYo1X0FPBgIiEr4u5ygIzwkUjx9nxgNmES3en8apTgHgg0/i8GjhYIj8bE4zl7jCPB2OcceGuPOlopDBx15rdOGsslY/i92sh4c3oBeC1iNvdLe2VyyVKsDLJI8kWJRGSqn3dAGxEB2b97Ckje988d5nURYVCy7jEmnopotum+PVkBfBZsPbfmMdCpWBIclj/5ditwqGIJ0vEMJNcWBTb5x1VWtJyJdoIgUXurqgErD046+5wI7N5n7KwODeDC27cVwL/tHHVC9t903wBersDE17GQbI72QUm7X3JSMdN8Wj38ZknoREl/eN9h/kcyU8qWqURN8u/GVKoEZ5n4sUjSBHijgCbCjD4Vq4emgP2JDBj405EktQe4O1JKzOXUTm8m7ieMzv5YJjYn2wFcTlTl6RyzaUfivL3f6caaJk4mCu6yMLbywuAkcCYB/DkHwNqBIUnKvh10c8wZP01Y90zK6d0jKoV7I4jNBtXoQlBVpmxo5mRqiMbtI9bqCdO+1Lzwfc6mxPvAjCuJU8qDP1wpPuNoxvqqMCW3gwmOdUMSfbVEbWNjT4hGlnDkeCAVvMXXd2o6GqljzqlY7B918TED/HoTHGz9IiylLmPmnd1uZLc1d1U9aN2+CUzYWtOjzoKGldODIMzNBBFnI5qtN+ewVL4Pu5QQ8sp2o3kJuK/uezMjMgu8F8TmtRqArwU5Z1lrjO0ewmqQw8DPLn73E0OjH5cBPwX76da47TMMZ08AZ4G/ci4nMvfuTeasaJS+t65DTU1VoPM6lj+kk4dYO6cr1RVdeP8mE6rl0rrK3XFLtPrSxrC2RXPs1Lm/2Qx4es3AFPouXPcCx/+/AfwN1dlOXbyUyFUAAAAASUVORK5CYII=";
    public static final String CLOSE_INTERSTITIAL_MDPI = "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";
    public static final String CLOSE_INTERSTITIAL_LDPI = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAKQWlDQ1BJQ0MgUHJvZmlsZQAASA2dlndUU9kWh8+9N73QEiIgJfQaegkg0jtIFQRRiUmAUAKGhCZ2RAVGFBEpVmRUwAFHhyJjRRQLg4Ji1wnyEFDGwVFEReXdjGsJ7601896a/cdZ39nnt9fZZ+9917oAUPyCBMJ0WAGANKFYFO7rwVwSE8vE9wIYEAEOWAHA4WZmBEf4RALU/L09mZmoSMaz9u4ugGS72yy/UCZz1v9/kSI3QyQGAApF1TY8fiYX5QKUU7PFGTL/BMr0lSkyhjEyFqEJoqwi48SvbPan5iu7yZiXJuShGlnOGbw0noy7UN6aJeGjjAShXJgl4GejfAdlvVRJmgDl9yjT0/icTAAwFJlfzOcmoWyJMkUUGe6J8gIACJTEObxyDov5OWieAHimZ+SKBIlJYqYR15hp5ejIZvrxs1P5YjErlMNN4Yh4TM/0tAyOMBeAr2+WRQElWW2ZaJHtrRzt7VnW5mj5v9nfHn5T/T3IevtV8Sbsz55BjJ5Z32zsrC+9FgD2JFqbHbO+lVUAtG0GQOXhrE/vIADyBQC03pzzHoZsXpLE4gwnC4vs7GxzAZ9rLivoN/ufgm/Kv4Y595nL7vtWO6YXP4EjSRUzZUXlpqemS0TMzAwOl89k/fcQ/+PAOWnNycMsnJ/AF/GF6FVR6JQJhIlou4U8gViQLmQKhH/V4X8YNicHGX6daxRodV8AfYU5ULhJB8hvPQBDIwMkbj96An3rWxAxCsi+vGitka9zjzJ6/uf6Hwtcim7hTEEiU+b2DI9kciWiLBmj34RswQISkAd0oAo0gS4wAixgDRyAM3AD3iAAhIBIEAOWAy5IAmlABLJBPtgACkEx2AF2g2pwANSBetAEToI2cAZcBFfADXALDIBHQAqGwUswAd6BaQiC8BAVokGqkBakD5lC1hAbWgh5Q0FQOBQDxUOJkBCSQPnQJqgYKoOqoUNQPfQjdBq6CF2D+qAH0CA0Bv0BfYQRmALTYQ3YALaA2bA7HAhHwsvgRHgVnAcXwNvhSrgWPg63whfhG/AALIVfwpMIQMgIA9FGWAgb8URCkFgkAREha5EipAKpRZqQDqQbuY1IkXHkAwaHoWGYGBbGGeOHWYzhYlZh1mJKMNWYY5hWTBfmNmYQM4H5gqVi1bGmWCesP3YJNhGbjS3EVmCPYFuwl7ED2GHsOxwOx8AZ4hxwfrgYXDJuNa4Etw/XjLuA68MN4SbxeLwq3hTvgg/Bc/BifCG+Cn8cfx7fjx/GvyeQCVoEa4IPIZYgJGwkVBAaCOcI/YQRwjRRgahPdCKGEHnEXGIpsY7YQbxJHCZOkxRJhiQXUiQpmbSBVElqIl0mPSa9IZPJOmRHchhZQF5PriSfIF8lD5I/UJQoJhRPShxFQtlOOUq5QHlAeUOlUg2obtRYqpi6nVpPvUR9Sn0vR5Mzl/OX48mtk6uRa5Xrl3slT5TXl3eXXy6fJ18hf0r+pvy4AlHBQMFTgaOwVqFG4bTCPYVJRZqilWKIYppiiWKD4jXFUSW8koGStxJPqUDpsNIlpSEaQtOledK4tE20Otpl2jAdRzek+9OT6cX0H+i99AllJWVb5SjlHOUa5bPKUgbCMGD4M1IZpYyTjLuMj/M05rnP48/bNq9pXv+8KZX5Km4qfJUilWaVAZWPqkxVb9UU1Z2qbapP1DBqJmphatlq+9Uuq43Pp893ns+dXzT/5PyH6rC6iXq4+mr1w+o96pMamhq+GhkaVRqXNMY1GZpumsma5ZrnNMe0aFoLtQRa5VrntV4wlZnuzFRmJbOLOaGtru2nLdE+pN2rPa1jqLNYZ6NOs84TXZIuWzdBt1y3U3dCT0svWC9fr1HvoT5Rn62fpL9Hv1t/ysDQINpgi0GbwaihiqG/YZ5ho+FjI6qRq9Eqo1qjO8Y4Y7ZxivE+41smsImdSZJJjclNU9jU3lRgus+0zwxr5mgmNKs1u8eisNxZWaxG1qA5wzzIfKN5m/krCz2LWIudFt0WXyztLFMt6ywfWSlZBVhttOqw+sPaxJprXWN9x4Zq42Ozzqbd5rWtqS3fdr/tfTuaXbDdFrtOu8/2DvYi+yb7MQc9h3iHvQ732HR2KLuEfdUR6+jhuM7xjOMHJ3snsdNJp9+dWc4pzg3OowsMF/AX1C0YctFx4bgccpEuZC6MX3hwodRV25XjWuv6zE3Xjed2xG3E3dg92f24+ysPSw+RR4vHlKeT5xrPC16Il69XkVevt5L3Yu9q76c+Oj6JPo0+E752vqt9L/hh/QL9dvrd89fw5/rX+08EOASsCegKpARGBFYHPgsyCRIFdQTDwQHBu4IfL9JfJFzUFgJC/EN2hTwJNQxdFfpzGC4sNKwm7Hm4VXh+eHcELWJFREPEu0iPyNLIR4uNFksWd0bJR8VF1UdNRXtFl0VLl1gsWbPkRoxajCCmPRYfGxV7JHZyqffS3UuH4+ziCuPuLjNclrPs2nK15anLz66QX8FZcSoeGx8d3xD/iRPCqeVMrvRfuXflBNeTu4f7kufGK+eN8V34ZfyRBJeEsoTRRJfEXYljSa5JFUnjAk9BteB1sl/ygeSplJCUoykzqdGpzWmEtPi000IlYYqwK10zPSe9L8M0ozBDuspp1e5VE6JA0ZFMKHNZZruYjv5M9UiMJJslg1kLs2qy3mdHZZ/KUcwR5vTkmuRuyx3J88n7fjVmNXd1Z752/ob8wTXuaw6thdauXNu5Tnddwbrh9b7rj20gbUjZ8MtGy41lG99uit7UUaBRsL5gaLPv5sZCuUJR4b0tzlsObMVsFWzt3WazrWrblyJe0fViy+KK4k8l3JLr31l9V/ndzPaE7b2l9qX7d+B2CHfc3em681iZYlle2dCu4F2t5czyovK3u1fsvlZhW3FgD2mPZI+0MqiyvUqvakfVp+qk6oEaj5rmvep7t+2d2sfb17/fbX/TAY0DxQc+HhQcvH/I91BrrUFtxWHc4azDz+ui6rq/Z39ff0TtSPGRz0eFR6XHwo911TvU1zeoN5Q2wo2SxrHjccdv/eD1Q3sTq+lQM6O5+AQ4ITnx4sf4H++eDDzZeYp9qukn/Z/2ttBailqh1tzWibakNml7THvf6YDTnR3OHS0/m/989Iz2mZqzymdLz5HOFZybOZ93fvJCxoXxi4kXhzpXdD66tOTSna6wrt7LgZevXvG5cqnbvfv8VZerZ645XTt9nX297Yb9jdYeu56WX+x+aem172296XCz/ZbjrY6+BX3n+l37L972un3ljv+dGwOLBvruLr57/17cPel93v3RB6kPXj/Mejj9aP1j7OOiJwpPKp6qP6391fjXZqm99Oyg12DPs4hnj4a4Qy//lfmvT8MFz6nPK0a0RupHrUfPjPmM3Xqx9MXwy4yX0+OFvyn+tveV0auffnf7vWdiycTwa9HrmT9K3qi+OfrW9m3nZOjk03dp76anit6rvj/2gf2h+2P0x5Hp7E/4T5WfjT93fAn88ngmbWbm3/eE8/syOll+AAAACXBIWXMAAAsTAAALEwEAmpwYAAADCUlEQVRIDYWVy2pUQRCGz8yZiTAkGS9gEPF+wQuCQkBFY7aajS6yEckbuNU30Jdw7RPkBSaiGzeK10TJHTciqLiay5nx+ztVTWfMMQ11qrvqr7+qu/qck2Xlo4IrL3dHjzDC7jjKHAoqLGIP+kye5ycHg8EF2SqVyqeiKFaYfkXasjHSmC1LydOrHq1Wq4+QD5B3kMGQdORDHsMzaly1Es5oDoBarXbdiFPSPgm6JppHn7D1ev3abkkCOYH3kmBV3kO2EZpfNvnS3d0dTuI9COdH5Tc455cG6qLrNu+jqzZ3ldoilv7c7PV6rwAFTgUpiRra7Pf7z9AaHiD9CxHOm840zGX7jaTYzDiahqkIJMk4x4dkP880DXjAbZlkV8vYVVHPJMe2ge8q6/tIjBGHuLBpBG5NGhiX7Gx1rgPW69hVicZR1u/NL98itlPBk2XjrFeHYpfwNcwf1BUAuiG6GWpeYfMWXk8yAdESot0cDlGQg2sZVjF+GbSjy4Zh73k+l4D8+oWdYH8B0JOo6rMWKPIFi3OsYr24OeHCGXGevl3dDB9+5lNUvYDxOKLqvyDHsLXQtxD1RVgfgcM5YxPcu5OmcYd4kfa5b2RkRDua8PX/dEgAwYaB0oQFthqVbHJbprrd7hvWRySdTucd11HvzIowiLA+nFOXJI7J5OziOXIM30CcM9QY67eIEo2Z7TTrVeuDn32Ixz9pmKAUvJw2jPUPPGlD/baIoIVvPERm2Qmw34di1atQhLajBv1BniMaYbscW5MgVd9Az6OnEX/Rps0m3yWw3h8/KnGJMzS/wkTjAJWsEaAK/Z1oY/s8ZIt+fIv42qkf2ypc+wNj8iMKmQDeMXAksXV6vvJJUpsXNOCDedvI06sbTJ5kNkmi6tKXyMldy+c7kG22jNzsW59ngDNsdRPtRNIi8x/OtqSGnTGSupOVad+avj1PkfWhRDEpvjXkCUQHjcxjI7c3OBoSoN+IJud6kRdLn+a95v8J8Wt+LB9Z65+gIXKPCYbdHkr+T0U7BAlTVmj2F1wgNAt/daF+AAAAAElFTkSuQmCC";

    public CloseImageView(Context context) {
        this(context, null);
    }

    public CloseImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CloseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setVisible(boolean isVisible) {
        if (isVisible) {
            setImageBitmap(getBitmapByDensity(mContext));
        } else {
            setImageBitmap(null);
        }
    }

    public RelativeLayout.LayoutParams getStandardLayoutParams() {
        int closeDimen = getDpUnits(CLOSE_IMAGE_DIMEN);
        int closeMargin = (getDpUnits(CLOSE_IMAGE_MARGIN)) / 2;

        final RelativeLayout.LayoutParams closeImageViewParams = new RelativeLayout.LayoutParams(closeDimen,
                closeDimen);
        closeImageViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        closeImageViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        closeImageViewParams.setMargins(closeMargin, closeMargin, closeMargin, closeMargin);
        return closeImageViewParams;
    }

    private Bitmap getBitmapByDensity(Context context) {
        if (mBitmap == null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            byte[] bytes;
            switch (metrics.densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    bytes = Base64.decode(CLOSE_INTERSTITIAL_LDPI, Base64.DEFAULT);
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                case DisplayMetrics.DENSITY_TV:
                    bytes = Base64.decode(CLOSE_INTERSTITIAL_HDPI, Base64.DEFAULT);
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                case DisplayMetrics.DENSITY_400:
                    bytes = Base64.decode(CLOSE_INTERSTITIAL_XHDPI, Base64.DEFAULT);
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                case DisplayMetrics.DENSITY_XXXHIGH:
                    bytes = Base64.decode(CLOSE_INTERSTITIAL_XXHDPI, Base64.DEFAULT);
                    break;
                default:
                    bytes = Base64.decode(CLOSE_INTERSTITIAL_MDPI, Base64.DEFAULT);
            }
            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return mBitmap;
    }

    public int getDpUnits(int px){
        Resources r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }
}
