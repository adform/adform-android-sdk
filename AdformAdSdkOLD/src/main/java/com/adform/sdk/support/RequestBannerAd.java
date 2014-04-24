package com.adform.sdk.support;

import com.adform.sdk.entities.AdEntity;
import com.adform.sdk.entities.AdServingEntity;
import com.adform.sdk.entities.TagDataEntity;
import com.adform.sdk.exceptions.RequestException;
import com.adform.sdk.utils.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;

public class RequestBannerAd extends RequestAd<AdServingEntity> {

	public RequestBannerAd() {
	}

	public RequestBannerAd(InputStream xmlArg) {
		is = xmlArg;
	}

	@Override
    AdServingEntity parse(final InputStream inputStream)
			throws RequestException {
		final AdServingEntity response = new AdServingEntity();
		try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(Strings.fromStream(inputStream));
            JSONObject jsonAdServing = (JSONObject)((JSONObject)obj).get("adServing");
            response.setAdEntity(new AdEntity());
            JSONObject jsonAdEntity = (JSONObject)jsonAdServing.get("ad");
            TagDataEntity tagDataEntity = new TagDataEntity();
            JSONObject jsonTagDataEntity = (JSONObject)jsonAdEntity.get("tagData");
            tagDataEntity.setSrc((String)jsonTagDataEntity.get("src"));
            response.getAdEntity().setTagDataEntity(tagDataEntity);
		} catch (final Exception e) {
			throw new RequestException("Cannot parse Response", e);
		}

		return response;
	}

	@Override
    AdServingEntity parseTestString() throws RequestException {
		return parse(is);
	}
}
