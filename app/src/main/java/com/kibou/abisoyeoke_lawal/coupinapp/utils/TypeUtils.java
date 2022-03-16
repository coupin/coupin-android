package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import com.kibou.abisoyeoke_lawal.coupinapp.models.InnerItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.LocationV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TypeUtils {
    /**
     * Convert inner item into merchant v2
     * @param item
     * @param visited
     * @return
     */
    public static MerchantV2 convertInnerItemToMerchantV2(InnerItem item, boolean visited, boolean favourite) {
        MerchantV2 merchant = new MerchantV2();
        InnerItem.MerchantInfo merchantInfo = item.merchantInfo;

        merchant.banner = merchantInfo.banner;
        merchant.logo = merchantInfo.logo;
        merchant.address = merchantInfo.address;
        merchant.details = merchantInfo.companyDetails;
        merchant.email = item.email;
        merchant.mobile = merchantInfo.mobileNumber;
        merchant.title = merchantInfo.companyName;
        if (merchantInfo.rewards != null && merchantInfo.rewards.size() > 0) {
            merchant.reward = merchantInfo.rewards.get(0);
            merchant.rewards = merchantInfo.rewards;
            merchant.rewardsCount = merchantInfo.rewards.size();
        } else {
            merchant.rewardsCount = 0;
        }
        merchant.rating = merchantInfo.rating;
        merchant.location = new LocationV2();
        merchant.location.longitude = merchantInfo.location[0];
        merchant.location.latitude = merchantInfo.location[1];

        merchant.id = item.id;
        merchant.favourite = favourite;
        merchant.visited = visited;

        return merchant;
    }

    /**
     * Convert object to String
     * @param object serializable object
     * @return serialized string
     */
    public static String objectToString(Serializable object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new Base64OutputStream(byteArrayOutputStream, Base64.NO_PADDING
                            | Base64.NO_WRAP));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return  byteArrayOutputStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert String to object
     * @param string serialized object
     * @return deserialized object
     */
    public static Object stringToObject(String string) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(string.getBytes()), Base64.NO_PADDING
                    | Base64.NO_WRAP)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date stringToDate(String dateStr) {
        try {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH
            );
            Date dateFormat = simpleDateFormat1.parse(dateStr);
            assert dateFormat != null;
            return dateFormat;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
